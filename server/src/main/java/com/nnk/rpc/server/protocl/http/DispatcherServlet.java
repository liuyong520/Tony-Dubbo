package com.nnk.rpc.server.protocl.http;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这个代码大家应该很熟悉吧，这个是sevlet的基本知识。
 * 任何请求被进来都会被这个sevlet处理
 */
public class DispatcherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new HttpHandler().handler(req,resp);
    }
}
