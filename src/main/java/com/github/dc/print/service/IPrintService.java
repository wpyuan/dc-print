package com.github.dc.print.service;

import java.io.OutputStream;
import java.util.Map;

/**
 * <p>
 *     打印接口服务类
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/20 8:24
 */
public interface IPrintService<T> {

    /**
     * 客制化打印
     * @param code 客制化打印处理器标识
     * @param businessKey 业务主键
     * @param os 输出流
     */
    void print(String code, T businessKey, OutputStream os);

    /**
     * 打印
     * @param data 打印数据
     * @param htmlTmpName 模板目录下的模板文件名
     * @param os 输出流
     */
    void print(Map<String, Object> data, String htmlTmpName, OutputStream os);
}
