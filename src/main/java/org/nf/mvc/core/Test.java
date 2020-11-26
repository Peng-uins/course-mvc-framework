package org.nf.mvc.core;

import java.lang.reflect.Method;

public class Test {

    public void say(){

    }

    public static void main(String[] args) throws Exception {
        Method method = Test.class.getMethod("say");

        Class<?> clazz = method.getDeclaringClass();
        System.out.println(clazz);
    }
}
