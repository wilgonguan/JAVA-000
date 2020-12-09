package me.levi.service;

import me.levi.entity.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Levi
 * @date 2020/11/16 11:58
 */
@Service
public class MyService {

    @Autowired
    private School school;

    public void printInfo() {
        school.ding();
        school.getClass1().dong();
    }
}
