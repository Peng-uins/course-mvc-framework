package org.nf.mvc.core;

import org.nf.mvc.param.AbstractParamResolves;
import org.nf.mvc.param.ParamsResolver;
import org.nf.mvc.param.impl.BasicParamResolver;
import org.nf.mvc.param.impl.BeanParamResolver;
import org.nf.mvc.param.impl.ServlerApiParamResolver;
import org.nf.mvc.util.ScanUtils;
import org.nf.mvc.view.View;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 核心servlet,用于接收所有的请求
 * 然后根据请求的url去匹配对应的Controller类的方法
 */
public class DispatcherServlet extends HttpServlet {

    /**
     * 这个map用于缓存请求的处理方法(Method)，
     * key保存的是请求的url(也就是Method注解上的url地址)
     */
    private Map<String,Method> map = new HashMap<>();

    /**
     * Lsit集合，用于缓存所有的参数解析器
     */
    private static List<ParamsResolver> resolverslist = new ArrayList<>();

    /**
     * 初始化，解析Method方法并缓存
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //请求反射映射
        iniRequestMapping();
        //请求参数解析器
        iniParamResolver();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //当请求到达service方法时，从map集合中找到匹配的Method来处理请求
        //获取客户端地址
        String url = request.getRequestURI();
        //判断map集合中是否存在这个key
        //如果包含了，则取出对应的Method方法来处理请求
        if(map.containsKey(url)){
            Method method = map.get(url);
            try {
                //找到当前method所在的类的Class类对象，然后创建实例
                //然后new一个的对象
                Object obj = method.getDeclaringClass().newInstance();
                //参数的映射转换,返回一s个Object数组，标识方法中所有的参数值
                Object[] params = resolverParams(request,response,method);

                //回调这个method,params就是封装好的请求数据，映射到方法中
                Object returnView =  method.invoke(obj,params);
                responseView(returnView,response,request);

              // Parentview view =  (Parentview)method.invoke(obj,params);
              // view.response(view.object,response,request);
                request.getParameter("username");
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            //否则其他的所有请求都交由会Tomcat处理
            //不然就会忽略这些请求会导致浏览器空白
            //因此先获取Tomcat的默认Servlet的转发器,然后执行转发
            // 然后跳转过去给Tomcat的方法来处理
            request.getServletContext().getNamedDispatcher("default").forward(request,response);
        }
    }

    private void iniRequestMapping(){
        //扫描所有包下的类，并返回所有类的完整类名
        Set<String> classname = ScanUtils.scanPackage();
        //循环遍历
        for(String className : classname){
            try{
                //执行类加载得到class对象
                Class<?>clazz = Class.forName(className);
                //获取Class对象中的所有公共的Method
                Method[] methods = clazz.getMethods();
                //循环遍历方法数组，找出带有WebRequest注解的Method，并收集起来
                for(Method method : methods){
                    //如果方法有标识注解(就是一个请求处理方法)，
                    // 那么就将这个方法缓存起来，可以重复使用
                    if(method.isAnnotationPresent(WebRequest.class)){
                        //获取注解
                        WebRequest anno = method.getAnnotation(WebRequest.class);
                        //获取注解的value属性值
                        String url = anno.value();
                        //将url作为key，method作为value，缓存到集合中
                        map.put(url,method);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化参数解析器，这些解析器只要初始化一次并缓存起来
     * （保存到list中）
     */
    private void iniParamResolver(){
        resolverslist.add(new BasicParamResolver());
        resolverslist.add(new BeanParamResolver());
        resolverslist.add(new ServlerApiParamResolver());
    }

    /**
     * 解析转换方法参数
     */
    private Object[] resolverParams(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Method method){
        //获取请求方法中所有的参数
        Parameter[] params = method.getParameters();
        //定义Object数组，用于存放参数的值，长度为参数列表的长度
        Object[] values = new Object[params.length];
        for (int i = 0 ; i <params.length;i++){
            Parameter param = params[i];
            //遍历解析器集合，匹配转换，如果转换成功则返回具体的值
            //否则返回null，嚷嚷下一个解析器继续处理
            for(ParamsResolver resolver : resolverslist){
                ((AbstractParamResolves)resolver).setRequest(request);
                ((AbstractParamResolves)resolver).setResponse(response);
                //进行解析转换，并返回转换后的value
                Object value =  resolver.process(param);
                //判断value是否为空，不为空则保存到Object数组
                if(value != null){
                    //将转换后的值保存到Object数组中
                    values[i] = value;
                    //转换成功无需在走下一个解析器，直接跳出当前循环
                    //执行下一个循环
                    break;
                }

            }
        }
        return values;
    }

    /**
     * 响应视图
     */
    private void responseView(Object returnView, HttpServletResponse response, HttpServletRequest request)throws  Exception{
        //returnView是返回视图对象,不为空则转换成View的对象实例
        if (returnView != null){
            //判断如果returnView是View类的实例，则可以强转
            if(returnView instanceof View){
                //响应视图
                View view = (View)returnView;
                //设置request和resoonse对象
                view.setRequest(request);
                view.setResponse(response);
                //执行视图响应方法
                view.response();
            }else {
                //否则返回的不是View实例则使用默认视图响应
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().println(returnView);
            }
        }
    }
}
