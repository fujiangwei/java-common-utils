package com.common.util.xml.xstream.convert;

import com.thoughtworks.xstream.converters.SingleValueConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XStreamDateConverter implements SingleValueConverter {

    @Override
    public boolean canConvert(Class arg0) {
        return Date.class == arg0;
    }

    @Override
    public Object fromString(String arg0) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(arg0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString(Object arg0) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.format((Date) arg0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}