package com.common.util.xml.xstream.convert;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class LongConverter implements Converter {
    @Override
    public boolean canConvert(Class arg0) {
        return Long.class == arg0;
    }

    @Override
    public void marshal(Object arg0, HierarchicalStreamWriter arg1, MarshallingContext arg2) {
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext arg1) {
        Long longNum = new Long("1");
        System.out.println(reader.getValue());
        if ("".equals(reader.getValue())) {
            longNum = null;
            return longNum;
        } else {
            longNum = longNum.valueOf(reader.getValue());
            return longNum;
        }
    }

}