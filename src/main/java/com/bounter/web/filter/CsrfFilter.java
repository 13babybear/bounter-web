package com.bounter.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 跨域请求伪造过滤器
 * 通过控制请求头的Referer参数实现
 * @author simon
 *
 */
public class CsrfFilter implements Filter {

	//Referer URL白名单集
	private final String[] whiteUrls = {"localhost","index.html"};
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		// 获取请求Referer地址
		String referUrl = req.getHeader("Referer").toLowerCase();
		if (referUrl.startsWith("http://")) {
			referUrl = referUrl.substring(7);
		} else if (referUrl.startsWith("https://")) {
			referUrl = referUrl.substring(8);
		}
		
		//进行Referer URL白名单匹配
		for(String url : whiteUrls) {
			//匹配成功
			if (referUrl.indexOf(url.toLowerCase()) > -1) {
				chain.doFilter(request, response);
				return;
			}
		}

		//转发到系统首页
		req.getRequestDispatcher("/").forward(req, res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
