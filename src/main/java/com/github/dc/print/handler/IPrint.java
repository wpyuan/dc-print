package com.github.dc.print.handler;

import java.io.OutputStream;
import java.util.Map;

/**
 * <p>
 * 打印接口类
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/20 10:57
 */
public interface IPrint<T> {

    /**
     * 返回模板目录下模板文件名
     *
     * @return 模板文件名
     */
    String template();

    /**
     * 返回打印数据
     *
     * @param businessKey 业务主键
     * @return 打印数据
     */
    Map<String, Object> data(T businessKey);

    /**
     * 打印时的异常处理
     *
     * @param code 处理器标识
     * @param businessKey 业务主键
     * @param os 输出流
     * @param e 异常
     */
    void handleException(String code, T businessKey, OutputStream os, Exception e);
}
