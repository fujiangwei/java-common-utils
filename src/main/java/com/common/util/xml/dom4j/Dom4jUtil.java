package com.common.util.xml.dom4j;

import com.common.util.xml.utils.DomainUtil;
import com.common.util.xml.annotations.FieldValue;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 文件描述 Dom4jUtil
 **/
public class Dom4jUtil {

    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Dom4jUtil.class);

    /**
     * @description ：处理对象属性为List类型
     * @param rootElt 根元素
     * @param curField 当前字段
     * @param xmlLabelValue 当前字段对应xml的元素label
     * @param listAlias2CountMap
     *          对象实体中列表类型的字段别名和其在xml字符串中对应的该字段列表长度关系映射，用于获取列表长度值
     * @param obj 映射对象
     * @date  2019/9/29 18:21
     * @modifier
     * @return  boolean
     */
    private static Boolean handleListEle(Element rootElt, Field curField, String xmlLabelValue,
                                         Map<String, String> listAlias2CountMap, Object obj) {

        // 当前集合的泛型类型
        Type genericType = curField.getGenericType();
        Boolean isContinue = Boolean.FALSE;
        if (null == genericType) {
            return Boolean.TRUE;
        }

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            // 得到泛型里的class类型对象
            Class<?> actualTypeArgument = (Class<?>)pt.getActualTypeArguments()[0];
            // 列表的长度,没有的话默认给1
            String curListEleCountLabel = listAlias2CountMap.get(xmlLabelValue);
            int curListEleCount = StringUtils.isEmpty(curListEleCountLabel) ? 1 : Integer.parseInt(rootElt.elementText(curListEleCountLabel));
            List<Object> curEleList = new ArrayList<>(curListEleCount);
            try {
                //这里i <= curListEleCount是为了处理序号从1开始
                for (int i = 0; i <= curListEleCount; i ++) {
                    Element curElement = rootElt.element(xmlLabelValue + i);
                    // 元素为空跳过
                    if (null == curElement) {
                        continue;
                    }
                    // 处理只有元素的
                    if (curElement.isTextOnly()) {
                        String curElementTextTrim = rootElt.elementTextTrim(xmlLabelValue + i);
                        curEleList.add(curElementTextTrim);
                    } else {
                        Object curEleBean = fromXml2Bean(curElement, actualTypeArgument, listAlias2CountMap);
                        curEleList.add(curEleBean);
                    }
                }
                curField.set(obj, curEleList);
            } catch (IllegalAccessException e) {
                LOGGER.error("[Dom4jUtil] handleListEle 异常，{}", e);
                isContinue = Boolean.TRUE;
            }
        }

        return isContinue;
    }

    /**
     * @description ：xml生成对象
     * @param rootElt 根元素
     * @param pojo 对象字节
     * @param listAlias2CountMap
     *          对象实体中列表类型的字段别名和其在xml字符串中对应的该字段列表长度关系映射，用于获取列表长度值
     * @date  2019/9/29 17:04
     * @modifier
     * @return  T
     */
    public static <T> T fromXml2Bean(Element rootElt, Class<T> pojo, Map<String, String> listAlias2CountMap) {

        // 根据传入的Class动态生成pojo对象
        Object obj = null;
        try {
            obj = pojo.newInstance();
            // 首先得到pojo所定义的字段
            Field[] fields = pojo.getDeclaredFields();
            for (Field curField : fields) {
                // 设置字段可访问（必须，否则报错）
                curField.setAccessible(true);
                // 字段注解别名是否存在
                boolean isAnnotationPresent = curField.isAnnotationPresent(XStreamAlias.class);
                String xmlLabelValue;
                if (isAnnotationPresent) {
                    xmlLabelValue = curField.getAnnotation(XStreamAlias.class).value();
                } else {
                    xmlLabelValue = curField.getName();
                }

                Class<?> curFieldType = curField.getType();
                // 集合List元素
                if (curFieldType.equals(List.class)) {
                    handleListEle(rootElt, curField, xmlLabelValue, listAlias2CountMap, obj);
                } else {
                    // 得到字段属性注解是否存在
                    boolean isAttributePresent = curField.isAnnotationPresent(XStreamAsAttribute.class);
                    if (isAttributePresent) {
                        String attributeValue = rootElt.attributeValue(xmlLabelValue);
                        curField.set(obj, attributeValue);
                        continue;
                    }
                    // 得到字段值注解是否存在
                    boolean isFieldValue = curField.isAnnotationPresent(FieldValue.class);
                    if (isFieldValue) {
                        // 获取当前元素的文本内容
                        String fieldValue = rootElt.getText();
                        curField.set(obj, fieldValue);
                        continue;
                    }

                    Element curElement = rootElt.element(xmlLabelValue);
                    if (null == curElement) {
                        continue;
                    }

                    boolean isBaseType = DomainUtil.isBaseType(curFieldType);
                    String curElementTextTrim;
                    // 只是文本元素
                    if (curElement.isTextOnly() && isBaseType) {
                        curElementTextTrim = rootElt.elementTextTrim(xmlLabelValue);
                        if (StringUtils.isNotEmpty(curElementTextTrim)) {
                            // 设置字段值
                            setValue(curField, curElementTextTrim, obj, listAlias2CountMap);
                        }
                    }  else { // 对象元素
                        Object curEleBean = fromXml2Bean(curElement, curFieldType, listAlias2CountMap);
                        curField.set(obj, curEleBean);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("[Dom4jUtil] fromXml2Bean 异常，{}", e);
            throw new IllegalArgumentException("转换对象出错");
        }

        return (T)obj;
    }

    /**
     * @description ：设定属性值
     * @param curField 字段
     * @param curElementTextTrim 当前字段xml文本内容
     * @param obj 映射实体对象
     * @date  2019/9/29 18:19
     * @modifier
     * @return  boolean
     */
    private static boolean setValue(Field curField, String curElementTextTrim,
                                    Object obj, Map<String, String> listAlias2CountMap) {
        // 根据字段的类型将值转化为相应的类型，并设置到生成的对象中。
        Class<?> curFieldType = curField.getType();
        boolean isContinue = Boolean.FALSE;
        try {
            if (curFieldType.equals(Long.class) || curFieldType.equals(long.class)) {
                if (StringUtils.isNotEmpty(curElementTextTrim)) {
                    curField.set(obj, Long.parseLong(curElementTextTrim));
                }
            } else if (curFieldType.equals(String.class)) {
                curField.set(obj, curElementTextTrim);
            } else if (curFieldType.equals(Double.class) || curFieldType.equals(double.class)) {
                curField.set(obj, Double.parseDouble(curElementTextTrim));
            } else if (curFieldType.equals(Integer.class) || curFieldType.equals(int.class)) {
                curField.set(obj, Integer.parseInt(curElementTextTrim));
            } else if (curFieldType.equals(java.util.Date.class)) {
                String formatPattern = listAlias2CountMap.containsKey("formatPattern")
                        ? listAlias2CountMap.get("formatPattern") : DomainUtil.DEFAULT_FORMAT_PATTERN;
                SimpleDateFormat format = new SimpleDateFormat(formatPattern);
                curField.set(obj, format.parse(curElementTextTrim));
            } else {
                LOGGER.warn("Field {} is a unknown class type", curField.getName());
                isContinue = Boolean.TRUE;
            }
        } catch (IllegalAccessException | ParseException e) {
            LOGGER.error("[Dom4jUtil] setValue 异常，{}", e);
            isContinue = Boolean.TRUE;
        }

        return isContinue;
    }

    /**
     * 解析xml
     * @param path
     */
    public static void parseXml1(String path){
        try{
            //xml转换为输入流
            InputStream inputStream = new FileInputStream(new File(path));
            //创建SAXReader读取器，专门用于读取xml
            SAXReader saxReader = new SAXReader();
            //根据saxReader的read重写方法可知，既可以通过inputStream输入流来读取，也可以通过file对象来读取
            Document document = saxReader.read(inputStream);
            //必须指定文件的绝对路径
//            Document document = saxReader.read(new File(path));
            //另外还可以使用DocumentHelper提供的xml转换器也是可以的。
            Document document2 = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><modules id=\"123\"><module> 这个是module标签的文本信息</module></modules>");
            Element rootElement1 = document2.getRootElement();

            //获取根节点对象
            Element rootElement = document.getRootElement();
            //获取节点的名称
            System.out.println("根节点名称：" + rootElement.getName());
            //获取节点属性数目
            System.out.println("根节点有多少属性：" + rootElement.attributeCount());
            //获取节点的属性id的值
            System.out.println("根节点id属性的值：" + rootElement.attributeValue("id"));
            //如果元素有子节点则返回空字符串，否则返回节点内的文本
            System.out.println("根节点内文本：" + rootElement.getText());
            //rootElement.getText() 之所以会换行是因为 标签与标签之间使用了tab键和换行符布局，这个也算是文本所以显示出来换行的效果。
            //去掉的是标签与标签之间的tab键和换行符等等，不是内容前后的空格
            System.out.println("根节点内文本(1)：" + rootElement.getTextTrim());
            //返回当前节点递归所有子节点的文本信息。
            System.out.println("根节点子节点文本内容：" + rootElement.getStringValue());

            //获取子节点
            Element element = rootElement.element("module");
            if(element != null){
                //因为子节点和根节点都是Element对象所以它们的操作方式都是相同的
                System.out.println("子节点的文本：" + element.getText());
            }
            //但是有些情况xml比较复杂，规范不统一，某个节点不存在直接java.lang.NullPointerException，所以获取到element对象之后要先判断一下是否为空
            //支持修改节点名称
            rootElement.setName("root");
            System.out.println("根节点修改之后的名称：" + rootElement.getName());
            //同样修改标签内的文本也一样
            rootElement.setText("text");
            System.out.println("根节点修改之后的文本：" + rootElement.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXml2(String path){
        try{
            //xml转换为输入流
            InputStream inputStream = new FileInputStream(new File(path));
            //创建SAXReader读取器，专门用于读取xml
            SAXReader saxReader = new SAXReader();
            //根据saxReader的read重写方法可知，既可以通过inputStream输入流来读取，也可以通过file对象来读取
            Document document = saxReader.read(inputStream);

            Element rootElement = document.getRootElement();
            Iterator<Element> modulesIterator = rootElement.elements("module").iterator();
            //rootElement.element("name");获取某一个子元素
            //rootElement.elements("name");获取根节点下子元素moudule节点的集合，返回List集合类型
            //rootElement.elements("module").iterator();把返回的list集合里面每一个元素迭代子节点，全部返回到一个Iterator集合中
            while(modulesIterator.hasNext()){
                Element moduleElement = modulesIterator.next();
                Element nameElement = moduleElement.element("name");
                System.out.println(nameElement.getName() + ":" + nameElement.getText());
                Element valueElement = moduleElement.element("value");
                System.out.println(valueElement.getName() + ":" + valueElement.getText());
                Element descriptionElement = moduleElement.element("description");
                System.out.println(descriptionElement.getName() + ":" + descriptionElement.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseXml3(String path){
        try{
            //xml转换为输入流
            InputStream inputStream = new FileInputStream(new File(path));
            //创建SAXReader读取器，专门用于读取xml
            SAXReader saxReader = new SAXReader();
            //根据saxReader的read重写方法可知，既可以通过inputStream输入流来读取，也可以通过file对象来读取
            Document document = saxReader.read(inputStream);

            Element rootElement = document.getRootElement();
            List moduleList = rootElement.elements("module");
            if(moduleList != null){
                //因为第一个module标签只有内容没有子节点，直接.iterator()就java.lang.NullPointerException了, 所以需要分开实现
                List<Element> elementList = rootElement.elements("module");
                for (Element element : elementList) {
                    if(element.isTextOnly()){
                        System.out.println("【1】" + (StringUtils.isNotEmpty(element.getTextTrim()) ? element.getTextTrim() : "nothing"));
                    } else {
                        Element nameElement = element.element("name");
                        System.out.println("   【2】" + nameElement.getName() + ":" + nameElement.getText());
                        Element valueElement = element.element("value");
                        System.out.println("   【2】" + valueElement.getName() + ":" + valueElement.getText());
                        Element descriptionElement = element.element("descript");
                        System.out.println("   【2】" + descriptionElement.getName() + ":" + descriptionElement.getText());

                        List<Element> subElementList = element.elements("module");
                        for (Element subElement : subElementList) {
                            if(!subElement.getTextTrim().equals("")){
                                System.out.println("      【3】" + subElement.getTextTrim());
                            }else{
                                Element subNameElement = subElement.element("name");
                                System.out.println("      【3】" + subNameElement.getName() + ":" + subNameElement.getText());
                                Element subValueElement = subElement.element("value");
                                System.out.println("      【3】" + subValueElement.getName() + ":" + subValueElement.getText());
                                Element subDescriptElement = subElement.element("descript");
                                System.out.println("      【3】" + subDescriptElement.getName() + ":" + subDescriptElement.getText());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成xml
     *    <1>DocumentHelper提供了创建Document对象的方法
     *    <2>操作这个Document对象，添加节点以及节点下的文本、名称和属性值
     *    <3>然后利用XMLWriter写入器把封装的document对象写入到磁盘中
     */
    public static void buildXml(String fileName){
        try {
            //DocumentHelper提供了创建Document对象的方法
            Document document = DocumentHelper.createDocument();
            //添加节点信息
            Element rootElement = document.addElement("modules");
            //这里可以继续添加子节点，也可以指定内容
            rootElement.setText("这个是module标签的文本信息");
            Element element = rootElement.addElement("module");

            Element nameElement = element.addElement("name");
            Element valueElement = element.addElement("value");
            Element descriptionElement = element.addElement("description");
            nameElement.setText("名称");
            //为节点添加属性值
            nameElement.addAttribute("language", "java");
            valueElement.setText("值");
            valueElement.addAttribute("language", "c#");
            descriptionElement.setText("描述");
            descriptionElement.addAttribute("language", "sql server");
            //将document文档对象直接转换成字符串输出
            document.setXMLEncoding("GBK");
            System.out.println(document.asXML());

            // 输出格式化
            OutputFormat ops = OutputFormat.createPrettyPrint();
            //设置编码
            ops.setEncoding("UTF-8");
            //设置缩进
            ops.setIndent(true);
            Writer fileWriter = new FileWriter(fileName);
            //dom4j提供了专门写入文件的对象XMLWriter
//            XMLWriter xmlWriter = new XMLWriter(fileWriter);
            XMLWriter xmlWriter = new XMLWriter(fileWriter, ops);
            xmlWriter.write(document);
            xmlWriter.flush();
            xmlWriter.close();
            System.out.println("xml文档添加成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
}

    public static void main(String[] args) throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource("test.xml").getPath();
        parseXml1(path);
//        parseXml2(path);
//        parseXml3(path);

        String path1 = ResourceUtils.getURL("classpath:").getPath();
//        buildXml(path1 + "module4.xml");
    }
}
