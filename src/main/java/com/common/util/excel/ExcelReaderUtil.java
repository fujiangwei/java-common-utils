package com.common.util.excel;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelReaderUtil {
	//excel2003扩展名
	public static final String EXCEL03_EXTENSION = ".xls";
	//excel2007扩展名
	public static final String EXCEL07_EXTENSION = ".xlsx";

	/**
	 * 每获取一条记录，即打印
	 * 在flume里每获取一条记录即发送，而不必缓存起来，可以大大减少内存的消耗，这里主要是针对flume读取大数据量excel来说的
	 * @param sheetName
	 * @param sheetIndex
	 * @param curRow
	 * @param cellList
	 */
	public static void sendRows(String filePath, String sheetName, int sheetIndex, int curRow, List<String> cellList) {
			StringBuffer oneLineSb = new StringBuffer();
//			oneLineSb.append(filePath);
//			oneLineSb.append("--");
//			oneLineSb.append("sheet" + sheetIndex);
//			oneLineSb.append("::" + sheetName);//加上sheet名
//			oneLineSb.append("--");
			oneLineSb.append("row" + curRow + "-" + cellList.size());
			oneLineSb.append("::");
			for (String cell : cellList) {
				oneLineSb.append(cell.trim());
				oneLineSb.append("|");
			}
			String oneLine = oneLineSb.toString();
			if (oneLine.endsWith("|")) {
				oneLine = oneLine.substring(0, oneLine.lastIndexOf("|"));
			}// 去除最后一个分隔符

			System.out.println(oneLine);
	}

	public static List<List<String>> readExcel(String fileName, String remoteMode) throws Exception {
		List<List<String>> result = Lists.newArrayList();
		int totalRows =0;
		if (fileName.endsWith(EXCEL03_EXTENSION)) {
			//处理excel2003文件
			ExcelXlsReader excelXls=new ExcelXlsReader();
			totalRows =excelXls.process(fileName);
		} else if (fileName.endsWith(EXCEL07_EXTENSION)) {
			//处理excel2007文件
			ExcelXlsxReaderWithDefaultHandler excelXlsxReader = new ExcelXlsxReaderWithDefaultHandler(result);
			totalRows = excelXlsxReader.process(fileName, remoteMode);
		} else {
			throw new Exception("文件格式错误，fileName的扩展名只能是xls或xlsx。");
		}
		System.out.println("发送的总行数：" + totalRows);

		return result;
	}

	public static void copyToTemp(File file, String tmpDir) throws Exception{
		FileInputStream fis=new FileInputStream(file);
		File file1=new File(tmpDir);
		if (file1.exists()){
			file1.delete();
		}
		FileOutputStream fos=new FileOutputStream(tmpDir);
		byte[] b=new byte[1024];
		int n=0;
		while ((n=fis.read(b))!=-1){
			fos.write(b,0,n);
		}
		fis.close();
		fos.close();
	}

	public static void main(String[] args) throws Exception {
		//String path="D:\\Github\\test.xls";
		String path="C:\\Users\\hspcadmin\\Desktop\\GBXX_20191216.xls";

//        String path="C:\\Users\\hspcadmin\\Desktop\\ZZZS_20200326.xls";

		/*ExcelReaderUtil.readExcel(file2.getAbsolutePath(),"/home/test/tmp.xlsx");*/
		ExcelReaderUtil.readExcel(path, "shared2");
		/*readXlsx(file2.getAbsolutePath());*/




	}
}