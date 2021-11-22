package com.github.dc.print.service.impl;

import com.github.dc.print.handler.IPrint;
import com.github.dc.print.handler.PrintHandlerManager;
import com.github.dc.print.helper.ExportExcelHelper;
import com.github.dc.print.service.IExportExcelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * 导出excel ServiceImpl
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/22 11:12
 */
@Service
@AllArgsConstructor
@Slf4j
public class ExportExcelServiceImpl<T> implements IExportExcelService<T> {

    private final PrintHandlerManager printHandlerManager;
    private final ExportExcelHelper exportExcelHelper;

    @Override
    public void export(String code, T businessKey, OutputStream os) {
        IPrint printHandler = printHandlerManager.get(code);
        Objects.requireNonNull(printHandler, "无对应打印处理器：" + code);
        try {
            this.export(printHandler.data(businessKey), printHandler.template(), os);
        } catch (Exception e) {
            printHandler.handleException(code, businessKey, os, e);
        }

    }

    @Override
    public void export(Map<String, Object> data, String xlsxTmpName, OutputStream os) {
        exportExcelHelper.toOutputStream(data, xlsxTmpName, os);
    }

    @Override
    public void batchExport(String code, List<T> businessKey, OutputStream os) {
        IPrint printHandler = printHandlerManager.get(code);
        Objects.requireNonNull(printHandler, "无对应打印处理器：" + code);
        try (CheckedOutputStream cos = new CheckedOutputStream(os, new CRC32()); ZipOutputStream zos = new ZipOutputStream(cos);) {
            zos.setLevel(9);
            for (T key : businessKey) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    Map<String, Object> data = printHandler.data(key);
                    this.export(data, printHandler.template(), baos);
                    // 写入zip
                    String customFileName = printHandler.customFileNameWhenBatchCompress(key, data, String.valueOf(key));
                    ZipEntry zipEntry = new ZipEntry(customFileName + ".xlsx");
                    zos.putNextEntry(zipEntry);
                    IOUtils.write(baos.toByteArray(), zos);
                    zos.closeEntry();
                } catch (Exception e) {
                    printHandler.handleException(code, key, os, e);
                }
            }
            zos.finish();
        } catch (IOException e) {
            log.warn("批量打印，流关闭异常", e);
        }
    }
}
