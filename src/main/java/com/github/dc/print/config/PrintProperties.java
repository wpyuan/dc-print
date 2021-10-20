package com.github.dc.print.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     打印配置属性
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/9/28 8:56
 */
@Configuration
@ConfigurationProperties(prefix = "dc.print")
@Data
public class PrintProperties {

    /**
     * 模板目录路径。resource目录下的相对路径，如：resource/print/template，则这里配置print/template
     */
    private String templateClassPath = "print/template";
    /**
     * 模板的字符集
     */
    private String encoding = "UTF-8";
    /**
     * 字体路径。resource目录下的相对路径，如：resource/print/fonts/simsun.ttc，则这里配置print/fonts/simsun.ttc, 可配置多个
     */
    private List<String> fontClassPath = new ArrayList<>();
}
