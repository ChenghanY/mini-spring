package com.framework.james.starter;

import com.framework.james.beans.BeanFactory;
import com.framework.james.core.ClassScanner;
import com.framework.james.web.handler.HandlerManager;
import com.framework.james.web.server.TomcatServer;
import org.apache.catalina.LifecycleException;

import java.io.IOException;
import java.util.List;

/**
 * 框架启动入口类
 *
 * @see TomcatServer 内置Tomcat服务器（有MVC接收与处理请求的流程注释）
 * @see ClassScanner 基于包的类扫描器
 * @see HandlerManager 间接管理Controller
 */
public class MiniApplication {
    /**
     * 框架启动方法
     * @param cls 应用层启动类
     * @param args 运行参数
     */
    public static void run(Class<?> cls, String[] args) {
        System.out.println("Hello mini-spring!");
        /**
         *  404错误则需要在Servlet里面注册URL
         */
        TomcatServer tomcatServer = new TomcatServer(args);
        try {
            // 启动Tomcat服务器
            tomcatServer.startServer();
            // 扫描启动类所在的包下所有类
            List<Class<?>> classList = ClassScanner.scanClasses(cls.getPackage().getName());
            // 根据classList进行Bean的初始化
            BeanFactory.initBean(classList);
            // HandlerManager根据classList解析所有Controller
            HandlerManager.resolveMappingHandler(classList);
            // 调试信息：获取启动类包下的所有类名
            classList.forEach(it -> System.out.println(it.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
