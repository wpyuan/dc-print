package com.github.dc.print.utils;

import com.github.dc.print.pojo.QrcodeConfig;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.qrcode.EncodeHintType;
import com.itextpdf.text.pdf.qrcode.ErrorCorrectionLevel;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 二维码工具类
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/27 9:56
 */
@UtilityClass
@Slf4j
public class QRCodeUtils {

    /**
     * 添加二维码
     *
     * @param baos         含有pdf内容的输出流
     * @param os           添加二维码后的输出流
     * @param font         字体
     * @param qrcodeConfig 二维码配置
     */
    public static void add(ByteArrayOutputStream baos, OutputStream os, BaseFont font, QrcodeConfig qrcodeConfig) {
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(baos.toByteArray());
            stamper = new PdfStamper(reader, os);
            PdfContentByte contentByte = null;
            Rectangle rectangle = null;
            if (qrcodeConfig.getIsQrcodeLocationAllPage()) {
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    rectangle = reader.getPageSize(i);
                    contentByte = stamper.getOverContent(i);
                    addQrcodeImg(contentByte, qrcodeConfig, rectangle.getWidth(), rectangle.getHeight());
                }
            } else {
                if (CollectionUtils.isEmpty(qrcodeConfig.getQrcodeLocationPageNumbers())) {
                    // 默认在首页处添加
                    qrcodeConfig.setQrcodeLocationPageNumbers(Arrays.asList(1));
                }
                for (Integer pageNumber : qrcodeConfig.getQrcodeLocationPageNumbers()) {
                    rectangle = reader.getPageSize(pageNumber);
                    contentByte = stamper.getOverContent(pageNumber);
                    addQrcodeImg(contentByte, qrcodeConfig, rectangle.getWidth(), rectangle.getHeight());
                }
            }
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("二维码添加异常", e);
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

    private static void addQrcodeImg(PdfContentByte contentByte, QrcodeConfig qrcodeConfig, float pageWidth, float pageHeight) throws DocumentException {
        Map<EncodeHintType, Object> hints = new HashMap<>(2);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BarcodeQRCode qrcode = new BarcodeQRCode(qrcodeConfig.getQrcodeContent(), 1, 1, hints);
        Image qrcodeImage = qrcode.getImage();
        // 显示位置
        if (qrcodeConfig.getQrcodeAbsoluteX() == null) {
            qrcodeConfig.setQrcodeAbsoluteX(Math.round(pageWidth - 10 - qrcodeConfig.getQrcodeSize()));
        }
        if (qrcodeConfig.getQrcodeAbsoluteY() == null) {
            qrcodeConfig.setQrcodeAbsoluteY(Math.round(pageHeight - 10 - qrcodeConfig.getQrcodeSize()));
        }
        qrcodeImage.setAbsolutePosition(qrcodeConfig.getQrcodeAbsoluteX(), qrcodeConfig.getQrcodeAbsoluteY());
        // 显示比例%
        qrcodeImage.scalePercent(qrcodeConfig.getQrcodeSize() / qrcodeImage.getWidth() * 100);
        contentByte.addImage(qrcodeImage);
    }
}
