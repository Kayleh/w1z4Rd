package com.kayleh;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * 路径指定资源文件，通过路径去获得资源文件的流
 */
public class NoCacheClassPathResource extends ClassPathResource {

    public NoCacheClassPathResource(String path, ClassLoader classLoader) {
        super(path, classLoader);
    }

    public InputStream getInputStream() throws IOException {
        InputStream is = null;
        is = this.getClassLoader().getResource(this.getPath()).openStream();
        return is;
    }

}
