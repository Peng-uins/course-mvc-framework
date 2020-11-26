package org.nf.mvc.param.impl;

import org.nf.mvc.param.AbstractParamResolves;
import org.nf.mvc.util.ConvertUtils;

import java.lang.reflect.Parameter;

/**
 * 继承数据类型的转换
 */
public class BasicParamResolver extends AbstractParamResolves {

    /**
     *
     * @param parameter 请求处理方法的参数，用于获取参数类型和参数名
     * @return
     */
    @Override
    public Object process(Parameter parameter) {
        //参数类型
        Class<?> parameterType = parameter.getType();
        //参数名称
        String paramName = parameter.getName();
        //获取请求的字符串数据
        String requestParam = request.getParameter(paramName);
        try{
            //进行类型转换
            Object value = ConvertUtils.convert(requestParam,parameterType);
            //返回转换后的值
            return value;
        }catch (Exception e){
            //如果是不支持的类型，则会引发异常
            //返回空，让下一个转换器来进行处理
            return  null;
        }
    }
}
