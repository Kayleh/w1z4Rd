package com.kayleh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.ContextLoader;


public class SpringBeanReloadJob implements Runnable {

    private static final Logger log = LogManager
            .getLogger(SpringBeanReloadJob.class);

    @Scheduled
    public void run() {
        try {
            XmlRefreshWebApplicationContext applicationContext = (XmlRefreshWebApplicationContext) ContextLoader
                    .getCurrentWebApplicationContext();
            applicationContext.reload();
        } catch (Exception ex) {
            log.error("Sping重载配置失败", ex);
        }
    }

}
