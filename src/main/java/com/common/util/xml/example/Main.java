package com.common.util.xml.example;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

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
        xmlSaxReader();
    }

    /**
     * 通过 sax方式读取数据量多的xml文件
     *
     * @throws Exception
     */
    public static void xmlSaxReader() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        File inputFile = new File(DIC_PATH + FILE_NAME_GB);
        // MySAXHandler 继承自 DefaultHandler
        saxParser.parse(inputFile, new MySAXHandler());
    }

}
