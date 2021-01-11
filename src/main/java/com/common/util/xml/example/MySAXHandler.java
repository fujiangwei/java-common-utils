package com.common.util.xml.example;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述
 *
 * @author
 * @date
 **/
public class MySAXHandler extends DefaultHandler {
    private List<List<String>> totalData = new ArrayList<>(1024 * 128);
    private List<String> currentRow = null;
    private String currentValue = "";

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // 解析对应的标签
        if (qName.equals("Row")) {
            currentRow = new ArrayList<>();
        }
        if (qName.equals("Data")) {
            currentValue = "";
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // System.out.println("</" + qName + ">");
        // 解析对应的标签
        if (qName.equals("Row")) {
            totalData.add(currentRow);
        }
        if (qName.equals("Data")) {
            this.currentRow.add(currentValue);
            currentValue = null;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        String str = new String(ch, start, length);
        if (currentValue != null) {
            currentValue = str;
        }
    }

    public static long startTime = 0;

    /**
     * Receive notification of the end of the document.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the end
     * of a document (such as finalising a tree or closing an output
     * file).</p>
     *
     * @throws SAXException Any SAX exception, possibly
     *                      wrapping another exception.
     * @see ContentHandler#endDocument
     */
    @Override
    public void endDocument() throws SAXException {
        int size = this.totalData.size();
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("文件解析完毕，共" + size + "条数据,共耗时：" + totalTime + "ms");
    }

    /**
     * Receive notification of the beginning of the document.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the beginning
     * of a document (such as allocating the root node of a tree or
     * creating an output file).</p>
     *
     * @throws SAXException Any SAX exception, possibly
     *                      wrapping another exception.
     * @see ContentHandler#startDocument
     */
    @Override
    public void startDocument() throws SAXException {
        startTime = System.currentTimeMillis();
    }
}
