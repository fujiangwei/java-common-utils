package com.common.util.excel.example;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述
 *
 * @author
 * @date
 **/
public class SaxSheetContentsHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

    private long startTime = 0;

    /**
     * 数据单元
     */
    private List<List<String>> totalData = new ArrayList<>(1024 * 128);

    /**
     * 数据记录，按行记录
     */
    private List<String> currentRow = null;


    public SaxSheetContentsHandler() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void startRow(int rowNum) {
        currentRow = new ArrayList<>();
    }

    @Override
    public void endRow(int rowNum) {
        totalData.add(currentRow);
        currentRow = null;
    }

    /**
     * @param cellReference  列名   例如A,B,C等
     * @param formattedValue 单元格值 对应每个单元格的值, [^A-Za-z]:匹配所有非字母的都用""
     *                       代替,因为多行Excel的列明是：A1，B1，C1，....D10等，匹配替换后每行的列名就是A,B,C等。
     *                       此方法用解析每一个cell的值
     * @param comment
     */
    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        currentRow.add(formattedValue);
    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
        System.out.println("??");
    }

    /**
     * Signal that the end of a sheet was been reached
     */
    @Override
    public void endSheet() {
        long costTime = System.currentTimeMillis() - startTime;
        System.out.println("sheet页解析完毕，共" + totalData.size() + "条数据，耗时" + costTime + "ms");
    }
}
