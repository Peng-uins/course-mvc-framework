package org.nf.mvc.param.impl;

import org.nf.mvc.param.AbstractParamResolves;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Parameter;

/**
 * servletApi参数
 */
public class ServlerApiParamResolver extends AbstractParamResolves {

    @Override
    public Object process(Parameter parameter) {
        if(parameter.getType().equals(HttpServletResponse.class)){
            return response;
        }else if(parameter.getType().equals(HttpServletRequest.class)){
            return request;
        }else if(parameter.getType().equals(HttpSession.class)){
            return request.getSession();
        }else if(parameter.getType().equals(ServletContext.class)){
            return request.getServletContext();
        }else{
            return null;
        }
    }
}
