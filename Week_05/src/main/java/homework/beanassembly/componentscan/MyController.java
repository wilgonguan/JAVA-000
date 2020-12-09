package homework.beanassembly.componentscan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author Levi
 * @date 2020/11/13 21:25
 */
@Controller
public class MyController {

    @Autowired
    private MyService myService;

}
