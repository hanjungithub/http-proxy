package com.example.httpproxy;

import org.apache.http.client.utils.URIUtils;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * @author hanjun
 * @date 2019/5/13
 */
public class MyHttpProxyServlet extends ProxyServlet {


    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        String uri = servletRequest.getRequestURI();
        String[] split = uri.split("/");
        System.out.println(split[1]);
        System.out.println(split[2]);
        if (split[2].equals("google")) {
            changeTarget("https://www.google.com/abc");
        }
        if (split[2].equals("sina")) {
            changeTarget("https://www.sina.com.cn");
        }
        super.service(servletRequest, servletResponse);
    }


    @Override
    protected String rewriteUrlFromRequest(HttpServletRequest servletRequest) {
        StringBuilder uri = new StringBuilder(500);
        uri.append(getTargetUri(servletRequest));
        // Handle the path given to the servlet
        /*if (servletRequest.getPathInfo() != null) {//ex: /my/path.html
            uri.append(encodeUriQuery(servletRequest.getPathInfo()));
        }*/
        // Handle the query string & fragment
        String queryString = servletRequest.getQueryString();//ex:(following '?'): name=value&foo=bar#fragment
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



    protected void changeTarget(String newTarget) throws ServletException {
        this.targetUri = newTarget;
        if (this.targetUri == null) {
            throw new ServletException("targetUri is required.");
        } else {
            try {
                System.out.println(this.targetUri);
                this.targetUriObj = new URI(this.targetUri);
            } catch (Exception var2) {
                throw new ServletException("Trying to process targetUri init parameter: " + var2, var2);
            }

            this.targetHost = URIUtils.extractHost(this.targetUriObj);
        }
    }


    @Override
    protected void initTarget() throws ServletException {
        this.targetUri = this.getConfigParam("targetUri");

        this.targetUri = "http://localhost:9093/uploadFile";
        if (this.targetUri == null) {
            throw new ServletException("targetUri is required.");
        } else {
            try {
                System.out.println(this.targetUri);
                this.targetUriObj = new URI(this.targetUri);
            } catch (Exception var2) {
                throw new ServletException("Trying to process targetUri init parameter: " + var2, var2);
            }

            this.targetHost = URIUtils.extractHost(this.targetUriObj);
        }
    }
}
