package com.common.util.xml.jdom;

import com.common.util.domain.StudentGridlb;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.springframework.util.ResourceUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述
 **/
public class JdomUtil {

    public static void main(String[] args) throws Exception {

        String path = Thread.currentThread().getContextClassLoader().getResource("test.xml").getPath();
        System.out.println("path:" + path);

        SAXBuilder saxBuilder = new SAXBuilder(false);
        Document doc = saxBuilder.build(path);
        Element rootElement = doc.getRootElement();

        // 常见操作
        //JDOM给了我们很多很灵活的使用方法来管理子元素（这里的List是java.util.List）
        List allChildren = rootElement.getChildren();
        // 获得指定名称子元素的list
        List namedChildren = rootElement.getChildren("classGridlb");
        // 获得指定名称的第一个子元素
        Element child = rootElement.getChild("classGridlb");

        // 删除第2个子元素
        allChildren.remove(1);
        // 删除名字为A的子元素
        allChildren.removeAll(rootElement.getChildren("A"));
        // 等效删除名字为A的子元素
        rootElement.removeChildren("A");
        // 加入B节点
        allChildren.add(new Element("B"));
        // 等效加入B节点
        rootElement.addContent(new Element("B"));
        allChildren.add(0, new Element("C"));
        // 修改名称
        rootElement.setName("king");
        // 修改内容
        rootElement.setText("A new description");

        try {
            parse2Obj(path);

            //在开发测试模式时，得到的地址为：{项目跟目录}/target/static/images/upload/
            //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/images/upload/
            String path1 = ResourceUtils.getURL("classpath:").getPath();
            System.out.println("path1:" + path1);
//            buildXML(path1);

            String path2 = Thread.currentThread().getContextClassLoader().getResource(".").getPath();
            System.out.println("path2:" + path2);
            // 获取跟目录
            File path3 = new File(ResourceUtils.getURL("classpath:").getPath());
            if(!path3.exists()) {
                path3 = new File("");
            }
            System.out.println("path3:" + path3.getAbsolutePath());

            // 如果上传目录为/static/images/upload/，则可以如下获取：
            File upload = new File(path3.getAbsolutePath(),"static/images/upload/");
            if(!upload.exists()) {
                upload.mkdirs();
            }
            System.out.println("upload url:" + upload.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        createXml();
    }

//    public static <T> T xml2Obj(Class<T> pojo, String path) {
//        SAXBuilder saxBuilder = new SAXBuilder(false);
//        Document doc = saxBuilder.build(path);
//        Element rootElement = doc.getRootElement();
//
//    }

    /**
     * 解析xml为对象
     * @param path
     */
    public static void parse2Obj(String path) {
        try {

            SAXBuilder saxBuilder = new SAXBuilder(false);
            Document doc = saxBuilder.build(path);
            Element rootElement = doc.getRootElement();

            List<StudentGridlb> studentGridlbList = new ArrayList<>();
            for (Object classGridlb : rootElement.getChildren("classGridlb")) {
                Element classGridlbEle = (Element) classGridlb;

                for (Object studentGrid : classGridlbEle.getChild("studentGrid").getChildren("studentGridlb")) {
                    Element studentGridEle = (Element) studentGrid;
                    StudentGridlb studentGridlb = new StudentGridlb();
                    studentGridlb.setId(studentGridEle.getChildTextTrim("stu_id"));
                    studentGridlb.setAge(Integer.parseInt(studentGridEle.getChildTextTrim("stu_age")));
                    studentGridlb.setName(studentGridEle.getChildTextTrim("stu_name"));
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    studentGridlb.setBirthday(format.parse(studentGridEle.getChildTextTrim("stu_birthday")));
                    studentGridlbList.add(studentGridlb);
                }
            }

            System.out.println(">>>>>>>>>>>>>>> " + studentGridlbList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建xml
     * @param path
     */
    public static void buildXML(String path) {

        Element root,student,number,name,age;
        //生成根元素：student-info
        root = new Element("student-info");
        //生成元素：student(number,name,age)
        student = new Element("student");
        number = new Element("number");
        name = new Element("name");
        age = new Element("age");
        //将根元素植入文档doc中
        Document doc = new Document(root);

        number.setText("001");
        name.setText("king");
        age.setText("24");
        student.addContent(number);
        student.addContent(name);
        student.addContent(age);
        root.addContent(student);
        Format format = Format.getCompactFormat();
        //设置xml文件的字符为gb2312
        format.setEncoding("gb2312");
        //设置xml文件的缩进为4个空格
        format.setIndent("    ");
        //元素后换行一层元素缩四格
        XMLOutputter XMLOut = new XMLOutputter(format);
        try {
            XMLOut.output(doc, new FileOutputStream(path + "student.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建xml
     */
    public static void createXml() {
        try {
            Document doc = new Document();
            ProcessingInstruction pi=new ProcessingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"test.xsl\"");
            doc.addContent(pi);
            Namespace ns = Namespace.getNamespace("http://www.bromon.org" );
            Namespace ns2 = Namespace.getNamespace("other", "http://www.w3c.org" );
            Element root = new Element("根元素", ns);
            root.addNamespaceDeclaration(ns2);
            doc.setRootElement(root);
            Element el1 = new Element("元素一");
            el1.setAttribute("属性", "属性一");
            Text text1=new Text("元素值");
            Element em = new Element("元素二").addContent("第二个元素");
            el1.addContent(text1);
            el1.addContent(em);
            Element el2 = new Element("元素三").addContent("第三个元素");
            root.addContent(el1);
            root.addContent(el2);

            //缩进四个空格,自动换行,gb2312编码
            Format format = Format.getCompactFormat();
            //设置xml文件的字符为gb2312
            format.setEncoding("GB2312");
            //设置xml文件的缩进为4个空格
            format.setIndent("    ");
            XMLOutputter outPutter = new XMLOutputter(format);
            outPutter.output(doc, new FileWriter("test2.xml"));
        }catch(Exception e)  {
            e.printStackTrace();
        }
    }

    /**
     * XSLT格式转换
     * @param stylesheet
     * @param in
     * @return
     * @throws JDOMException
     */
    public static Document transform(String stylesheet, Document in) throws JDOMException {
        try {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(stylesheet));
            JDOMResult out = new JDOMResult();
            transformer.transform(new JDOMSource(in), out);

            return out.getDocument();
        } catch (TransformerException e) {
            throw new JDOMException("XSLT Trandformation failed", e);
        }
    }

}
