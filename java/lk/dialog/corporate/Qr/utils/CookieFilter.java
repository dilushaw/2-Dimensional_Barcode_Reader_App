/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.utils;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is to disable cookies for the browser. This will be using as a filter through web.xml. To disable cookies
 * relevant code of the web.xml should be uncommented, it it is already commented. Code is uncommented means cookies are
 * disable.
 *
 * @author Dewmini
 */
public class CookieFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpres = (HttpServletResponse) response;

        httpres.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        httpres.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        httpres.setDateHeader("Expires", 0); // Proxies.
        chain.doFilter(request, response);
    }

    public void destroy() {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }
}
