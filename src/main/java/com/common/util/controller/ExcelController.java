package com.common.util.controller;

import com.common.util.domain.ExcelData;
import com.common.util.domain.User;
import com.common.util.excel.ExportExcelUtils;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExcelController {

    @PostMapping(value = "import")
    public List<User> importExcel(@RequestParam("files") MultipartFile[] files) throws Exception {
        Assert.notNull(files, "文件为空");
        String fileName = files[0].getOriginalFilename();

        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new IllegalArgumentException("上传文件格式不正确");
        }
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }

        InputStream is = files[0].getInputStream();
        Workbook wb = null;
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet = wb.getSheetAt(0);

        List<User> userList = Lists.newArrayList();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null){
                continue;
            }

            String name = row.getCell(0).getStringCellValue();
            double age = row.getCell(1).getNumericCellValue();

            User user = new User();
            user.setId(Double.valueOf(age).intValue());
            user.setName(name);
            userList.add(user);
        }

        return userList;
    }

    @RequestMapping(value = "/excel", method = RequestMethod.GET)
    public void excel(HttpServletResponse response) throws Exception {
        ExcelData data = new ExcelData();
        data.setName("hello");
        List<String> titles = new ArrayList();
        titles.add("a1");
        titles.add("a2");
        titles.add("a3");
        data.setTitles(titles);

        List<List<Object>> rows = new ArrayList();
        List<Object> row = new ArrayList();
        row.add("11111111111");
        row.add("22222222222");
        row.add("3333333333");
        rows.add(row);

        data.setRows(rows);

        ExportExcelUtils.exportExcel(response,"hello.xlsx", data);
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String excel() throws Exception {
       return "hello world";
    }

    @RequestMapping(value = "/remote", method = RequestMethod.GET)
    public void remoteTest() throws Exception {
    }
}