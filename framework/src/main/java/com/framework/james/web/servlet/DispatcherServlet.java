package com.framework.james.web.servlet;

import com.framework.james.web.handler.HandlerManager;
import com.framework.james.web.handler.Handler;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Spring 用DispatcherServlet来转发同一个Servlet下的请求给Controller中对应的方法
 * @see HandlerManager
 * @see Handler
 *
 */
public class DispatcherServlet implements Servlet {
    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        // version 1 : res.getWriter().println("test");
        // version 2 : 一个Servlet接收的请求分发给不同Controller处理
        for (Handler handler : HandlerManager.handlerList) {
            try {
                if (handler.handle(req, res)) {
                    return;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
