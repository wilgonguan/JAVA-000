package me.levi.service;

import me.levi.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Levi
 * @date 2020/12/1 21:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserService userService;

    @Test
    void listUser() {
        List<User> users = userService.listUser();
        for (User user : users) {
            System.out.println(user.getId());
            System.out.println(user.getName());
        }
    }

    @Test
    void update() {
        Integer id = 1;
        User user = new User();
        user.setId(id);
        user.setName("user修改1");
        userService.update(user);
        System.out.println(userService.findById(id).getName());
    }
}
