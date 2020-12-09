package homework.proxy;

import java.lang.reflect.Proxy;

/**
 * @author Levi
 * @date 2020/11/14 11:10
 */
public class MyProxy {
    public static void main(String[] args) {
        DoSomething doSomething = new DoSomethingImpl();
        doSomething = (DoSomething) proxyObject(doSomething);
        doSomething.doSomething();

        System.out.println(doSomething.getClass());
    }

    public static Object proxyObject(Object object) {
        MyProxy myProxy = new MyProxy();
        return Proxy.newProxyInstance(myProxy.getClass().getClassLoader(), object.getClass().getInterfaces(), (proxy, method, args) -> {
            myProxy.before();

            Object result = method.invoke(object, args);

            myProxy.after();
            return result;
        });
    }

    public void before() {
        System.out.println("before do something");
    }

    public void after() {
        System.out.println("after do something");
    }
}
