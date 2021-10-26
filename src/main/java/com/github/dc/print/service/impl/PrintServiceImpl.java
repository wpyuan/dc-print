package com.github.dc.print.service.impl;

import com.github.dc.print.handler.IPrint;
import com.github.dc.print.handler.PrintHandlerManager;
import com.github.dc.print.helper.PdfHelper;
import com.github.dc.print.service.IPrintService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 *     打印接口服务实现类
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/20 8:27
 */
@Service
@AllArgsConstructor
@Slf4j
public class PrintServiceImpl<T> implements IPrintService<T> {

    private final PrintHandlerManager printHandlerManager;
    private final PdfHelper pdfHelper;

    @Override
    public void print(String code, T businessKey, OutputStream os, Boolean enableWatermark, String watermarkContent) {
        IPrint printHandler = printHandlerManager.get(code);
        if (printHandler == null) {
            throw new NullPointerException("无对应打印处理器");
        }
        Map<String, Object> data = printHandler.data(businessKey);
        try {
            this.print(data, printHandler.template(), os, enableWatermark, watermarkContent);
        } catch (Exception e) {
            printHandler.handleException(code, businessKey, os, e);
        }
    }

    @Override
    public void print(Map<String, Object> data, String htmlTmpName, OutputStream os, Boolean enableWatermark, String watermarkContent) {
        pdfHelper.exportPdf(data, htmlTmpName, os, enableWatermark, watermarkContent);
    }

    @Override
    public void batchPrint(String code, List<T> businessKey, OutputStream os, Boolean enableWatermark, String watermarkContent) {
        IPrint printHandler = printHandlerManager.get(code);
        if (printHandler == null) {
            throw new NullPointerException("无对应打印处理器");
        }
        try (CheckedOutputStream cos = new CheckedOutputStream(os, new CRC32()); ZipOutputStream zos = new ZipOutputStream(cos);){
            zos.setLevel(9);
            for (T key : businessKey) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
                    Map<String, Object> data = printHandler.data(key);
                    this.print(data, printHandler.template(), baos, enableWatermark, watermarkContent);

                    // 写入zip
                    String customFileName = printHandler.customFileNameWhenBatchCompress(key, data, String.valueOf(key));
                    ZipEntry zipEntry = new ZipEntry(customFileName + ".pdf");
                    zos.putNextEntry(zipEntry);
                    IOUtils.write(baos.toByteArray(), zos);
                    zos.closeEntry();
                } catch (Exception e) {
                    printHandler.handleException(code, key, os, e);
                }

            }

            zos.finish();
        } catch (IOException e) {
            printHandler.handleException(code, businessKey, os, e);
        }

    }
}
