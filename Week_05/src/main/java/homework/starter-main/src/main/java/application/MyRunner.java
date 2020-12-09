package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import me.levi.service.MyService;

/**
 * @author Levi
 * @date 2020/11/16 12:15
 */
@Component
public class MyRunner implements CommandLineRunner {
    @Autowired
    private MyService myService;

    @Override
    public void run(String... args) {
        myService.printInfo();
    }
}
