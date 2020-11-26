package org.nf.mvc.view;

import javax.servlet.ServletException;
import java.io.IOException;
import java.rmi.server.ServerCloneException;


/**
 * 重定向视图
 */
public class RedirectView extends View {

    private String url;

    public RedirectView(String url) {
        this.url = url;
    }

    @Override
    public void response() throws ServerCloneException, IOException, ServletException {
        response.sendRedirect(url);
    }
}
