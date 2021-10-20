package com.github.dc.print.service.impl;

import com.github.dc.print.handler.IPrint;
import com.github.dc.print.handler.PrintHandlerManager;
import com.github.dc.print.helper.PdfHelper;
import com.github.dc.print.service.IPrintService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.Map;

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
public class PrintServiceImpl<T> implements IPrintService<T> {

    private final PrintHandlerManager printHandlerManager;
    private final PdfHelper pdfHelper;

    @Override
    public void print(String code, T businessKey, OutputStream os) {
        IPrint printHandler = printHandlerManager.get(code);
        if (printHandler == null) {
            throw new NullPointerException("无对应打印处理器");
        }
        Map<String, Object> data = printHandler.data(businessKey);
        try {
            this.print(data, printHandler.template(), os);
        } catch (Exception e) {
            printHandler.handleException(code, businessKey, os, e);
        }
    }

    @Override
    public void print(Map<String, Object> data, String htmlTmpName, OutputStream os) {
        pdfHelper.exportPdf(data, htmlTmpName, os);
    }
}
