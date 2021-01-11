package com.common.util.excel.example;

import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述
 *
 * @author
 * @date
 **/
public class MyXLSReader implements HSSFListener {
    private boolean sheetPrased = false;
    private List<List<String>> totalData = new ArrayList<>(1024 * 64);
    private List<String> currentRowData = new ArrayList<>();
    private int currentRowIndex = -1;
    private int currentColIndex = -1;
    private SSTRecord sstRecord;

    private String filePath;
    private POIFSFileSystem fs;
    private FormatTrackingHSSFListener formatListener;

    /**
     * process an HSSF Record. Called when a record occurs in an HSSF file.
     *
     * @param record the record to be processed
     */
    @Override
    public void processRecord(Record record) {
        switch (record.getSid()) {
            case BoundSheetRecord.sid: {
                if (sheetPrased == false) {
                    sheetPrased = true;
                } else {
                    System.out.println("error:解析到不止一个sheet页");
                }
                break;
            }
            case BOFRecord.sid: {
                break;
            }
            case SSTRecord.sid: {
                sstRecord = (SSTRecord) record;
                //System.out.println(sstRecord);
                break;
            }
            case BlankRecord.sid: {
                BlankRecord brec = (BlankRecord) record;
                String value = "";
                this.currentColIndex = brec.getColumn();
                this.currentRowIndex = brec.getRow();
                break;
            }
            case FormulaRecord.sid: {
                FormulaRecord frec = (FormulaRecord) record;
                String value = String.valueOf(frec.getValue());
                this.currentColIndex = frec.getColumn();
                this.currentRowIndex = frec.getRow();
                break;
            }
            case StringRecord.sid: {
                StringRecord srec = (StringRecord) record;
                String value = srec.getString();
//                this.currentColIndex = srec.get;
//                this.currentRowIndex = srec.getRow();
                break;
            }
            case LabelRecord.sid: {
                LabelRecord lrec = (LabelRecord) record;
                String value = lrec.getValue();
                this.currentColIndex = lrec.getColumn();
                this.currentRowIndex = lrec.getRow();
                break;
            }
            case LabelSSTRecord.sid: {
                LabelSSTRecord lsrec = (LabelSSTRecord) record;
                this.currentRowIndex = lsrec.getRow();
                this.currentColIndex = lsrec.getColumn();
                if (sstRecord == null) {
                    this.currentRowData.add("");
                } else {
                    String value = sstRecord.getString(lsrec.getSSTIndex()).toString().trim();
                    value = value.equals("") ? "" : value;
                    this.currentRowData.add(value);
                }
                break;
            }
            case NumberRecord.sid: {
                NumberRecord numrec = (NumberRecord) record;
                String value = String.valueOf(numrec.getValue());
                this.currentColIndex = numrec.getColumn();
                this.currentRowIndex = numrec.getRow();
                break;
            }
            case BoolErrRecord.sid: {
                BoolErrRecord berec = (BoolErrRecord) record;
                this.currentColIndex = berec.getColumn();
                this.currentRowIndex = berec.getRow();
                break;
            }
            default: {
                break;
            }
        }

        if (record instanceof MissingCellDummyRecord) {
            this.currentRowData.add("");
        }

        if (record instanceof LastCellOfRowDummyRecord) {
            if (this.currentRowIndex >= 0) {
                LastCellOfRowDummyRecord mc = (LastCellOfRowDummyRecord) record;
                this.totalData.add(this.currentRowData);
            }
            this.currentRowData = new ArrayList<>();
        }
    }

    public List<List<String>> read(String fileName) throws Exception {
        filePath = fileName;
        this.fs = new POIFSFileSystem(new FileInputStream(fileName));
        // this类是自定义的HSSFListener类
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(formatListener);
        // 开始执行解析
        factory.processWorkbookEvents(request, fs);
        return totalData;
    }
}
