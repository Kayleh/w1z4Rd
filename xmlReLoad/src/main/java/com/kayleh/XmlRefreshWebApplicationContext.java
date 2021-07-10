package com.kayleh;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 扩展Xbean包中的XmlWebApplication类，使之支持热加载。
 * @Author: Kayleh
 * @Date: 2021/7/9 19:53
 * @Version: 1.0
 */
public class XmlRefreshWebApplicationContext extends XmlWebApplicationContext {
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
    private static String springClassPathPrefix = "classpath:";

    public void reload() {
        //重新获得web.xml文件中"contextConfigLocation"参数（被加载的bean目录）
        String location = this.getServletContext().getInitParameter(CONFIG_LOCATION_PARAM);
        //通过特定的符号，；\n对参数进行分割，得出各个的路径
        String[] locations = StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS);
        //获得被修改过的location，然后往下再重新加载那些被修改过的xml文件
        List<String> moidifiedLocationList = getModifiedLocation(locations);
        if (null != moidifiedLocationList && moidifiedLocationList.size() != 0) {
            this.setConfigLocations(StringUtils.toStringArray(moidifiedLocationList));
            doRefresh();
        }
    }

    /**
     * 鉴定资源文件(*.xml)是否已经发生更改
     *
     * @param locations
     * @return
     */
    public List<String> getModifiedLocation(String[] locations) {
        List<String> refreshLocationList = new ArrayList<String>();
        List<NoCacheClassPathResource> actualResources = new ArrayList<NoCacheClassPathResource>();
        ConfigFileModifiedFactory fileModifiedFactory = ConfigFileModifiedFactory.getInstance();
        try {
            /**
             * 获得资源文件对象（Resource）
             */
            for (String location : locations) {
                Resource[] resources = ((ResourcePatternResolver) this).getResources(location);
                for (Resource resource : resources) {
                    actualResources.add((NoCacheClassPathResource) resource);
                }
            }
            /**
             * 通过逐个比较资源的最近修改时间来判断文件是否已经更改
             */
            for (NoCacheClassPathResource resource : actualResources) {
                Long fileLastModified = fileModifiedFactory.get(resource.getPath());
                if (fileLastModified != null && fileLastModified != resource.lastModified()) {
                    //已经发生修改，就把路径写入refreshLocationList的名单中，稍后reload
                    refreshLocationList.add(springClassPathPrefix + resource.getPath());
                }
                //更新所有资源文件的最近修改时间
                fileModifiedFactory.put(resource.getPath(), resource.lastModified());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return refreshLocationList;
    }

    public void doRefresh() {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.getBeanFactory();
        try {
            this.loadBeanDefinitions(beanFactory);
            finishBeanFactoryInitialization(beanFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据路径获得资源文件
     *
     * @param location
     * @return
     */
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new NoCacheClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        } else {
            try {
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException ex) {
                return getResourceByPath(location);
            }


        }
    }
}
