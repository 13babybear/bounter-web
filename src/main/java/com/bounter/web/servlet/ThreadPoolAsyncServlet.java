package com.bounter.web.servlet;


import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bounter.web.listener.AppAsyncListener;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by simon on 2017/5/29.
 */
@WebServlet(value = "/threadPoolAsyncServlet", asyncSupported = true)
public class ThreadPoolAsyncServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext asyncContext = req.startAsync();
        //添加异步监听器（可选）
        asyncContext.addListener(new AppAsyncListener());
        //设置超时时间（可选）
        asyncContext.setTimeout(6000);

        //从应用上下文中获取线程池
        ExecutorService executorService = (ExecutorService)req.getServletContext().getAttribute("executorService");
        //提交异步任务
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //模拟耗时任务执行
                    Thread.sleep(2000);

                    //任务执行完成后响应
                    asyncContext.getResponse().getWriter().write("Hello from thread pool asynchronize servlet!");
                    //通知容器异步处理完成
                    asyncContext.complete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
