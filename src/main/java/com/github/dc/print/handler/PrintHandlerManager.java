package com.github.dc.print.handler;

import com.github.dc.print.annotation.PrintHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * <p>
 *     打印处理器调度工具
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/10/20 10:51
 */
@Slf4j
public class PrintHandlerManager {

    private Set<IPrint> handlerList = new HashSet<>();

    public IPrint get(String code) {
        for (IPrint printHandler : this.handlerList) {
            PrintHandler printHandlerAnnotation = printHandler.getClass().getAnnotation(PrintHandler.class);
            if (StringUtils.isBlank(printHandlerAnnotation.value())) {
                continue;
            }
            if (printHandlerAnnotation.value().equals(code)) {
                return printHandler;
            }
        }
        // 无对应打印处理器
        log.warn("无对应打印处理器");

        return null;
    }

    public void setHandlerList(List<IPrint> handlerList) {
        this.handlerList.addAll(handlerList);
    }

    public void setParse(IPrint... parse) {
        this.handlerList.addAll(Arrays.asList(parse));
    }

    public void setParse(IPrint parse) {
        this.handlerList.add(parse);
    }

    public List<IPrint> getHandlerList() {
        return Collections.unmodifiableList(new ArrayList<>(this.handlerList));
    }
}
