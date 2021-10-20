package com.github.dc.print.helper;

import com.itextpdf.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * pdf辅助类
 *
 * @author Administrator
 */
@AllArgsConstructor
@Slf4j
public class PdfHelper {

    private Configuration freemarkerCfg;
    private String encoding;
    private List<String> fontsPath;

    /**
     * 由html模板导出pdf
     *
     * @param data    数据
     * @param htmlTmp 模板
     * @param os      输出流
     */
    public void exportPdf(Map<String, Object> data, String htmlTmp, OutputStream os) {
        try {
            createPdf(freeMarkerRender(data, htmlTmp), os);
        } catch (IOException | com.lowagie.text.DocumentException e) {
            throw new RuntimeException("打印异常-生成pdf", e);
        }
    }

    /**
     * freemarker渲染html
     *
     * @param data    渲染数据
     * @param htmlTmp 模板文件名
     * @return 渲染后的结果
     */
    public String freeMarkerRender(Map<String, Object> data, String htmlTmp) {
        try (Writer out = new StringWriter()) {
            // 获取模板,并设置编码方式
            freemarkerCfg.setEncoding(Locale.getDefault(), encoding);
            freemarkerCfg.setDefaultEncoding(encoding);
            Template template = freemarkerCfg.getTemplate(htmlTmp);
            template.setEncoding(encoding);
            // 将合并后的数据和模板写入到流中，这里使用的字符流
            template.process(data, out);
            out.flush();
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("打印失败-渲染html", e);
        }
    }

    /**
     * 生成pdf
     * @param content 内容
     * @param os 输出流
     * @throws IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void createPdf(String content, OutputStream os) throws IOException, com.lowagie.text.DocumentException {
        ITextRenderer render = new ITextRenderer();
        ITextFontResolver fontResolver = render.getFontResolver();
        ClassPathResource fontResource = null;
        for (String fontPath : fontsPath) {
            fontResource = new ClassPathResource(fontPath);
            fontResolver.addFont(fontResource.getPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        }

        // 解析html生成pdf
        render.setDocumentFromString(content);
        render.layout();
        render.createPDF(os);
    }
}