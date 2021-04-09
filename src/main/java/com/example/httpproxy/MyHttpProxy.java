package com.xhnewi.api.config;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xhnewi.api.mapper.ProxyPropertyMapper;
import com.xhnewi.api.model.ProxyProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIUtils;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * @Description: 自定义代理
 * @Author Dell
 * @Date 2021/4/9
 **/
@Slf4j
public class MyHttpProxy extends ProxyServlet {

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        //根据request的url改变targetUri
        analyzeRequestUrl(servletRequest);
        super.service(servletRequest, servletResponse);
    }

    private void analyzeRequestUrl(HttpServletRequest servletRequest) throws ServletException {
        String pathInfo = servletRequest.getPathInfo();
        log.error("pathInfo -> " + pathInfo);
        if (pathInfo != null) {
            //切分pathInfo的多级目录
            String[] pathArrays = pathInfo.split("/");
            if (pathArrays != null && pathArrays.length > 0) {
                for (int i = 0; i < pathArrays.length; i++) {
                    log.error("第" + (i + 1) + "级 -> " + pathArrays[i]);
                }
            }
            String refProxyPath = pathArrays[1];
            //根据子路径获取对应要代理的地址
            ProxyPropertyMapper proxyPropertyMapper = SpringUtil.getBean("proxyPropertyMapper");
            ProxyProperty proxyProperty = proxyPropertyMapper.selectOne(new QueryWrapper<ProxyProperty>().eq("proxy_path", refProxyPath));
            if(proxyProperty==null){
                throw new ServletException("代理路径[/"+refProxyPath+"]不正确，请找管理员确认路径");
            }
            changeTargetBySubPath(proxyProperty.getForwardPath());
        }
    }

    @Override
    protected String rewriteUrlFromRequest(HttpServletRequest servletRequest) {

        StringBuilder uri = new StringBuilder(500);
        uri.append(getTargetUri(servletRequest));
        // Handle the path given to the servlet
        //注释掉，不要后面的子路径
       /* if (servletRequest.getPathInfo() != null) {//ex: /my/path.html
            uri.append(encodeUriQuery(servletRequest.getPathInfo()));
        }*/
        String queryString = servletRequest.getQueryString();
        String fragment = null;
        //split off fragment from queryString, updating queryString if found
        if (queryString != null) {
            int fragIdx = queryString.indexOf('#');
            if (fragIdx >= 0) {
                fragment = queryString.substring(fragIdx + 1);
                queryString = queryString.substring(0,fragIdx);
            }
        }

        queryString = rewriteQueryStringFromRequest(servletRequest, queryString);
        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            uri.append(encodeUriQuery(queryString));
        }

        if (doSendUrlFragment && fragment != null) {
            uri.append('#');
            uri.append(encodeUriQuery(fragment));
        }
        return uri.toString();
    }

    /**
     * 根据url的路径改变targetUri
     *
     * @throws ServletException
     */
    protected void changeTargetBySubPath(String newTargetUri) throws ServletException {
        this.targetUri = newTargetUri;
        if (this.targetUri == null) {
            throw new ServletException("targetUri is required.");
        } else {
            try {
                this.targetUriObj = new URI(this.targetUri);
            } catch (Exception var2) {
                throw new ServletException("Trying to process targetUri init parameter: " + var2, var2);
            }
            this.targetHost = URIUtils.extractHost(this.targetUriObj);
        }
    }

}
