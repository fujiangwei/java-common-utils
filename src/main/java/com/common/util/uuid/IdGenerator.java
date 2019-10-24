package com.common.util.uuid;

public interface IdGenerator {
      
    /** 
     * 生成下一个不重复的流水号 
     * @return 
     */  
    String next();  
}