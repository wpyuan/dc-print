package com.github.dc.print.controller;

import com.github.dc.print.service.IPrintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 *     默认打印实现
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/20 10:21
 */
@Controller
@Slf4j
@RequestMapping("/print")
public class DefaultPrintController {

    @Autowired
    private IPrintService service;

    @GetMapping("/inline/{code}")
    public void inline(@PathVariable String code, @RequestParam("businessKey") Object businessKey, @RequestParam("title") String title, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\""
                + new String(title.getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".pdf" + "\"");
        service.print(code, businessKey, response.getOutputStream());
    }

    @GetMapping("/down/{code}")
    public void down(@PathVariable String code, @RequestParam("businessKey") Object businessKey, @RequestParam("title") String title, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + new String(title.getBytes(StandardCharsets.UTF_8), "ISO8859-1") + ".pdf" + "\"");
        service.print(code, businessKey, response.getOutputStream());
    }
}
