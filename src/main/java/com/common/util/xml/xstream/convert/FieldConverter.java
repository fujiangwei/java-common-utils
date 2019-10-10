package com.common.util.xml.xstream.convert;

import com.common.util.xml.utils.DomainUtil;
import com.common.util.xml.annotations.FieldValue;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 文件描述 FieldConverter
 **/
public class FieldConverter implements Converter {

    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldConverter.class);

    /**
     * 时间格式
     */
    private String formatPattern;

    public FieldConverter() {
    }

    public FieldConverter(String formatPattern) {
        this.formatPattern = formatPattern;
    }

    @Override
    public boolean canConvert(Class type) {
//        return type.equals(InsDirPackage.class) || type.equals(ExecSQLPackage.class);
        return true;
    }

    /**
     * 将java对象转为xml时使用
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        try {
            handleSource(source, writer);
        } catch (Exception e) {
            LOGGER.error("[FieldConverter] marshal 异常，{}", e);
            throw new IllegalArgumentException("转换xml出错");
        }
    }

    /**
     * @description ：处理对象为xml转换
     * @param source 对象源
     * @param writer 流写出对象
     * @date  2019/9/29 18:48
     * @modifier
     * @return  void
     */
    private void handleSource(Object source, HierarchicalStreamWriter writer) throws IllegalAccessException {

        try {
            Class<?> aClass = source.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field currField : declaredFields) {
                currField.setAccessible(Boolean.TRUE);
                // 字段注解
                boolean isAttr = currField.isAnnotationPresent(XStreamAsAttribute.class);
                boolean isXStreamAlias = currField.isAnnotationPresent(XStreamAlias.class);
                boolean isFieldValue = currField.isAnnotationPresent(FieldValue.class);

                // 当前字段值
                Object curFieldValue = currField.get(source);

                // 字段别名
                String xmlAlias;
                if (isXStreamAlias) {
                    xmlAlias = currField.getAnnotation(XStreamAlias.class).value();
                } else {
                    xmlAlias = currField.getName();
                }

                if (isAttr) {
                    // 字段属性
                    writer.addAttribute(xmlAlias, null == curFieldValue ? "" : String.valueOf(curFieldValue));
                } else if (isFieldValue) {
                    // 字段值
                    writer.setValue(String.valueOf(curFieldValue));
                } else {
                    // 字段简单处理
                    boolean isBaseType = DomainUtil.isBaseType(currField.getType());
                    if (curFieldValue instanceof List) {
                        List<Object> list = (List<Object>) curFieldValue;
                        for (int i = 0; i < list.size(); i++) {
                            writer.startNode(xmlAlias + i);
                            Object curr = list.get(i);
                            isBaseType = DomainUtil.isBaseType(curr.getClass());
                            if (null == curr || isBaseType) {
                                writer.setValue(null == curr ? "" : String.valueOf(curr));
                            } else {
                                handleSource(curr, writer);
                            }
                            writer.endNode();
                        }
                    } else {
                        // 开始节点
                        writer.startNode(xmlAlias);

                        if (null == curFieldValue || isBaseType) {
                            writer.setValue(null == curFieldValue ? "" : String.valueOf(curFieldValue));
                        } else if (currField.getType().equals(Date.class)) { //时间对象
                            SimpleDateFormat format = new SimpleDateFormat(formatPattern);
                            writer.setValue(null == curFieldValue ? "" : format.format(curFieldValue));
                        } else { //对象类型
                            handleSource(curFieldValue, writer);
                        }

                        // 结束节点
                        writer.endNode();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("[FieldConverter] handleSource 异常，{}", e);
            throw e;
        }
    }

    /**
     * 将xml转为java对象使用
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        System.out.println("coming.....");
        return null;
    }
}