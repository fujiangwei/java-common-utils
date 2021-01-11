package com.common.util.excel.example;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 文件描述
 *
 * @author
 * @date
 **/
public class Main {
    private static final String DIC_PATH = "C:\\Users\\admin\\Desktop\\";
    private static final String FILE_NAME_GB = "xxx.xls";

    public static void main(String[] args) throws Exception {

        // xlsxEventReader();
        xlsEventReader();
    }

    /**
     * 通过事件响应机制读取数据量较大的xlsx文件
     *
     * @throws Exception
     */
    public static void xlsxEventReader() throws Exception {
        Map<Integer, List<Map<String, String>>> dataMap = new HashMap<>();
        OPCPackage pkg = null;
        InputStream in = new FileInputStream("C:\\Users\\admin\\Desktop\\xxx.xlsx");
        pkg = OPCPackage.open(in);
        XSSFReader xssfReader = new XSSFReader(pkg);
        StylesTable styles = xssfReader.getStylesTable();
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
        Iterator<InputStream> iterators = xssfReader.getSheetsData();
        int sheetCount = 1;
        // 遍历所有的sheet页
        while (iterators.hasNext()) {
            SaxSheetContentsHandler sheetHandler = new SaxSheetContentsHandler();
            // 开始解析，SaxSheetContentsHandler是实现了XSSFSheetXMLHandler.SheetContentsHandler接口的自定义类
            ProcessExcelUtil.processSheet(styles, strings, iterators.next(), sheetHandler);
            sheetCount = sheetCount + 1;
        }
    }

    /**
     * xls event
     *
     * @throws Exception
     */
    public static void xlsEventReader() throws Exception {
        MyXLSReader myXLSReader = new MyXLSReader();
        myXLSReader.read(DIC_PATH + "xxx.xls");
    }
}
