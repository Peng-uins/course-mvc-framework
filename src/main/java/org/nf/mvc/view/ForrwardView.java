package org.nf.mvc.view;


import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 转发视图
 */
public class ForrwardView extends View {

    private String url;

    public ForrwardView(String url) {
        this.url = url;
    }

    @Override
    public void response() throws  IOException, ServletException {
        request.getRequestDispatcher(url).forward(request,response);
    }
}
