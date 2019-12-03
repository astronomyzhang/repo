package com.siemens.dasheng.web.poi;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.util.CollectionUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allan
 * @version 1.0
 * @date Created in 3:02 PM 7/24/2019
 * @Description ${description}
 */
public class ExcelXlsxReaderWithDefaultHandler extends DefaultHandler {

    private static final String COMMON_DATE = "MM/dd/yyyy HH:mm:ss";


    private static final int THREE = 3;

    private static final String BIG_B = "B";

    private static final String A_ = "A";
    private static final String B_ = "B";
    private static final String C_ = "C";
    private static final String D_ = "D";

    /**
     * 单元格中的数据可能的数据类型
     */
    enum CellDataType {
        /**
         * BOOL
         */
        BOOL,
        /**
         * ERROR
         */
        ERROR,
        /**
         * FORMULA
         */
        FORMULA,
        /**
         * str
         */
        INLINESTR,
        /**
         * SSTINDEX
         */
        SSTINDEX,
        /**
         * number
         */
        NUMBER,
        /**
         * date
         */
        DATE,
        /**
         * null
         */
        NULL
    }

    /**
     * C
     */
    private static final String C = "c";

    /**
     * T
     */
    private static final String T = "t";

    /**
     * V
     */
    private static final String V = "v";
    /**
     * row
     */
    private static final String ROW = "row";


    /**
     * b
     */
    private static final String B = "b";

    /**
     * e
     */
    private static final String E = "e";

    /**
     * s
     */
    private static final String S = "s";

    /**
     * inlineStr
     */
    private static final String INLINE_STR = "inlineStr";

    /**
     *
     */
    private static final String STR = "str";


    /**
     * 共享字符串表
     */
    private SharedStringsTable sst;

    /**
     * 上一次的索引值
     */
    private String lastIndex;

    /**
     * 工作表索引
     */
    private int sheetIndex = 0;

    /**
     * 总行数
     */
    private int totalRows = 0;

    /**
     * 一行内cell集合
     */
    private List<String> cellList = new ArrayList<>();

    /**
     * 判断整行是否为空行的标记
     */
    private boolean flag = false;

    /**
     * 当前行
     */
    private int curRow = 1;

    /**
     * 当前列
     */
    private int curCol = 0;

    /**
     * T元素标识
     */
    private boolean isTElement;

    /**
     * 判断上一单元格是否为文本空单元格
     */
    private boolean endElementFlag = false;

    /**
     * 单元格数据类型，默认为字符串类型
     */
    private CellDataType nextDataType = CellDataType.SSTINDEX;

    private final DataFormatter formatter = new DataFormatter();

    /**
     * 单元格日期格式的索引
     */
    private short formatIndex;

    /**
     * 日期格式字符串
     */
    private String formatString;

    //定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等

    private String preRef = null, ref = null;

    //定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格


    /**
     * 单元格
     */
    private StylesTable stylesTable;


    /**
     * 数据
     */
    private List<List<String>> valueList = null;


    /**
     * 遍历工作簿中所有的电子表格
     * 并缓存在mySheetList中
     *
     * @param in
     * @throws Exception
     */
    @SuppressWarnings("all")
    public int process(InputStream in, List<List<String>> list) throws Exception {
        valueList = list;
        OPCPackage pkg = OPCPackage.open(in);
        XSSFReader xssfReader = new XSSFReader(pkg);
        stylesTable = xssfReader.getStylesTable();
        SharedStringsTable sst = xssfReader.getSharedStringsTable();
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        this.sst = sst;
        parser.setContentHandler(this);
        XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        //遍历sheet
        while (sheets.hasNext()) {
            //标记初始行为第一行
            curRow = 1;
            sheetIndex++;
            //sheets.next()和sheets.getSheetName()不能换位置，否则sheetName报错
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            //解析excel的每条记录，在这个过程中startElement()、characters()、endElement()这三个函数会依次执行
            parser.parse(sheetSource);
            sheet.close();
        }
        //返回该excel文件的总行数，不包括首列和空行
        return totalRows;
    }

    /**
     * 第一个执行
     *
     * @param uri
     * @param localName
     * @param name
     * @param attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        //c => 单元格
        if (C.equals(name)) {


            //前一个单元格的位置
            if (preRef == null) {
                preRef = attributes.getValue("r");
            } else {
                //判断前一次是否为文本空字符串，true则表明不是文本空字符串，false表明是文本空字符串跳过把空字符串的位置赋予preRef
                if (endElementFlag) {
                    preRef = ref;
                }
            }

            //当前单元格的位置
            ref = attributes.getValue("r");
            //设定单元格类型
            this.setNextDataType(attributes);
            endElementFlag = false;
        }

        //当元素为t时
        if (T.equals(name)) {
            isTElement = true;
        } else {
            isTElement = false;
        }

        //置空
        lastIndex = "";
    }


    /**
     * 第二个执行
     * 得到单元格对应的索引值或是内容值
     * 如果单元格类型是字符串、INLINESTR、数字、日期，lastIndex则是索引值
     * 如果单元格类型是布尔值、错误、公式，lastIndex则是内容值
     *
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        lastIndex += new String(ch, start, length);
    }

    /**
     * 第三个执行
     *
     * @param uri
     * @param localName
     * @param name
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        //t元素也包含字符串
        //这个程序没经过
        if (isTElement) {
            //将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
            String value = lastIndex.trim();
            cellList.add(curCol, value);
            endElementFlag = true;
            curCol++;
            isTElement = false;
            //如果里面某个单元格含有值，则标识该行不为空行
            if (value != null && !"".equals(value)) {
                flag = true;
            }
        } else if (V.equals(name)) {
            if (curCol == 0 && curRow != 1) {
                cellList.add(0, "");
                cellList.add(1, "");
                cellList.add(2, "");
                cellList.add(3, "");
            }


            //v => 单元格的值，如果单元格是字符串，则v标签的值为该字符串在SST中的索引
            //根据索引值获取对应的单元格值
            String value = this.getDataValue(lastIndex.trim(), "");
            endElementFlag = true;
            //如果里面某个单元格含有值，则标识该行不为空行
            if (value != null && !"".equals(value)) {
                flag = true;
            }
            //补全单元格之间的空单元格
            if (flag && curRow != 1) {
                if (ref.contains(A_)) {
                    cellList.set(0, value);
                } else if (ref.contains(B_)) {
                    cellList.set(1, value);
                } else if (ref.contains(C_)) {
                    cellList.set(2, value);
                } else if (ref.contains(D_)) {
                    cellList.set(3, value);
                }
            }
            curCol++;
        } else {
            //如果标签名称为row，这说明已到行尾，调用optRows()方法
            if (ROW.equals(name)) {
                //默认第一行为表头，以该行单元格数目为最大数目
                //该行不为空行且该行不是第一行，则发送（第一行为列名，不需要）
                if (flag && curRow != 1) {
                    List<String> tempList = new ArrayList<>();
                    for (String d : cellList) {
                        tempList.add(d);
                    }
                    if (!CollectionUtils.isEmpty(tempList)) {
                        valueList.add(tempList);
                    }
                    totalRows++;
                }

                cellList.clear();
                curRow++;
                curCol = 0;
                preRef = null;
                ref = null;
                flag = false;
            }
        }
    }

    /**
     * 处理数据类型
     *
     * @param attributes
     */
    public void setNextDataType(Attributes attributes) {
        //cellType为空，则表示该单元格类型为数字
        nextDataType = CellDataType.NUMBER;
        formatIndex = -1;
        formatString = null;
        //单元格类型
        String cellType = attributes.getValue("t");
        String cellStyleStr = attributes.getValue("s");
        String columnData = attributes.getValue("r");
        //处理布尔值
        if (B.equals(cellType)) {
            nextDataType = CellDataType.BOOL;
            //处理错误
        } else if (E.equals(cellType)) {
            nextDataType = CellDataType.ERROR;
        } else if (INLINE_STR.equals(cellType)) {
            nextDataType = CellDataType.INLINESTR;
            //处理字符串
        } else if (S.equals(cellType)) {
            nextDataType = CellDataType.SSTINDEX;
        } else if (STR.equals(cellType)) {
            nextDataType = CellDataType.FORMULA;
        }

        //处理日期
        if (cellStyleStr != null) {
            int styleIndex = Integer.parseInt(cellStyleStr);
            XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
            formatIndex = style.getDataFormat();
            formatString = style.getDataFormatString();
            if (columnData.contains(BIG_B)) {
                nextDataType = CellDataType.DATE;
                formatString = COMMON_DATE;
            }
        }
    }

    /**
     * 对解析出来的数据进行类型处理
     *
     * @param value   单元格的值，
     *                value代表解析：BOOL的为0或1， ERROR的为内容值，FORMULA的为内容值，INLINESTR的为索引值需转换为内容值，
     *                SSTINDEX的为索引值需转换为内容值， NUMBER为内容值，DATE为内容值
     * @param thisStr 一个空字符串
     * @return
     */
    @SuppressWarnings("deprecation")
    public String getDataValue(String value, String thisStr) {
        switch (nextDataType) {
            // 这几个的顺序不能随便交换，交换了很可能会导致数据错误
            //布尔值
            case BOOL:
                char first = value.charAt(0);
                thisStr = first == '0' ? "FALSE" : "TRUE";
                break;
            //错误
            case ERROR:
                thisStr = "\"ERROR:" + value.toString() + '"';
                break;
            //公式
            case FORMULA:
                thisStr = '"' + value.toString() + '"';
                break;
            case INLINESTR:
                XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                thisStr = rtsi.toString();
                rtsi = null;
                break;
            //字符串
            case SSTINDEX:
                String sstIndex = value.toString();
                try {
                    int idx = Integer.parseInt(sstIndex);
                    //根据idx索引值获取内容值
                    XSSFRichTextString rtss = new XSSFRichTextString(sst.getEntryAt(idx));
                    thisStr = rtss.toString();
                    //有些字符串是文本格式的，但内容却是日期
                    rtss = null;
                } catch (NumberFormatException ex) {
                    thisStr = value.toString();
                }
                break;
            //数字
            case NUMBER:
                if (formatString != null) {
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString).trim();
                } else {
                    thisStr = value;
                }
                thisStr = thisStr.replace("_", "").trim();
                break;
            //日期
            case DATE:
                thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, formatString);
                // 对日期字符串作特殊处理，去掉T
                thisStr = thisStr.replace("T", " ");
                break;
            default:
                thisStr = " ";
                break;
        }
        return thisStr;
    }

    /**
     * countNullCell
     *
     * @param ref
     * @param preRef
     * @return
     */
    public int countNullCell(String ref, String preRef) {
        //excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
        String strOne = ref.replaceAll("\\d+", "");
        String strTwo = preRef.replaceAll("\\d+", "");

        strOne = fillChar(strOne, 3, '@', true);
        strTwo = fillChar(strTwo, 3, '@', true);

        char[] letter = strOne.toCharArray();
        char[] letter1 = strTwo.toCharArray();
        int res = (letter[0] - letter1[0]) * 26 * 26 + (letter[1] - letter1[1]) * 26 + (letter[2] - letter1[2]);
        return res - 1;
    }

    /**
     * fillChar
     *
     * @param str
     * @param len
     * @param let
     * @param isPre
     * @return
     */
    public String fillChar(String str, int len, char let, boolean isPre) {
        StringBuilder stringBuilder = new StringBuilder();
        int len1 = str.length();
        if (len1 < len) {
            if (isPre) {
                for (int i = 0; i < (len - len1); i++) {
                    stringBuilder.append(let);
                    stringBuilder.append(str);
                }
            } else {
                for (int i = 0; i < (len - len1); i++) {
                    stringBuilder.append(str);
                    stringBuilder.append(let);
                }
            }
        }
        return stringBuilder.toString();
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
}
