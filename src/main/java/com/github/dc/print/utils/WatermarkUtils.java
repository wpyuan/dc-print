package com.github.dc.print.utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 水印工具类
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/27 9:45
 */
@UtilityClass
@Slf4j
public class WatermarkUtils {

    /**
     * 添加水印
     *
     * @param baos              含有pdf内容的字节输出流
     * @param os                添加水印后的最终输出流
     * @param watermarkContent  水印内容
     * @param watermarkFont     水印字体
     * @param watermarkFontSize 水印字号
     * @param gs                水印透明度
     */
    public static void add(ByteArrayOutputStream baos, ByteArrayOutputStream os, String watermarkContent, BaseFont watermarkFont, Integer watermarkFontSize, PdfGState gs) {
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(baos.toByteArray());
            stamper = new PdfStamper(reader, os);
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
                int countChinese = countChinese(watermarkContent);
                double contentLength = Math.round(countChinese * watermarkFontSize) + (watermarkContent.length() - countChinese) * watermarkFontSize * 0.5;
                // 间隔空隙长度与内容长度的比例 1~3
                double spacingPercent = getSpacingPercent(contentLength);
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

    private static int countChinese(String string) {
        String regex = "[\u4e00-\u9fa5]";
        return string.length() - string.replaceAll(regex, "").length();
    }

    private static double getSpacingPercent(double contentLength) {
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
