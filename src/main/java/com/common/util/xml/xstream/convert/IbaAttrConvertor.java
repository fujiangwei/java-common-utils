package com.common.util.xml.xstream.convert;

import com.common.util.xml.domain.IbaAttr;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class IbaAttrConvertor implements Converter {
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class clazz) {
        return clazz.equals(IbaAttr.class);
    }

    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        final IbaAttr fieldDto = (IbaAttr) value;
        writer.addAttribute(fieldDto.getText(), fieldDto.getText());
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {
        IbaAttr result = new IbaAttr();
        result.setChname(reader.getAttribute("CHName"));
        result.setEnname(reader.getAttribute("ENName"));
        result.setType(reader.getAttribute("Type"));        
        result.setText(reader.getValue());
        return result;
    }
}