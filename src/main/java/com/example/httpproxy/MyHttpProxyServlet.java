package com.example.httpproxy;

import org.apache.http.client.utils.URIUtils;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

import javax.servlet.ServletException;
import java.net.URI;

/**
 * @author hanjun
 * @date 2019/5/13
 */
public class MyHttpProxyServlet extends ProxyServlet {
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
