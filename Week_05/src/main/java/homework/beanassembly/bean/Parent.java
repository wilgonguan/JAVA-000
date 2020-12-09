package homework.beanassembly.bean;

import lombok.Data;

/**
 * @author Levi
 * @date 2020/11/13 20:11
 */
@Data
public class Parent {
    private String name;
    private Child child;
}
