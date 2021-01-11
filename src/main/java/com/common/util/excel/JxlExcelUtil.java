package com.common.util.excel;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 文件描述
 **/
public class JxlExcelUtil {

    public static void main(String[] args) throws Exception{
        String path = "C:\\Users\\hspcadmin\\Desktop\\GBXX_20191216.xls";
        //1:创建workbook
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        Workbook workbook= Workbook.getWorkbook(fileInputStream);
        //2:获取第一个工作表sheet
        Sheet sheet=workbook.getSheet(0);
        //3:获取数据
        System.out.println("行："+sheet.getRows());
        System.out.println("列："+sheet.getColumns());
        for(int i=0;i<sheet.getRows();i++){
            for(int j=0;j<sheet.getColumns();j++){
                Cell cell=sheet.getCell(j,i);
                System.out.print(cell.getContents()+" ");
            }
            System.out.println();
        }

        //最后一步：关闭资源
        workbook.close();
    }
}