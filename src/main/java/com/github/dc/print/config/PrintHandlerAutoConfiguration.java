package com.github.dc.print.config;

import com.github.dc.print.annotation.PrintHandler;
import com.github.dc.print.handler.IPrint;
import com.github.dc.print.handler.PrintHandlerManager;
import com.github.dc.print.helper.ApplicationContextHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 打印处理器配置
 *
 * @author PeiYuan
 */
@Configuration
@DependsOn("dcPrintApplicationContextHelper")
public class PrintHandlerAutoConfiguration {

    @Bean
    public PrintHandlerManager printHandlerManager() {
        PrintHandlerManager printHandlerManager = new PrintHandlerManager();
        printHandlerManager.setHandlerList(scanHandler());
        return printHandlerManager;
    }

    private <T> List<IPrint> scanHandler() {
        List<IPrint> printHandlerList = new ArrayList<>();
        Map<String, Object> beans = ApplicationContextHelper.getApplicationContext().getBeansWithAnnotation(PrintHandler.class);
        beans.forEach((beanName, bean) -> {
            if (bean instanceof IPrint) {
                printHandlerList.add((IPrint) bean);
            }
        });
        return printHandlerList;
    }
}
