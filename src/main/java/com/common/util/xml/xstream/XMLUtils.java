package com.common.util.xml.xstream;

import com.alibaba.fastjson.JSON;
import com.common.util.xml.dom4j.Dom4jUtil;
import com.common.util.domain.*;
import com.common.util.xml.xstream.convert.FieldConverter;
import com.common.util.xml.xstream.convert.IbaAttrConvertor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Map;

/**
 * 文件描述 XStream工具
 **/
public class XMLUtils {

    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);

    /**
     * xml默认
     */
    public static final String XML_DEFAULT_HEAD = "<?xml version=\"1.0\" standalone=\"yes\"?>";

    static XStream xStream;
    static {
        //单下划线变成双下划线处理
        xStream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("_-", "_")));
//        xStream = new XStream(new Xpp3Driver(new NoNameCoder()));
        // problem: Security framework of XStream not initialized, XStream is probably vulnerable
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByWildcard(new String[] {
                "com.common.util.**"
        });
    }

    /**
     * @description ：xml字符串解析为对象
     * @param clazz 对象字节
     * @param xmlStr xml串
     * @date  2019/9/26 19:32
     * @modifier
     * @return  T
     */
    public static <T> T xmlStr2Obj(Class<T> clazz, final String xmlStr) {
        xStream.processAnnotations(clazz);
        try {
            return (T)xStream.fromXML(xmlStr);
        } catch (Exception e) {
            LOGGER.error("[XMLUtils] xml2Obj 异常, {}", e.getMessage());
            throw new IllegalArgumentException("解析xml出错");
        }
    }

    /**
     * @description ：对象转为xml
     * @param obj 传入待转换对象
     * @date  2019/9/26 19:30
     * @modifier
     * @return  java.lang.String
     */
    public static String obj2XmlStr(Object obj, String head, Converter converter) {
        // 解析注解
        xStream.processAnnotations(obj.getClass());
        if (null != converter) {
            xStream.registerConverter(converter);
        }
        try {
            String xmlStr = xStream.toXML(obj);
            if (StringUtils.isNotEmpty(head)) {
                return head + xmlStr;
            }
            return XML_DEFAULT_HEAD + xmlStr;
        } catch (Exception e) {
            LOGGER.error("[XMLUtils] obj2Xml 异常, {}", e);
            throw new IllegalArgumentException("解析xml出错");
        }
    }

    /**
     * @description ：对象转为xml
     * @param obj 传入待转换对象
     * @date  2019/9/26 19:30
     * @modifier
     * @return  java.lang.String
     */
    public static String obj2XmlStr(Object obj, String head) {
        // 解析注解
        xStream.processAnnotations(obj.getClass());
        // 注册转化器
        xStream.registerConverter(new FieldConverter());
        try {
            String xmlStr = xStream.toXML(obj);
            if (StringUtils.isNotEmpty(head)) {
                return head + xmlStr;
            }
            return XML_DEFAULT_HEAD + xmlStr;
        } catch (Exception e) {
            LOGGER.error("[XMLUtils] obj2Xml 异常, {}", e);
            throw new IllegalArgumentException("解析xml出错");
        }
    }

    public static PDMDataFile fromXML(String filePath) {
        xStream.registerConverter(new IbaAttrConvertor());
        xStream.alias("PDMDataFile", PDMDataFile.class);
        xStream.addImplicitCollection(PDMDataFile.class, "lstPart");

        xStream.alias("Part", Part.class);
        xStream.aliasAttribute(Part.class, "partId", "PartID");
        xStream.aliasAttribute(Part.class, "version", "Version");
        xStream.aliasAttribute(Part.class, "parentUuid", "ParentUUID");

        xStream.aliasField("Attributes", Part.class, "atts");
        xStream.aliasField("BomUses", Part.class, "bomUses");

        xStream.alias("Attributes", Attributes.class);
        xStream.aliasField("PartName", Attributes.class, "partName");
        xStream.aliasField("PartType", Attributes.class, "partType");
        xStream.aliasField("PartDesc", Attributes.class, "partDesc");
        xStream.aliasField("PartUuid", Attributes.class, "partUuid");
        xStream.aliasField("UpdatedBy", Attributes.class, "updatedBy");
        xStream.aliasField("Effectivity", Attributes.class, "effectivity");
        xStream.aliasField("lifecycleState", Attributes.class, "lifeCycleState");
        xStream.aliasField("CreatedBy", Attributes.class, "createdBy");
        xStream.aliasField("CreatedTime", Attributes.class, "createdTime");
        xStream.aliasField("LastUpdated", Attributes.class, "lastUpdated");

        xStream.addImplicitCollection(Attributes.class, "lstIbaAttr");

        xStream.alias("ibaAttr", IbaAttr.class);
        xStream.aliasAttribute(IbaAttr.class, "type", "Type");
        xStream.aliasAttribute(IbaAttr.class, "chname", "CHName");
        xStream.aliasAttribute(IbaAttr.class, "enname", "ENName");

        xStream.alias("BomUses", BomUses.class);
        xStream.addImplicitCollection(BomUses.class, "lstBom");

        xStream.alias("Bom", Bom.class);
        xStream.useAttributeFor("partId", Bom.class);
        xStream.useAttributeFor("partUuid", Bom.class);
        xStream.aliasAttribute(Bom.class, "partId", "PartID");
        xStream.aliasAttribute(Bom.class, "partUuid", "PartUUID");

        PDMDataFile result = new PDMDataFile();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            result = (PDMDataFile) xStream.fromXML(fis);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

        //xml转对象
        String xml0 = "<User><id>2</id><name>2222</name><status>1</status></User>";
        User user1 = xmlStr2Obj(User.class, xml0);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        InsDirPackage insDirPackage = new InsDirPackage();
        insDirPackage.setlDeptId(0);
        insDirPackage.setlOrgId("0");
        insDirPackage.setVcSessionId("25664954000000");
        insDirPackage.setlInstrCount("3");

        User user = new User();
        user.setId(1);
        user.setName("11111");
        insDirPackage.setUser(user);

        Instruction ins = new Instruction();
//        ins.setlFundId("53343");
        ins.setlBasecombiId("1111");
        ins.setlTempNo("1");
        ins.setlStockCount("1");
        ins.setStocks(Lists.newArrayList("1"));
        ins.setUser(user);
        ins.setUsers(Lists.newArrayList(user));

        Instruction ins2 = new Instruction();
        ins2.setlFundId("53242");
        ins2.setlBasecombiId("2222");
        ins2.setlTempNo("2");
        ins2.setlStockCount("2");
        ins2.setStocks(Lists.newArrayList("2", "3"));

        Instruction ins3 = new Instruction();
        ins3.setlFundId("53242");
        ins3.setlBasecombiId("2222");
        ins3.setlTempNo("3");
        ins3.setlStockCount("6");
        ins3.setStocks(Lists.newArrayList("4", "5", "6", "7", "8", "9"));

        insDirPackage.setInstructions(Lists.newArrayList(ins, ins2, ins3));
        String xml1 = obj2XmlStr(insDirPackage, "",  new FieldConverter("yyyy-MM-dd"));
        System.out.println("===================InsDirPackage================");
        stopWatch.stop();
        System.out.println("total " + stopWatch.getTotalTimeMillis() + "ms>>>>>>> " + xml1);

        Map<String, String> listAlias2CountMap = Maps.newHashMap();
        listAlias2CountMap.put("Instruction", "l_instr_count");
        listAlias2CountMap.put("Stock", "l_stock_count");
        InsDirPackage insDirPackage2 = Dom4jUtil.fromXml2Bean(DocumentHelper.parseText(xml1).getRootElement(),
                InsDirPackage.class, listAlias2CountMap);
        System.out.println(JSON.toJSON(insDirPackage2));

        ExecSQLPackage execSQLPackage = new ExecSQLPackage();
        execSQLPackage.setQueryMode(2);
        execSQLPackage.setQueryCondition("find");
        execSQLPackage.setBeginDate(new Date());
        DataParam dataParam = new DataParam();
        dataParam.setUserName("qq");
        dataParam.setPassword("");
        execSQLPackage.setDataParam(dataParam);
        Query query = new Query();
        query.setTransactionCode("1");
        query.setQuerySql("select * from user");
        execSQLPackage.setQuery(query);

        String xml2 = obj2XmlStr(execSQLPackage, "", new FieldConverter("yyyy-MM-dd"));
        System.out.println("==================ExecSQLPackage=================");
        System.out.println(xml2);


        ExecSQLPackage execSQLPackage1 = Dom4jUtil.fromXml2Bean(DocumentHelper.parseText(xml2).getRootElement(),
                ExecSQLPackage.class, Maps.newHashMap());

        System.out.println(JSON.toJSON(execSQLPackage1));

        String path = Thread.currentThread().getContextClassLoader().getResource("xml/test.xml").getPath();
        fromXML(path);
    }
}
