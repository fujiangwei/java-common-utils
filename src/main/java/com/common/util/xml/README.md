# 1、XStream使用详解
```text
Xstream是一种OXMapping 技术，是用来处理XML文件序列化的框架,将JavaBean序列化，或将XML文件反序列化操作简单，上手快
```

## 1、注解使用

> 设置应用注解两种配置方式：应用某个JavaBean类的注解或自动使用JavaBean类的注解

    // 解析注解，应用到相应的对象上
    xStream.processAnnotations(obj.getClass());
    //自动检测注解
    xstream.autodetectAnnotations(true);
    
> 重命名注解：@XStreamAlias()
```text
类上注解等效xStream.alias()方法
字段上注解等效xStream.aliasField()方法
```
> 省略集合根节点：@XStreamImplicit
```text
等效xStream.addImplicitCollection()方法
```
> 把字段节点设置成属性：@XStreamAsAttribute
```text
等效xStream.useAttributeFor()方法
```
> 忽略字段：@XStreamOmitField
```text
等效xStream.omitField()方法
```
> 设置转换器：@XStreamConverter()
```text
等效转换器注册 xStream.registerConverter(new YourConverter());
常见转化器有：
    SingleValueConverter：单值转换接口
    AbstractSingleValueConverter：单值转换抽象类
    Converter：常规转换器接口
```

# 2、dom4j
```text
dom4j是一套非常优秀的java开源api，主要用于读写xml文档，具有性能优异、功能强大和方便等特点
```
> 常用操作

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

# 3、Jdom
```text
JDOM是一个开源项目，它基于树型结构，利用纯JAVA的技术对XML文档实现解析、生成、序列化以及多种操作。
```
> 1、org.jdom这个包里的类是你J解析xml文件后所要用到的所有数据类型。

* Attribute
* CDATA
* Coment
* DocType
* Document
* Element
* EntityRef
* Namespace
* ProscessingInstruction
* Text

> 2、org.jdom.transform在涉及xslt格式转换时应使用下面的2个类

* JDOMSource
* JDOMResult
* org.jdom.input

> 3、输入类，一般用于文档的创建工作

* SAXBuilder
* DOMBuilder
* ResultSetBuilder
* org.jdom.output

> 4、输出类，用于文档转换输出

* XMLOutputter
* SAXOutputter
* DomOutputter
* JTreeOutputter

> 5、 常用操作

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


参照：

[1、浅谈 Java 主流开源类库解析 XML](https://www.cnblogs.com/java-class/p/6901910.html)

[2、Jdom使用](https://blog.csdn.net/qq_27376871/article/details/53178366)

[3、xml技术解决方案（dom4j/xstream）](https://blog.csdn.net/thunder09/article/details/5555946)




