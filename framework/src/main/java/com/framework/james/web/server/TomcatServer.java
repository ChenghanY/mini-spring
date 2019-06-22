package com.framework.james.web.server;

import com.framework.james.web.handler.Handler;
import com.framework.james.web.handler.HandlerManager;
import com.framework.james.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 【step1】Spring 的内置服务器启动
 * 【step2】解析到项目中的Controller
 * @see HandlerManager
 * 【step3】将Tomcat服务器和Servlet建立联系
 * @see DispatcherServlet
 * 【step4】解析http请求，将参数映射到Controller的方法并反射调用
 * @see Handler#handle(ServletRequest, ServletResponse)
 */
public class TomcatServer {
    /** Tomcat服务器 */
    private Tomcat tomcat;

    /** 运行参数 留以后用 */
    private String[] args;

    public TomcatServer(String[] args) {
        this.args = args;
    }

    public void startServer() throws LifecycleException {
        // 配置Tomcat参数
        tomcat = new Tomcat();
        tomcat.setPort(6699);
        tomcat.start();
        /* Tomcat分为四个容器，将职责解耦
           Engine   是顶级容器，可以理解为Tomcat的总控中心
           Host     一个Host对应一个虚拟主机
           Context  是最接近Servlet的容器，可以设置Servlet的资源属性和管理组件
           Wrapper  是对Servlet的封装，负责Servlet的加载、初始化、执行、封装
         */
        Context context = new StandardContext();
        /* 设置Context的默认配置
           1. 设置Web Application的路径，"" 为根目录
           2. 设置Context的生命周期监听器，使用默认配置
         */
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());
        /* 将Servlet绑定在Tomcat上
           1. 依附context，注册Servlet，记住servletName:"dispatcherServlet"
           2. 将uri路径映射到指定Servlet上，"/"映射为根路径下所有uri
           3. context依附至上层容器Host
         */
        // Spring的Servlet
        DispatcherServlet servlet = new DispatcherServlet();
        Tomcat.addServlet(context, "dispatcherServlet", servlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/", "dispatcherServlet");
        tomcat.getHost().addChild(context);
        /* 设置服务器线程为常驻线程
           目的：不间断为请求提供服务
         */
        Thread awaitThread = new Thread("tomcat_await_thread") {
            @Override
            public void run() {
                TomcatServer.this.tomcat.getServer().await();
            }
        };
        /* 设置线程为非守护
           目的：不会因为用户线程全部停止而停止
         */
        awaitThread.setDaemon(false);
        awaitThread.start();
    }
}
