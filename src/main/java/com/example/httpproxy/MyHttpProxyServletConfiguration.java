package com.example.httpproxy;

import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyHttpProxyServletConfiguration {

  @Value("${myhttpproxy.servlet_url}")
  private String servlet_url;
  @Value("${myhttpproxy.target_url}")
  private String target_url;
  @Value("${myhttpproxy.logging_enabled}")
  private String logging_enabled;

  @Bean
  public ServletRegistrationBean servletRegistrationBean(){
    ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new ProxyServlet(), servlet_url);
    servletRegistrationBean.addInitParameter("targetUri", target_url);
    servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, logging_enabled);
    return servletRegistrationBean;
  }

  @Bean("servletRegistrationBean1")
  public ServletRegistrationBean servletRegistrationBean1(){
    ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new MyHttpProxyServlet(), "/api/*");
    //这个targetUrl没有什么作用，中间换掉了
    servletRegistrationBean.addInitParameter("targetUri", "https://www.baidu.com");
    servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, logging_enabled);
    return servletRegistrationBean;
  }
}