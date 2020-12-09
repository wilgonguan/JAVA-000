package homework.beanassembly.configuration;

import homework.beanassembly.bean.Child;
import homework.beanassembly.bean.Parent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Levi
 * @date 2020/11/13 21:10
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public Parent getParent() {
        Parent parent = new Parent();
        Child child = new Child();
        child.setName("byConfigurationChild");
        parent.setChild(child);
        parent.setName("byConfigurationParent");
        return parent;
    }
}
