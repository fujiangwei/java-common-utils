package com.common.util.xml.xstream.convert;

import com.common.util.xml.domain.Query;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class QueryConverter implements Converter {
    @Override
    public boolean canConvert(Class type) {
        return type.equals(Query.class);
    }

    /**
     * 将java对象转为xml时使用
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Query query = (Query) source;
        // 设置属性值
        writer.addAttribute("TransactionCode", query.getTransactionCode());
        // 设置文本值
        writer.setValue(query.getQuerySql());
    }

    /**
     * 将xml转为java对象使用
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Query query = new Query();
        query.setTransactionCode(reader.getAttribute("TransactionCode"));
        query.setQuerySql(reader.getValue());
        return query;
    }
}