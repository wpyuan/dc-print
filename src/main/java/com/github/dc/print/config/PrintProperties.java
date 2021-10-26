package com.github.dc.print.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    /**
     * 水印配置
     */
    private WatermarkProperties watermark = new WatermarkProperties();

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public class WatermarkProperties {
        /**
         * 水印字号，默认18号
         */
        private Integer fontSize = 18;
        /**
         * 不透明度 0~1，越靠近0越透明，默认0.125
         */
        private Float opacity = 0.125f;
    }

}
