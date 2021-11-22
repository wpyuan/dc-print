package com.github.dc.print.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/22 11:09
 */
public interface IExportExcelService<T> {
    /**
     * 客制化打印
     *
     * @param code        客制化打印处理器标识
     * @param businessKey 业务主键
     * @param os          输出流
     */
    void export(String code, T businessKey, OutputStream os);

    /**
     * 打印
     *
     * @param data        打印数据
     * @param xlsxTmpName 模板目录下的模板文件名
     * @param os          输出流
     */
    void export(Map<String, Object> data, String xlsxTmpName, OutputStream os);

    /**
     * 批量打印
     *
     * @param code        客制化打印处理器标识
     * @param businessKey 业务主键
     * @param os          输出流
     */
    void batchExport(String code, List<T> businessKey, OutputStream os);
}
