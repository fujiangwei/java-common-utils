package com.common.util.excel.example;

import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author
 * @Description: Process excel util
 * @date
 */
public class ProcessExcelUtil {
    /**
     * 解析每个sheet页数据
     *
     * @param styles
     * @param strings
     * @param sheetInputStream
     * @param saxSheetContentsHandler
     * @throws Exception
     */
    public static void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, InputStream sheetInputStream, SaxSheetContentsHandler saxSheetContentsHandler)
            throws Exception {
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            sheetParser.setContentHandler(new XSSFSheetXMLHandler(styles, strings, saxSheetContentsHandler, false));
            sheetParser.parse(new InputSource(sheetInputStream));
        } catch (Exception e) {
            throw e;
        } finally {
            if (Objects.nonNull(sheetInputStream)) {
                sheetInputStream.close();
            }
        }
    }

    /**
     * @param args args
     * @throws Exception exception
     */
    public static void main(String[] args) throws Exception {
        Map<Integer, List<Map<String, String>>> dataMap = new HashMap<>();
        OPCPackage pkg = null;
        InputStream in = new FileInputStream("C:\\Users\\admin\\Desktop\\xxx.xlsx");
        pkg = OPCPackage.open(in);
        XSSFReader xssfReader = new XSSFReader(pkg);
        StylesTable styles = xssfReader.getStylesTable();
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
        Iterator<InputStream> iterators = xssfReader.getSheetsData();
        int sheetCount = 1;
        while (iterators.hasNext()) {
            List<Map<String, String>> dataListMap = new ArrayList<>();
            SaxSheetContentsHandler sheetHandler = new SaxSheetContentsHandler();
            processSheet(styles, strings, iterators.next(), sheetHandler);
            dataMap.put(Integer.valueOf(sheetCount), dataListMap);
            sheetCount = sheetCount + 1;
        }
    }
}
