package com.common.util.xml.xstream.convert;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义XStream转化器，
 * 使null值标签可以输出到XML
 */
public class NullConverter implements Converter {
    private Map<Class<?>, List<String>> attributes = null;

    public void regAttribute(Class<?> type, String attribute) {
        if (null == attributes) {
            attributes = new HashMap<Class<?>, List<String>>();
        }

        List value = attributes.get(type);
        if (null == value) {
            value = new ArrayList<String>();
            attributes.put(type, value);
        }

        value.add(attribute);
    }

    /**
     * 是否是属性（是属性的不用以单独标签实现）
     *
     * @param type
     * @param attribute
     * @return
     */
    private boolean isClassAttribute(Class<?> type, String attribute) {
        List<String> value = getAttributes(type);
        if (null == value) {
            return false;
        }

        if (value.contains(attribute)) {
            return true;
        }
        return false;
    }

    /**
     * 获取注册的属性
     *
     * @param type
     * @return
     */
    private List<String> getAttributes(Class<?> type) {
        if (null != attributes) {
            return attributes.get(type);
        }
        return null;
    }

    /**
     * 输出对象的属性标签
     *
     * @param source
     * @param writer
     */
    private void writerAttribute(Object source, HierarchicalStreamWriter writer) {
        Class cType = source.getClass();
        List<String> value = getAttributes(cType);
        if ((null != value) && (value.size() > 0)) {
            Method[] methods = cType.getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.indexOf("get") != -1 && methodName != "getClass") {
                    String name = methodName.substring(3);
                    name = name.toLowerCase();
                    if (value.contains(name)) {
                        Object o = null;
                        try {
                            o = method.invoke(source, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        writer.addAttribute(name, o == null ? "" : o.toString());
                    }
                }
            }
        }
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
        if (null == source) {
            return;
        }

        Class cType = source.getClass();
        Method[] methods = cType.getMethods();
        writerAttribute(source, writer);
        for (Method m : methods) {
            String methodName = m.getName();
            if (methodName.indexOf("get") != -1 && methodName != "getClass") {
                if (source instanceof List) {
                    List list = (List) source;
                    for (Object obj : list) {
                        String name = obj.getClass().toString();
                        name = name.substring(name.lastIndexOf(".") + 1);

                        writer.startNode(name);
                        marshal(obj, writer, context);
                        writer.endNode();
                    }
                } else {
                    boolean isBaseType = isBaseType(m.getReturnType());
                    String name = methodName.substring(3);
                    if (isBaseType) {
                        name = name.toLowerCase();
                    }
                    Object o = null;
                    try {
                        o = m.invoke(source, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //如果是基本类型调用toString，否则递归
                    if (isBaseType) {
                        if (!isClassAttribute(cType, name)) {
                            writer.startNode(name);
                            writer.setValue(o == null ? "" : o.toString());
                            writer.endNode();
                        }
                    } else {
                        writer.startNode(name);
                        marshal(o, writer, context);
                        writer.endNode();
                    }
                }
            }
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return null;
    }

    @Override
    public boolean canConvert(Class type) {
        return true;
    }

    private boolean isBaseType(Class<?> type) {
        if (type.equals(Integer.class)
                || type.equals(Double.class)
                || type.equals(String.class)
                || type.equals(Boolean.class)
                || type.equals(Long.class)
                || type.equals(Short.class)
                || type.equals(Byte.class)
                || type.equals(Float.class)) {
            return true;
        }
        return false;
    }
}