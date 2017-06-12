package com.bounter.web.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非法字符过滤器（防SQL注入，防XSS跨站脚本攻击）
 * 
 * @author simon
 *
 */
public class XssFilter implements Filter {

	// 特殊脚本字符集
	String[] scripts = { "script", "mid", "master", "truncate", "insert", "select", "delete", "update", "declare",
			"iframe", "'", "onreadystatechange", "alert", "atestu", "xss", ";", "'", "\"", "<", ">", "(", ")", ",",
			"\\", "svg", "confirm", "prompt", "onload", "onmouseover", "onfocus", "onerror" };

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// 获取所有请求参数
		Enumeration<?> params = req.getParameterNames();
		String paramName;
		String paramValue;
		while (params.hasMoreElements()) {
			paramName = (String) params.nextElement();
			// 获取请求参数值
			paramValue = req.getParameter(paramName);

			// 对请求参数值进行特殊脚本字符检查
			if (existScriptString(paramValue)) {
				// 特殊字符检查失败时返回错误信息
				String warning = "输入项中不能包含非法字符。";

				resp.setContentType("text/html; charset=UTF-8");
				PrintWriter out = resp.getWriter();

				out.println(
						"{\"httpCode\":\"-9998\",\"msg\":\"" + warning + "\", \"fieldName\": \"" + paramName + "\"}");
				out.flush();
				out.close();
				return;
			}
		}

		filterChain.doFilter(req, resp);
	}

	private boolean existScriptString(String paramValue) {
		// 先进行基本的特殊字符替换
		StringBuilder sb = new StringBuilder(paramValue.length() + 16);
		for (int i = 0; i < paramValue.length(); i++) {
			char c = paramValue.charAt(i);
			switch (c) {
			case '>':
				sb.append('＞');// 全角大于号
				break;
			case '<':
				sb.append('＜');// 全角小于号
				break;
			case '\'':
				sb.append('‘');// 全角单引号
				break;
			case '\"':
				sb.append('“');// 全角双引号
				break;
			case '&':
				sb.append('＆');// 全角
				break;
			case '\\':
				sb.append('＼');// 全角斜线
				break;
			case '#':
				sb.append('＃');// 全角井号
				break;
			case '(':
				sb.append('（');//
				break;
			case ')':
				sb.append('）');//
				break;
			default:
				sb.append(c);
				break;
			}
		}
		paramValue = sb.toString().toLowerCase();

		//进行特殊脚本字符串匹配
		for (int i = 0; i < scripts.length; i++) {
			if (paramValue.indexOf(scripts[i]) > -1) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
