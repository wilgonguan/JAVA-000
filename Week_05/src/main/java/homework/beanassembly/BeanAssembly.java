package homework.beanassembly;

import homework.beanassembly.bean.Parent;
import homework.beanassembly.componentscan.ComponentScanConfiguration;
import homework.beanassembly.configuration.BeanConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Levi
 * @date 2020/11/13 20:25
 */
public class BeanAssembly {

    public static void main(String[] args) {
        byXml();

        byAnnotation();

        byComponentScan();
    }

    private static void byXml() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("my-application-context.xml");
        Parent parent = ctx.getBean(Parent.class);
        System.out.println(parent);
    }

    private static void byAnnotation() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(BeanConfiguration.class);
        Parent parent = ctx.getBean(Parent.class);
        System.out.println(parent);
    }

    private static void byComponentScan() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(ComponentScanConfiguration.class);
        String[] definitionNames = ctx.getBeanDefinitionNames();
        for (String name : definitionNames) {
            System.out.println(name);
        }
    }
}
