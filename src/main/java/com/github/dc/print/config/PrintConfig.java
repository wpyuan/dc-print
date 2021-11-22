package com.github.dc.print.config;

import com.github.dc.print.helper.ExportExcelHelper;
import com.github.dc.print.helper.PdfHelper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *     打印配置
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/20 15:28
 */
@Configuration
@Slf4j
public class PrintConfig {

    @Bean
    @ConditionalOnMissingBean
    public PdfHelper pdfHelper(PrintProperties printProperties) {

        freemarker.template.Configuration freemarkerCfg = new freemarker.template.Configuration(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        //freemarker的模板目录
        ClassPathResource classPathResource = new ClassPathResource(printProperties.getTemplateClassPath());
        freemarkerCfg.setClassLoaderForTemplateLoading(classPathResource.getClassLoader(), printProperties.getTemplateClassPath());
        List<String> fonts = printProperties.getFontClassPath();
        fonts.add("fonts/simsun.ttc");
        BaseFont watermarkFont = null;
        try {
            watermarkFont = BaseFont.createFont("fonts/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException | IOException e) {
            log.warn("水印字体加载失败，请检查！路径：fonts/simsun.ttc", e);
        }
        return new PdfHelper(freemarkerCfg, printProperties.getEncoding(), fonts, watermarkFont, printProperties.getWatermark().getFontSize(), printProperties.getWatermark().getOpacity());
    }

    @Bean
    @ConditionalOnMissingBean
    public ExportExcelHelper exportExcelHelper(PrintProperties printProperties) {
        return new ExportExcelHelper(printProperties.getTemplateClassPath());
    }
}
