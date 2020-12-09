package me.levi.service;

import me.levi.entity.User;

import java.util.List;

/**
 * @author Levi
 * @date 2020/12/1 21:44
 */
public interface UserService {

    List<User> listUser();

    void update(User user);

    User findById(Integer id);
}
