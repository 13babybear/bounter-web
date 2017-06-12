package com.bounter.web.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by simon on 2017/5/29.
 */
@WebServlet(value = "/simpleAsyncServlet", asyncSupported = true)
public class SimpleAsyncServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取异步上下文并返回
        AsyncContext asyncContext = req.startAsync();

        //开启新的线程异步地执行任务
        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                try {
                    //模拟耗时任务执行
                    Thread.sleep(2000);

                    //任务执行完成后响应
                    asyncContext.getResponse().getWriter().write("Hello from simple asynchronize servlet!");
                    //通知容器异步处理完成
                    asyncContext.complete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
