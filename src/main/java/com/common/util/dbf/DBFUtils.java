package com.common.util.dbf;

import com.common.util.file.ReadRemoteFile;
import com.linuxense.javadbf.*;
import jcifs.smb.SmbFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 文件描述 dbf文件工具
 **/
public class DBFUtils {

    public static void main(String args[]) throws DBFException, IOException{
        List<HashMap<String, Object>> listdata = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("a", "1");
        m.put("b", "1");
        m.put("c", "1");
        HashMap<String, Object> m2 = new HashMap<String, Object>();
        m2.put("a", "1");
        m2.put("b", "1");
        m2.put("c", "1");
        listdata.add(m2);
        listdata.add(m);

//        DBFExport("D:/dbf/test01.dbf", listdata);

        DBFImport("D:/dbf/test01.dbf");

//        tt();

    }

    public static void tt() throws DBFException, IOException {
        long start = System.currentTimeMillis();
        FileChannel channel = (new FileInputStream("X:\\20191211\\SCD_GG_98_20191211_GJ.dbf")).getChannel();
        DBFFileReader dbfReader = new DBFFileReader(channel,true);
        DBFFileHeader header = dbfReader.getHeader();
        int fields = header.getNumFields();
        StringBuilder fieldV = new StringBuilder();
        System.out.println(dbfReader.getHeader().getNumFields() + "----" + dbfReader.getHeader().getNumRecords());
        for (int i = 0; i < fields; i ++) {
            fieldV.append(header.getFieldName(i)).append("(").append(header.getFieldType(i)).append(") ");
        }
        System.out.println("字段信息：" + fieldV.toString());
        int index = 0;
        while(dbfReader.hasNext()) {
            DBFFileReader.Row row = dbfReader.readRow();
            StringBuilder readV = new StringBuilder();
            readV.append(index).append("、");
            for (int i = 0; i < fields; i++) {
                if (i == fields - 1) {
                    readV.append(row.read(i).toString().trim());
                } else {
                    readV.append(row.read(i).toString().trim()).append("_");
                }
            }
            System.out.println(readV.toString());
            index ++;
        }
        dbfReader.close();
        channel.close();
        System.out.println("total : " + (System.currentTimeMillis() - start));
    }

    /**
     * list 生成 dbf
     *
     * @param dbfname  文件 名
     * @param listdata 文件源数据
     * @throws IOException
     */
    public static void DBFExport(String dbfname, List<HashMap<String, Object>> listdata) throws IOException {

        int i2 = 0;
        for (String key : listdata.get(0).keySet()) {
            i2++;
        }
        DBFField fields[] = new DBFField[i2];

        int i = 0;
        for (String key : listdata.get(0).keySet()) {
            fields[i] = new DBFField();
            fields[i].setName(key);
            fields[i].setType(DBFDataType.CHARACTER);
            fields[i].setLength(100);
            i++;
        }

        FileOutputStream fos = new FileOutputStream(dbfname);
        DBFWriter writer = new DBFWriter(fos);
        writer.setFields(fields);


        for (int j = 0; j < listdata.size(); j++) {
//			HashMap<String,Object> m3 = listdata.get(j);
            Object rowData[] = new Object[i];
            int i1 = 0;
            for (String key : listdata.get(j).keySet()) {
                rowData[i1] = listdata.get(j).get(key);
                i1++;
            }
            writer.addRecord(rowData);
        }

        writer.write(fos);
        fos.close();
        System.out.println("dbf文件生成！");
    }

    public static void DBFImport(String filePath) {
        long start = System.currentTimeMillis();
        //从dbf中获取内容
        DBFReader reader = null;
        InputStream in;
        try {
            SmbFile smbFile = ReadRemoteFile.smbFileGet(filePath);
//            in = new FileInputStream(new File(filePath));
            in = smbFile.getInputStream();
            //将文件从文件流中读入。
            reader = new DBFReader(in);
            Object[] rowObjects = null;
            int fieldCount = reader.getFieldCount();
            System.out.println("字段数：" + fieldCount);
            System.out.println("记录数：" + reader.getRecordCount());
            StringBuilder fieldV = new StringBuilder();
            for (int i = 0; i < fieldCount; i ++) {
                DBFField curField = reader.getField(i);
//                System.out.println(curField);
                String aa = curField.getName() + "(" + curField.getType().name() + ")";
                if (i == fieldCount - 1) {
                    fieldV.append(aa);
                } else {
                    fieldV.append(aa).append("_");
                }
            }
            System.out.println("字段信息：" + fieldV.toString());
            int index = 0;
            while ((rowObjects = reader.nextRecord()) != null) {
                StringBuilder recordV = new StringBuilder();
                recordV.append(index).append("、");
                for (int i = 0; i < fieldCount; i ++) {
                    String curValue = new String(rowObjects[i].toString().trim().getBytes("ISO-8859-1"), "GBK");
                    if (i == (fieldCount - 1)) {
                        recordV.append(curValue);
                    } else {
                        recordV.append(curValue).append("_");
                    }
                }

                System.out.println("当前记录值：" + recordV.toString());
                index ++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DBFException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("total : " + (System.currentTimeMillis() - start));
        }
    }

    public static boolean isDBF(String fileName) {
        if (fileName.endsWith(".dbf")) {
            return true;
        } else {
            return false;
        }
    }
}
