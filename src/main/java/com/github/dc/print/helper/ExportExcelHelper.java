package com.github.dc.print.helper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * <p>
 * 导出excel 辅助类
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/22 9:41
 */
@Slf4j
@AllArgsConstructor
public class ExportExcelHelper {

    /**
     * 模板目录
     */
    private String templateDir;

    /**
     * 由html模板导出pdf至OutputStream
     *
     * @param data        数据
     * @param xlsxTmpName 模板目录下的模板文件名
     * @param os          输出流
     */
    public void toOutputStream(Map<String, Object> data, String xlsxTmpName, OutputStream os) {
        ClassPathResource classPathResource = new ClassPathResource(templateDir + File.separator + xlsxTmpName);
        try (InputStream is = classPathResource.getInputStream();) {
            Map<String, Object> param = data;
            Context context = new Context(param);
            JxlsHelper.getInstance().processTemplate(is, os, context);
        } catch (Exception e) {
            throw new RuntimeException("打印异常-生成excel", e);
        }
    }
}
