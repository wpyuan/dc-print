package com.github.dc.print.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 二维码配置
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/27 11:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class QrcodeConfig {
    /**
     * 启用二维码
     */
    private Boolean enableQrcode = false;
    /**
     * 二维码内容
     */
    private String qrcodeContent;
    /**
     * 二维码所在页码
     */
    private List<Integer> qrcodeLocationPageNumbers;
    /**
     * 是否所有页都添加二维码
     */
    private Boolean isQrcodeLocationAllPage = false;
    /**
     * 二维码所在页面X轴绝对坐标
     */
    private Integer qrcodeAbsoluteX;
    /**
     * 二维码所在页面Y轴绝对坐标
     */
    private Integer qrcodeAbsoluteY;
    /**
     * 二维码尺寸，默认60X60
     */
    private Integer qrcodeSize = 60;
}
