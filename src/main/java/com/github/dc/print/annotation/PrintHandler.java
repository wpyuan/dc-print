package com.github.dc.print.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <p>
 *     打印数据处理器
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/20 10:48
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public @interface PrintHandler {

    String value();
}
