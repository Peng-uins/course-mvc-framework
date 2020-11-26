package org.nf.mvc.view;

import javax.servlet.ServletException;
import java.io.IOException;
import java.rmi.server.ServerCloneException;

/**
 * 文本视图，原样输出
 */
public class PlainView extends View {

    private String content;

    public PlainView(String content) {
        this.content = content;
    }

    @Override
    public void response() throws ServerCloneException, IOException, ServletException {
        //text/plain原样输出不会解译
        response.setContentType("text/plain;charset=utf-8");
        response.getWriter().println(content);
    }
}
