package com.siemens.dasheng.web.util;

import com.siemens.dasheng.web.poi.ExcelXlsReader;
import com.siemens.dasheng.web.poi.ExcelXlsxReaderWithDefaultHandler;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liming
 * @Date: 2019/1/21 10:39
 */
@Component
public class OfflineDataAnalysisUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static Integer CELLNO = 3;

    private final static String PCELLLSTR = "(\"[^\"]{0,1000}(\"{2}){0,1000}[^\"]{0,1000}\"){0,1000}[^,]{0,1000},";

    private static final String COMMON_DATE = "MM/dd/yyyy HH:mm:ss";

    private static final String POINT0 = ".0";

    private static final int SIXTY_ONE = 61;

    public List<List<String>> getListData(MultipartFile file) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(COMMON_DATE);
        InputStream in = file.getInputStream();
        List<List<String>> list = null;
        if (null != in) {
            try {
                String originalFilename = file.getOriginalFilename();
                //解析excel
               // list = getLists(in, originalFilename, sdf);
                list = exportData(in, sdf);
            } catch (Exception e) {
                logger.error("getListData errors {} ", e.getMessage());
                list = new ArrayList<>();
                String line;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                int lineNum = 0;
                Pattern pcells = Pattern.compile(PCELLLSTR);

                try {
                    while ((line = bufferedReader.readLine()) != null) {

                        if (lineNum == 0) {
                            //表头信息
                        } else {
                            String str;
                            line += ",";
                            Matcher mCells = pcells.matcher(line);
                            //每行记录一个list
                            List cells = new LinkedList();
                            //读取每个单元格
                            while (mCells.find()) {
                                str = mCells.group();
                                str = str.replaceAll(
                                        "(?sm)\"?([^\"]*(\"{2})*[^\"]*)\"?.*,", "$1");
                                str = str.replaceAll("(?sm)(\"(\"))", "$2");
                                cells.add(str);
                            }
                            //从第2行起的数据信息list
                            list.add(cells);
                        }
                        lineNum++;
                    }
                } catch (IOException e1) {
                    throw new IllegalArgumentException("Can't get data from this csv file.");
                } finally {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                }

            } finally {
                in.close();
            }
        }
        return list;
    }

    private List<List<String>> getLists(InputStream in, String originalFilename, SimpleDateFormat sdf) throws Exception {
        List<List<String>> list;
        //创建Excel工作薄
        Workbook work = getWorkbook(in, originalFilename);
        if (null == work) {
            throw new Exception("Can't create excel sheet.");
        }

        Sheet sheet;
        Row row;
        Cell cell;

        NumberFormat nf = NumberFormat.getInstance();
        list = new ArrayList<>();
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }

            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum() + 1; j <= sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }

                //遍历所有的列
                List<String> li = new ArrayList<>();
                for (int y = 0; y < CELLNO; y++) {
                    cell = row.getCell(y);
                    if (cell != null && (!"".equals(cell.toString()))) {
                        li.add(getCellValue(cell, sdf, nf));
                    } else {
                        li.add("");
                    }
                }
                list.add(li);

            }
        }
        return list;
    }

    /**
     * 解析excel数据采取流方式
     * @param in
     * @return
     */
    public  List<List<String>> exportData(InputStream in, SimpleDateFormat sdf) throws  Exception{
        List<List<String>> resultList = new LinkedList<>();
        if(!in.markSupported()) {
            in = new PushbackInputStream(in, 8);
        }

        byte[] header8 = IOUtils.peekFirst8Bytes(in);
        if(NPOIFSFileSystem.hasPOIFSHeader(header8)) {
            ExcelXlsReader excelXls = new ExcelXlsReader();
            excelXls.process(in, resultList);

        } else if(POIXMLDocument.hasOOXMLHeader(in)) {
            ExcelXlsxReaderWithDefaultHandler excelXlsxReader = new ExcelXlsxReaderWithDefaultHandler();
            excelXlsxReader.process(in, resultList);
        } else {
            throw new InvalidFormatException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
        }

        return resultList;

    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     *
     * @param inStr,fileName
     * @return
     */
    public static Workbook getWorkbook(InputStream inStr, String fileName) {
        try {
            return WorkbookFactory.create(inStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Excel version not supported.");
        }
    }


    /**
     * 描述：对表格中数值进行格式化
     *
     * @param
     * @return
     */

    public static String getCellValue(Cell cell, SimpleDateFormat sdf, NumberFormat nf) {
        String cellValue = null;
        // 判断数据的类型
        switch (cell.getCellType()) {
            // 数字
            case Cell.CELL_TYPE_NUMERIC:
                // 处理日期格式、时间格式
                if (DateUtil.isCellDateFormatted(cell)) {
                    try {
                        cellValue = sdf.format(cell.getDateCellValue());
                    } catch (Exception e) {

                    }
                } else {
                    cellValue = new BigDecimal(Double.toString(cell.getNumericCellValue())).toPlainString();
                    if (cellValue.endsWith(POINT0)) {
                        cellValue = cellValue.replace(".0", "");
                    }
                }
                break;
            case Cell.CELL_TYPE_STRING:
                // 字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                // Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                // 公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK:
                // 空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR:
                // 故障
                cellValue = "";
                break;
            default:
                cellValue = "";
                break;
        }
        return cellValue;

    }


}
