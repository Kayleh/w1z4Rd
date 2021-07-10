package com.kayleh;

import java.util.HashMap;
import java.util.Map;


/**
 * 专门用于保存资源文件path ： 修改时间的工厂类，实质上是维护着一个map
 */
public class ConfigFileModifiedFactory {

    private static ConfigFileModifiedFactory configFactory = new ConfigFileModifiedFactory();
    private Map<String, Long> fileModifiedMap = new HashMap<String, Long>();

    public static ConfigFileModifiedFactory getInstance() {
        return configFactory;
    }

    public void put(String key, long lastModified) {
        fileModifiedMap.put(key, lastModified);
    }

    public Long get(String key) {
        return fileModifiedMap.get(key);
    }

}
