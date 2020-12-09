package homework.beanassembly.componentscan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Levi
 * @date 2020/11/13 21:32
 */
@Service
public class MyService {

    @Autowired
    private MyRpository myRpository;
}
