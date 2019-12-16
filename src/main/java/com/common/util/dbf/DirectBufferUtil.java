package com.common.util.dbf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class DirectBufferUtil {
  
    final static Logger logger = LoggerFactory.getLogger(DirectBufferUtil.class);
  
    /** 
     * hidden Construct for DirectBufferUtil.java. 
     */  
    private DirectBufferUtil() {  
  
    }  
  
    /** 
     * 清除并释放DirectBuffer. 释放对应的channel后的资源. 
     *  
     * @param buffer 
     * @return 
     */  
    public static boolean clean(final ByteBuffer buffer) {
        if ((buffer == null) || !buffer.isDirect()) {
            return false;
        }
        final Boolean b = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {  
                Boolean success = Boolean.FALSE;  
                try {  
                    Method getCleanerMethod = buffer.getClass().getMethod("cleaner", (Class[]) null);
                    getCleanerMethod.setAccessible(true);  
                    Object cleaner = getCleanerMethod.invoke(buffer, (Object[]) null);  
                    Method clean = cleaner.getClass().getMethod("clean", (Class[]) null);  
                    clean.invoke(cleaner, (Object[]) null);  
                    success = Boolean.TRUE;  
                } catch (Exception e) {  
                    // logger error  
                    System.out.println(e.toString());  
                    logger.error("clean fails for below ");  
                    logger.error(e.toString(),e);  
                }  
                return success;  
            }  
        });  
  
        return b.booleanValue();  
    }  
}