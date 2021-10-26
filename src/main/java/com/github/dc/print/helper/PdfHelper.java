package com.github.dc.print.helper;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.*;

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
    private BaseFont watermarkFont;
    private Integer watermarkFontSize;
    private Float watermarkOpacity;

    /**
     * 由html模板导出pdf
     *
     * @param data             数据
     * @param htmlTmp          模板
     * @param os               输出流
     * @param enableWatermark  是否启用水印
     * @param watermarkContent 水印内容
     */
    public void exportPdf(Map<String, Object> data, String htmlTmp, OutputStream os, Boolean enableWatermark, String watermarkContent) {
        try {
            createPdf(freeMarkerRender(data, htmlTmp), os, enableWatermark, watermarkContent);
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
            Template template = freemarkerCfg.getTemplate(htmlTmp, encoding);
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
     *
     * @param content          内容
     * @param os               输出流
     * @param enableWatermark  是否启用水印
     * @param watermarkContent 水印内容
     * @throws IOException
     * @throws com.lowagie.text.DocumentException
     */
    public void createPdf(String content, OutputStream os, Boolean enableWatermark, String watermarkContent) throws IOException, com.lowagie.text.DocumentException {
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        render.createPDF(baos);

        // 水印处理
        if (enableWatermark) {
            this.addWatermark(baos, os, watermarkContent);
            return;
        }

        os.write(baos.toByteArray());
    }

    /**
     * 添加水印
     *
     * @param baos             含有pdf内容的字节输出流
     * @param os               添加水印后的最终输出流
     * @param watermarkContent 水印内容
     */
    public void addWatermark(ByteArrayOutputStream baos, OutputStream os, String watermarkContent) {
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(baos.toByteArray());
            stamper = new PdfStamper(reader, os);
            // 设置透明度
            PdfGState gs = this.getPdfGState();
            Rectangle rectangle = null;
            PdfContentByte contentByte = null;
            Set<Pair<Float, Float>> location = new HashSet<>();
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                rectangle = reader.getPageSize(i);
                int pageHeight = Math.round(rectangle.getHeight());
                int pageWidth = Math.round(rectangle.getWidth());
                // 内容下层
                contentByte = stamper.getUnderContent(i);
                contentByte.beginText();
                // 字体添加透明度
                contentByte.setGState(gs);
                // 添加字体大小
                contentByte.setFontAndSize(watermarkFont, watermarkFontSize);
                // 设置水印布局
                int countChinese = this.countChinese(watermarkContent);
                double contentLength = Math.round(countChinese * watermarkFontSize) + (watermarkContent.length() - countChinese) * watermarkFontSize * 0.5;
                // 间隔空隙长度与内容长度的比例 1~3
                double spacingPercent = this.getSpacingPercent(contentLength);
                long blockLength = Math.round((watermarkFontSize + contentLength / Math.sqrt(2)) * spacingPercent);
                long widthCount = pageWidth / blockLength + 2;
                long heightCount = pageHeight / blockLength + 2;
                for (int j = 0; j < widthCount; j++) {
                    for (int k = 0; k < heightCount; k++) {
                        contentByte.showTextAligned(Element.ALIGN_CENTER, watermarkContent, j * blockLength, k * blockLength, 45);
                        float x = (float) ((j + 1 / 3) * blockLength);
                        float y = (float) ((k + 0.5) * blockLength);
                        if (!location.contains(Pair.of(x, y))) {
                            contentByte.showTextAligned(Element.ALIGN_CENTER, watermarkContent, x, y, 45);
                            location.add(Pair.of(x, y));
                        }
                        x = (float) ((j + 2 / 3) * blockLength);
                        if (!location.contains(Pair.of(x, y))) {
                            contentByte.showTextAligned(Element.ALIGN_CENTER, watermarkContent, x, y, 45);
                            location.add(Pair.of(x, y));
                        }
                    }
                }
                contentByte.endText();
            }
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("水印添加异常", e);
        } finally {
            // 关闭
            if (stamper != null) {
                try {
                    stamper.close();
                } catch (DocumentException | IOException e) {
                    log.warn("添加水印，流关闭异常", e);
                }
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    private PdfGState getPdfGState() {
        // 设置透明度
        PdfGState gs = new PdfGState();
        // 非描边
        gs.setFillOpacity(watermarkOpacity);
        // 描边
        gs.setStrokeOpacity(watermarkOpacity);
        return gs;
    }

    private int countChinese(String string) {
        String regex = "[\u4e00-\u9fa5]";
        return string.length() - string.replaceAll(regex, "").length();
    }

    private double getSpacingPercent(double contentLength) {
        double spacingPercent = -1;
        int maxLength = 288;
        int minLength = 36;
        int midLength = (maxLength - minLength) / 2 + minLength;
        // 间隔空隙长度与内容长度的比例 3~1
        int maxSpacingPercent = 3;
        int minSpacingPercent = 1;
        int midSpacingPercent = (maxSpacingPercent - minSpacingPercent) / 2 + minSpacingPercent;
        if (contentLength >= maxLength) {
            spacingPercent = 1;
        } else if (contentLength <= minLength) {
            spacingPercent = 3;
        } else {
            if (contentLength > midLength) {
                // 下半区
                spacingPercent = ((contentLength - midLength) / (maxLength - midLength)) * (minSpacingPercent - midSpacingPercent) + midSpacingPercent;
            } else if (contentLength < midLength) {
                // 上半区
                spacingPercent = ((midLength - contentLength) / (midLength - minLength)) * (midSpacingPercent - maxSpacingPercent) + maxSpacingPercent;
            } else {
                spacingPercent = midSpacingPercent;
            }
        }

        return spacingPercent;
    }
}