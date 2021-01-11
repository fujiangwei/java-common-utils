# 一、在线文件类型检测
https://www.aconvert.com/cn/analyze.html 

# 二、解析xls，xlsx
    解析这三类文件的时候，推荐使用POI，因为此技术已经较为成熟，使用范围广。在解析这三类文件的时候，有两种方式可供使用
    1. 第一种是一次性将文件的二进制流读到内存中再解析，如果数据量大的话，这种方法就不适用了，因为会占用大量的系统资源，甚至导致OOM等问题
    2. 第二种是采用事件响应机制来解析数据，这种方式的原理是不用一次性加载数据，而是边读边解析，此时文件一直被占用，因此不能进行修改等写入操作

* 解析xls

    xls文件是excel 2003的版本，需要专门使用POI的HSSF相关组件去解析，但是暂未发现POI中有对xls进行事件响应机制的解析方式，所以需要自行编写相关的listener。
    主要代码如下：
    
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
        
* 解析xlsx

    xlsx文件是excel 2007版本，是要使用POI中的XSSF组件去解析，由于POI提供了专门了事件响应方式去解析大型的xlsx文件，所以代码编写上
比xls要简单许多。主要代码如下：

        public static void xlsxEventReader() throws Exception {
            Map<Integer, List<Map<String, String>>> dataMap = new HashMap<>();
            OPCPackage pkg = null;
            InputStream in = new FileInputStream("C:\\Users\\yangliang20945\\Desktop\\资讯\\GBXX_20201209.xlsx");
            pkg = OPCPackage.open(in);
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable styles = xssfReader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            Iterator<InputStream> iterators = xssfReader.getSheetsData();
            // 遍历所有的sheet页
               while (iterators.hasNext()) {
                    SaxSheetContentsHandler sheetHandler = new SaxSheetContentsHandler();
                    // 开始解析，SaxSheetContentsHandler是实现了XSSFSheetXMLHandler.SheetContentsHandler接口的自定义类
                    ProcessExcelUtil.processSheet(styles, strings, iterators.next(), sheetHandler);
            }
        }