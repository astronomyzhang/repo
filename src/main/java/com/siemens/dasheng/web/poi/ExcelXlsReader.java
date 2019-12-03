package com.siemens.dasheng.web.poi;

import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allan
 * @version 1.0
 * @date Created in 3:06 PM 7/24/2019
 * @Description ${description}
 */
@SuppressWarnings("all")
public class ExcelXlsReader implements HSSFListener {

    private static final String COMMON_DATE = "MM/dd/yyyy HH:mm:ss";

    private int minColums = -1;

    private static final int ONE = 1;

    private POIFSFileSystem fs;

    /**
     * 总行数
     */
    private int totalRows = 0;

    /**
     * 上一行row的序号
     */
    private int lastRowNumber;

    /**
     * 上一单元格的序号
     */
    private int lastColumnNumber;

    /**
     * 是否输出formula，还是它对应的值
     */
    private boolean outputFormulaValues = true;

    /**
     * 用于转换formulas
     */
    private EventWorkbookBuilder.SheetRecordCollectingListener workbookBuildingListener;

    /**
     * excel2003工作簿
     */
    private HSSFWorkbook stubWorkbook;

    private SSTRecord sstRecord;

    private FormatTrackingHSSFListener formatListener;

    private final HSSFDataFormatter formatter = new HSSFDataFormatter();

    private final DataFormatter dataFormatter = new DataFormatter();

    /**
     * 表索引
     */
    private int sheetIndex = 0;

    private BoundSheetRecord[] orderedBSRs;

    @SuppressWarnings("unchecked")
    private ArrayList boundSheetRecords = new ArrayList();

    private int nextRow;

    private int nextColumn;

    private boolean outputNextStringRecord;

    /**
     * 当前行
     */
    private int curRow = 0;

    /**
     * 存储一行记录所有单元格的容器
     */
    private List<String> cellList = new ArrayList<String>();

    /**
     * 判断整行是否为空行的标记
     */
    private boolean flag = false;

    /**
     * 数据
     */
    private List<List<String>> valueList = null;

    /**
     * 遍历excel下所有的sheet
     *
     * @param in
     * @throws Exception
     */
    public int process(InputStream in, List<List<String>> resultList) throws Exception {
        valueList = resultList;
        this.fs = new POIFSFileSystem(in);
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatListener);
        } else {
            workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
            request.addListenerForAllRecords(workbookBuildingListener);
        }
        factory.processWorkbookEvents(request, fs);
        //返回该excel文件的总行数，不包括首列和空行
        return totalRows;
    }

    /**
     * HSSFListener 监听方法，处理Record
     * 处理每个单元格
     *
     * @param record
     */
    @SuppressWarnings("unchecked")
    @Override
    public void processRecord(Record record) {
        int thisRow = -1;
        int thisColumn = -1;
        String thisStr = null;
        String value = null;

        switch (record.getSid()) {
            case BoundSheetRecord.sid:
                boundSheetRecords.add(record);
                break;
            //开始处理每个sheet
            case BOFRecord.sid:
                BOFRecord br = (BOFRecord) record;
                if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
                    //如果有需要，则建立子工作簿
                    if (workbookBuildingListener != null && stubWorkbook == null) {
                        stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
                    }

                    if (orderedBSRs == null) {
                        orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                    }
                    sheetIndex++;
                }
                break;
            case SSTRecord.sid:
                sstRecord = (SSTRecord) record;
                cellList.add(0, "");
                cellList.add(1, "");
                cellList.add(2, "");
                cellList.add(3, "");
                break;
            //单元格为空白
            case BlankRecord.sid:
                BlankRecord brec = (BlankRecord) record;
                thisRow = brec.getRow();
                thisColumn = brec.getColumn();
                if (thisColumn == 0) {
                    cellList.add(0, "");
                    cellList.add(1, "");
                    cellList.add(2, "");
                    cellList.add(3, "");
                }
                thisStr = "";
                cellList.set(thisColumn, thisStr);
                break;
            //单元格为布尔类型
            case BoolErrRecord.sid:
                BoolErrRecord berec = (BoolErrRecord) record;
                thisRow = berec.getRow();
                thisColumn = berec.getColumn();
                if (thisColumn == 0) {
                    cellList.add(0, "");
                    cellList.add(1, "");
                    cellList.add(2, "");
                    cellList.add(3, "");
                }
                thisStr = berec.getBooleanValue() + "";
                cellList.set(thisColumn, thisStr);
                //如果里面某个单元格含有值，则标识该行不为空行
                checkRowIsNull(thisStr);
                break;
            //单元格为公式类型
            case FormulaRecord.sid:
                FormulaRecord frec = (FormulaRecord) record;
                thisRow = frec.getRow();
                thisColumn = frec.getColumn();
                if (thisColumn == 0) {
                    cellList.add(0, "");
                    cellList.add(1, "");
                    cellList.add(2, "");
                    cellList.add(3, "");
                }
                if (outputFormulaValues) {
                    if (Double.isNaN(frec.getValue())) {
                        outputNextStringRecord = true;
                        nextRow = frec.getRow();
                        nextColumn = frec.getColumn();
                    } else {
                        thisStr = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression()) + '"';
                    }
                } else {
                    thisStr = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook, frec.getParsedExpression()) + '"';
                }
                cellList.set(thisColumn, thisStr);
                //如果里面某个单元格含有值，则标识该行不为空行
                checkRowIsNull(thisStr);
                break;
            //单元格中公式的字符串
            case StringRecord.sid:
                if (outputNextStringRecord) {
                    StringRecord srec = (StringRecord) record;
                    thisStr = srec.getString();
                    thisRow = nextRow;
                    thisColumn = nextColumn;
                    if (thisColumn == 0) {
                        cellList.add(0, "");
                        cellList.add(1, "");
                        cellList.add(2, "");
                        cellList.add(3, "");
                    }
                    outputNextStringRecord = false;
                }
                break;
            case LabelRecord.sid:
                LabelRecord lrec = (LabelRecord) record;
                curRow = thisRow = lrec.getRow();
                thisColumn = lrec.getColumn();
                if (thisColumn == 0) {
                    cellList.add(0, "");
                    cellList.add(1, "");
                    cellList.add(2, "");
                    cellList.add(3, "");
                }
                value = lrec.getValue().trim();
                value = value.equals("") ? "" : value;
                cellList.set(thisColumn, value);
                //如果里面某个单元格含有值，则标识该行不为空行
                checkRowIsNull(value);
                break;
            //单元格为字符串类型
            case LabelSSTRecord.sid:
                LabelSSTRecord lsrec = (LabelSSTRecord) record;
                curRow = thisRow = lsrec.getRow();
                thisColumn = lsrec.getColumn();
                if (thisColumn == 0) {
                    cellList.add(0, "");
                    cellList.add(1, "");
                    cellList.add(2, "");
                    cellList.add(3, "");
                }
                if (sstRecord == null) {
                    cellList.set(thisColumn, "");
                } else {
                    value = sstRecord.getString(lsrec.getSSTIndex()).toString().trim();
                    value = value.equals("") ? "" : value;
                    cellList.set(thisColumn, value);
                    //如果里面某个单元格含有值，则标识该行不为空行
                    checkRowIsNull(value);
                }
                break;
            //单元格为数字类型
            case NumberRecord.sid:
                NumberRecord numrec = (NumberRecord) record;
                curRow = thisRow = numrec.getRow();
                thisColumn = numrec.getColumn();
                if (thisColumn == 0) {
                    cellList.add(0, "");
                    cellList.add(1, "");
                    cellList.add(2, "");
                    cellList.add(3, "");
                }

                //第二种方式，参照formatNumberDateCell里面的实现方法编写
                Double valueDouble = ((NumberRecord) numrec).getValue();
                String formatString = formatListener.getFormatString(numrec);
                if (thisColumn == ONE) {
                    formatString = COMMON_DATE;
                }
                int formatIndex = formatListener.getFormatIndex(numrec);
                value = formatter.formatRawCellContents(valueDouble, formatIndex, formatString).trim();

                value = value.equals("") ? "" : value;
                //向容器加入列值
                cellList.set(thisColumn, value);
                //如果里面某个单元格含有值，则标识该行不为空行
                checkRowIsNull(value);
                break;
            default:
                System.out.println("1");
                break;
        }

        //遇到新行的操作
        if (thisRow != -1 && thisRow != lastRowNumber) {
            lastColumnNumber = -1;
        }

        //空值的操作
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            curRow = thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            if (thisColumn == 0) {
                cellList.add(0, "");
                cellList.add(1, "");
                cellList.add(2, "");
                cellList.add(3, "");
            }
            cellList.set(thisColumn, "");
        }

        //更新行和列的值
        if (thisRow > -1) {
            lastRowNumber = thisRow;
        }

        if (thisColumn > -1) {
            lastColumnNumber = thisColumn;
        }

        //行结束时的操作
        if (record instanceof LastCellOfRowDummyRecord) {
            if (minColums > 0) {
                //列值重新置空
                if (lastColumnNumber == -1) {
                    lastColumnNumber = 0;
                }
            }
            lastColumnNumber = -1;

            //该行不为空行且该行不是第一行，发送（第一行为列名，不需要）
            if (flag && curRow != 0) {
                List<String> tempList = new ArrayList<>();
                for (String d : cellList) {
                    tempList.add(d);
                }
                if (!CollectionUtils.isEmpty(tempList)) {
                    valueList.add(tempList);
                }
                totalRows++;
            }
            //清空容器
            cellList.clear();
            flag = false;
        }
    }


    /**
     * 如果里面某个单元格含有值，则标识该行不为空行
     *
     * @param value
     */
    public void checkRowIsNull(String value) {
        if (value != null && !"".equals(value)) {
            flag = true;
        }
    }
}
