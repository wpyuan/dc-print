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
     * 批量压缩时定义文件名(不带格式)，不设置则返回默认文件名
     *
     * @param businessKey 当前压缩文件的业务主键
     * @param data 打印的业务数据
     * @param defaultFileName 默认文件名
     * @return 定义压缩包里的文件名
     */
    default String customFileNameWhenBatchCompress(T businessKey, Map<String, Object> data, String defaultFileName) {
        return defaultFileName;
    }

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
