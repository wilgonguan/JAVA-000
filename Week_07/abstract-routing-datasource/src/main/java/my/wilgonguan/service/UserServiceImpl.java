package me.levi.service;

import me.levi.dynamicdatasource.DataSourceSelector;
import me.levi.dynamicdatasource.DynamicDataSourceEnum;
import me.levi.entity.User;
import me.levi.repository.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Levi
 * @date 2020/12/1 21:44
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @DataSourceSelector(value = DynamicDataSourceEnum.REPLICA)
    @Override
    public List<User> listUser() {
        return userMapper.selectAll();
    }

    @DataSourceSelector
    @Override
    public void update(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }

    @DataSourceSelector(value = DynamicDataSourceEnum.REPLICA)
    @Override
    public User findById(Integer id) {
        User user = new User();
        user.setId(id);
        return userMapper.selectByPrimaryKey(user);
    }
}
