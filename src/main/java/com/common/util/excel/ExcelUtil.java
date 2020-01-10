package com.common.util.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.util.Assert;

import java.io.*;
import java.util.Date;

/**
 * 文件描述
 **/
@Slf4j(topic = "ExcelUtil")
public class ExcelUtil {

    public static void importExcel(File file) {
        Workbook workbook = null;
        try {
            workbook = getWorkbook(file);
            //获取第一张表
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < sheet.getLastRowNum(); i ++) {
                //获取索引为i的行，以0开始
                Row row = sheet.getRow(i);
                System.out.println(row.getCell(0).getStringCellValue());
            }
        } catch (Exception e) {
            log.error("importExcel fail,{}", e);
        } finally {
            close(workbook);
        }
    }

    public static void close(Workbook workbook) {
        if (null != workbook) {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error("close fail,{}", e);
            }
        }
    }

    /**
     * Excel共有两种格式：xls（03版本）和xlsx(07及之后版本)
     *      POI提供了两个对应接口类，分别为：HSSFWorkbook和XSSFWorkbook。
     * @param file 文件
     * @return Workbook
     */
    public static Workbook getWorkbook(File file) {
        Assert.notNull(file, "file is null.");
        String filePath = file.getPath();
        Assert.isTrue((isExcel2003(filePath) || isExcel2007(filePath)), "file is not excel.");

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            if (isExcel2003(filePath)) {
                return new HSSFWorkbook(fileInputStream);
            }

            return new XSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            log.error("getWorkbook fail,{}", e);
            throw new IllegalArgumentException("getWorkbook fail");
        } finally {
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * excel操作
     * @param exportFilePath
     * @throws Exception
     */
    public static void excelTest(String exportFilePath) throws Exception {
        File exportFile = new File(exportFilePath);
        // 创建Excel文件(Workbook)
        HSSFWorkbook workbook = null;
        if(isExcel2003(exportFilePath)) {
            workbook = new HSSFWorkbook();
        }
//        else {
//            workbook = new XSSFWorkbook();
//        }

        FileOutputStream out = new FileOutputStream(exportFile);

        //创建工作表(Sheet)
//        Sheet sheet = workbook.createSheet();
        HSSFSheet sheet = workbook.createSheet("king");
        // 创建行,从0开始
        Row row = sheet.createRow(0);
        // 创建行的单元格,也是从0开始
        Cell cell = row.createCell(0);
        // 设置单元格内容
        cell.setCellValue("姓名");
        row.createCell(1).setCellValue("年龄");
        // 创建行,从0开始
        HSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("张三");
        row1.createCell(1).setCellValue(3);
        // 创建行,从0开始
        HSSFRow row3 = sheet.createRow(2);
        row3.createCell(0).setCellValue("李四");
        row3.createCell(1).setCellValue(21);

        //创建文档信息
        workbook.createInformationProperties();
        /******1、摘要信息******/
        DocumentSummaryInformation dsi= workbook.getDocumentSummaryInformation();
        //类别
        dsi.setCategory("类别:Excel文件");
        //管理者
        dsi.setManager("管理者:king");
        //公司
        dsi.setCompany("公司:--");
        /******摘要信息******/
        SummaryInformation si = workbook.getSummaryInformation();
        //主题
        si.setSubject("主题:--");
        //标题
        si.setTitle("标题:测试文档");
        //作者
        si.setAuthor("作者:king");
        //备注
        si.setComments("备注:POI测试文档");

        /******2、创建批注******/
        HSSFPatriarch hssfPatriarch = sheet.createDrawingPatriarch();
        // dx1         第1个单元格中x轴的偏移量     dy1         第1个单元格中y轴的偏移量
        // dx2         第2个单元格中x轴的偏移量     dy2         第2个单元格中y轴的偏移量
        // col1        第1个单元格的列号            row1        第1个单元格的行号
        // col2        第2个单元格的列号            row2        第2个单元格的行号
        HSSFClientAnchor anchor = hssfPatriarch.createAnchor(0, 0, 0, 0, 5, 1, 9, 3);//创建批注位置
        //创建批注
        HSSFComment comment = hssfPatriarch.createCellComment(anchor);
        //设置批注内容
        comment.setString(new HSSFRichTextString("这是一个批注段落！"));
        //设置批注作者
        comment.setAuthor("king");
        //设置批注默认显示
        comment.setVisible(true);
        //创建行
        HSSFCell cell52 = sheet.createRow(5).createCell(1);
        cell52.setCellValue("测试");
        //把批注赋值给单元格
        cell52.setCellComment(comment);

        /****3、创建页眉和页脚******/
        //得到页眉
        HSSFHeader header =sheet.getHeader();
        header.setLeft("页眉左边");
        header.setRight("页眉右边");
        header.setCenter("页眉中间");
        //得到页脚
        HSSFFooter footer =sheet.getFooter();
        footer.setLeft("页脚左边");
        footer.setRight("页脚右边");
        footer.setCenter("页脚中间");

        /****4、Excel内嵌的格式******/
        // 当使用Excel内嵌的（或者说预定义）的格式时，直接用HSSFDataFormat.getBuiltinFormat静态方法即可。
        // 当使用自己定义的格式时，必须先调用HSSFWorkbook.createDataFormat()
        HSSFRow row10 = sheet.createRow(10);
        HSSFCell cell11 = row10.createCell(0);
        cell11.setCellValue(new Date());
        // 判断单元格是否为日期
        System.out.println(">>>>>>>>>>>>> 1 " + DateUtil.isCellDateFormatted(cell11));
        HSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
        cell11.setCellStyle(style);
        System.out.println(">>>>>>>>>>>>> 2 " + DateUtil.isCellDateFormatted(cell11));
        //设置保留2位小数--使用Excel内嵌的格式
        cell11 = row10.createCell(1);
        cell11.setCellValue(12.3456789);
        style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cell11.setCellStyle(style);
        //设置货币格式--使用自定义的格式
        cell11 = row10.createCell(2);
        cell11.setCellValue(12345.6789);
        style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("￥#,##0"));
        cell11.setCellStyle(style);
        //设置百分比格式--使用自定义的格式
        cell11 = row10.createCell(3);
        cell11.setCellValue(0.123456789);
        style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        cell11.setCellStyle(style);
        //设置中文大写格式--使用自定义的格式
        cell11 = row10.createCell(4);
        cell11.setCellValue(12345);
        style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("[DbNum2][$-804]0"));
        cell11.setCellStyle(style);
        //设置科学计数法格式--使用自定义的格式
        cell11 = row10.createCell(5);
        cell11.setCellValue(12345);
        style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00E+00"));
        cell11.setCellStyle(style);

        /****5、合并单元格******/
        HSSFRow row13 = sheet.createRow(12);
        //合并列
        HSSFCell cell131 = row13.createCell(0);
        cell131.setCellValue("合并列");
        CellRangeAddress region = new CellRangeAddress(12, 12, 0, 5);
        sheet.addMergedRegion(region);
        //合并行
        cell131 = row13.createCell(6);
        cell131.setCellValue("合并行");
        region = new CellRangeAddress(12, 15, 6, 6);
        sheet.addMergedRegion(region);

        /****6、单元格对齐******/
        cell131 = row13.createCell(10);
        cell131.setCellValue("单元");
        style = workbook.createCellStyle();
        //水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        //垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //自动换行
        style.setWrapText(true);
        //缩进
//        style.setIndention((short)5);
        //文本旋转，这里的取值是从-90到90，而不是0-180度。
//        style.setRotation((short)60);
        cell131.setCellStyle(style);

        /****7、设置边框******/
        cell131 = row13.createCell(12);
        cell131.setCellValue("设置边框");
        style = workbook.createCellStyle();
        //上边框
        style.setBorderTop(BorderStyle.DOTTED);
        //下边框
        style.setBorderBottom(BorderStyle.THICK);
        //左边框
        style.setBorderLeft(BorderStyle.DOUBLE);
        //右边框
        style.setBorderRight(BorderStyle.SLANTED_DASH_DOT);
        //上边框颜色
        style.setTopBorderColor(HSSFColor.RED.index);
        //下边框颜色
        style.setBottomBorderColor(HSSFColor.BLUE.index);
        //左边框颜色
        style.setLeftBorderColor(HSSFColor.GREEN.index);
        //右边框颜色
        style.setRightBorderColor(HSSFColor.PINK.index);
        cell131.setCellStyle(style);

        /****8、设置字体******/
        cell131 = row13.createCell(15);
        cell131.setCellValue("设置字体");
        style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        //设置字体名称
        font.setFontName("华文行楷");
        //设置字号
        font.setFontHeightInPoints((short)28);
        //设置字体颜色
        font.setColor(HSSFColor.RED.index);
        //设置下划线
        font.setUnderline(FontFormatting.U_SINGLE);
        //设置上标下标
        font.setTypeOffset(FontFormatting.SS_SUPER);
        //设置删除线
        font.setStrikeout(true);
        style.setFont(font);
        cell131.setCellStyle(style);

        /****9、设置宽度******/
        row = sheet.createRow(16);
        cell = row.createCell(1);
        cell.setCellValue("123456789012345678901234567890");
        // 设置第一列的宽度是31个字符宽度
        sheet.setColumnWidth(1, 31 * 256);
        //设置行的高度是50个点
        row.setHeightInPoints(50);

        /********10、创建sheet********/
        workbook.createSheet("Test0");
        workbook.createSheet("Test1");
        workbook.createSheet("Test2");
        workbook.createSheet("Test3");
        //重命名工作表
        workbook.setSheetName(2, "you");
        //设置默认工作表
        workbook.setActiveSheet(0);

        /*********下拉框*********/
        sheet.createRow(4).createCell(0).setCellValue("下拉框需要选中");
        CellRangeAddressList regions = new CellRangeAddressList(4, 65535, 0, 0);
        DVConstraint constraint = DVConstraint.createExplicitListConstraint(new String[] { "C++", "Java", "C#" });
        HSSFDataValidation dataValidate = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(dataValidate);

        workbook.write(out);
        out.flush();
        close(workbook);
        out.close();

        /********11、读取excel遍历sheet********/
        FileInputStream stream = new FileInputStream(exportFilePath);
        //读取现有的Excel
        HSSFWorkbook workbook2 = new HSSFWorkbook(stream);
        int numberOfSheets = workbook.getNumberOfSheets();
        //得到指定名称的Sheet
        HSSFSheet sheet2 = workbook2.getSheet("king");
        for (Row row2 : sheet2) {
            for (Cell cell2 : row2) {
                System.out.print(cell2 + "\t");
            }
            System.out.println();
        }
    }

    /**
     * 文档操作
     * @param exportFilePath
     * @throws Exception
     */
    public static void docTest(String exportFilePath) throws Exception {
        // 创建Word文件
        XWPFDocument doc = new XWPFDocument();
        // 新建一个段落
        XWPFParagraph p = doc.createParagraph();
        // 设置段落的对齐方式
        p.setAlignment(ParagraphAlignment.CENTER);
        //设置下边框
        p.setBorderBottom(Borders.DOUBLE);
        //设置上边框
        p.setBorderTop(Borders.DOUBLE);
        //设置右边框
        p.setBorderRight(Borders.DOUBLE);
        //设置左边框
        p.setBorderLeft(Borders.DOUBLE);
        //创建段落文本
        XWPFRun r = p.createRun();
        r.setText("POI创建的Word段落文本");
        //设置为粗体
        r.setBold(true);
        //设置颜色
        r.setColor("FF0000");
        // 新建一个段落
        p = doc.createParagraph();
        r = p.createRun();
        r.setText("POI读写Excel功能强大、操作简单。");
        //创建一个表格
        XWPFTable table= doc.createTable(3, 3);
        table.getRow(0).getCell(0).setText("表格1");
        table.getRow(1).getCell(1).setText("表格2");
        table.getRow(2).getCell(2).setText("表格3");
        FileOutputStream out = new FileOutputStream(exportFilePath);
        doc.write(out);
        out.close();

        FileInputStream stream = new FileInputStream(exportFilePath);
        // 创建Word文件
        XWPFDocument doc2 = new XWPFDocument(stream);
        //遍历段落
        for(XWPFParagraph paragraph : doc2.getParagraphs()) {
            System.out.print(paragraph.getParagraphText());
        }
        //遍历表格
        for(XWPFTable table2 : doc2.getTables()) {
            for(XWPFTableRow row : table2.getRows()) {
                for(XWPFTableCell cell : row.getTableCells()) {
                    System.out.print(cell.getText());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
//        excelTest(path + "user.xls");
//        docTest(path + "user.doc");

        String fileName = Thread.currentThread().getContextClassLoader().getResource("excel/table.xlsx").getPath();

        String path22 = "X:/20200102/GBXX_20200102.xlsx";
        File file = new File(path22);
        importExcel(file);
        System.out.println("123");
    }
}
