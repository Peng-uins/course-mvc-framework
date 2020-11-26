package org.nf.mvc.param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 抽象的参数解析器
 * 封装请求和响应对象，让子类继承
 * 继承接口的抽象方法，让不同的子类去实现
 */
public abstract class AbstractParamResolves implements ParamsResolver {
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
