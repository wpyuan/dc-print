package com.github.dc.print.api.v_2_1_x.controller;

import com.github.dc.print.service.IExportExcelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 * 默认打印实现接口，接口请求路径可携带调用方系统代码systemCode（兼容低版本写法）
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/20 10:21
 */
@Controller
@Slf4j
@RequestMapping({"/print", "/{systemCode}/print"})
public class DcExportExcelV1Controller {

    @Autowired
    private IExportExcelService service;

    @GetMapping("/xlsx/down/{code}")
    public ResponseEntity<?> down(@PathVariable String code, @RequestParam("businessKey") Object businessKey, @RequestParam("title") String title) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            service.export(code, businessKey, byteArrayOutputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("application/vnd.ms-excel"));
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(URLEncoder.encode(title, "UTF-8") + ".xlsx").build());
            return ResponseEntity.ok().headers(headers).body(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("操作失败，请联系管理员。" + StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), StringUtils.LF));
        }
    }

    @GetMapping("/xlsx/batch-down/{code}")
    public ResponseEntity<?> batchDown(@PathVariable String code, @RequestParam("businessKey") List<Object> businessKey, @RequestParam("title") String title) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            service.batchExport(code, businessKey, byteArrayOutputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename(URLEncoder.encode(title, "UTF-8") + ".zip").build());
            return ResponseEntity.ok().headers(headers).body(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("操作失败，请联系管理员。" + StringUtils.join(ExceptionUtils.getRootCauseStackTrace(e), StringUtils.LF));
        }
    }
}
