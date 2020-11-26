package org.nf.mvc.param.impl;

import org.nf.mvc.param.AbstractParamResolves;
import org.nf.mvc.util.ConvertUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

/**
 * 实现对象
 */
public class BeanParamResolver extends AbstractParamResolves {

    @Override
    public Object process(Parameter parameter) {
       try{
           //获取参数类型
           Class<?> paramType = parameter.getType();
           //创建当前参数的对象实例
           Object obj =  paramType.newInstance();
           //获取参数的所有私有字段
           Field[] fields = paramType.getDeclaredFields();
           for(Field field : fields){
               //打开访问开关
               field.setAccessible(true);
               //获取字段的类型
               Class<?> fieldType = field.getType();
               //字段名
               String fieldName = field.getName();
               //根据字段名匹配请求参数的name,获取请求参数的值
               String requestParam = request.getParameter(fieldName);
               if(requestParam != null && !"".equals(requestParam)){
                   //进行转换
                   Object value = ConvertUtils.convert(requestParam,fieldType);
                   //将value赋值给字段
                   field.set(obj,value);
               }
           }
           return obj;
       }catch (Exception e){
           return  null;
       }
    }
}
