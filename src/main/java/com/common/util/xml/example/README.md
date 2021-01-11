# xml解析
xml文件比较大，那么需要使用sax模式（就是事件响应机制）去解析xml，主要代码如下：
        
        public static void xmlSaxReader() throws Exception {
             SAXParserFactory factory = SAXParserFactory.newInstance();
             SAXParser saxParser = factory.newSAXParser();
             File inputFile = new File(DIC_PATH+FILE_NAME_GB);
             // MySAXHandler 继承自 DefaultHandler
             saxParser.parse(inputFile, new MySAXHandler());
         }
