package me.levi.starter;

import me.levi.entity.Klass;
import me.levi.entity.School;
import me.levi.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import me.levi.service.MyService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Levi
 * @date 2020/11/16 10:03
 */
@Configuration
public class MyAutoConfiguration {

    @Autowired
    private Student student100;

    @Autowired
    private Student student123;

    @Bean
    public Student student100() {
        Student student = new Student();
        student.setId(100);
        student.setName("KK100");
        return student;
    }

    @Bean
    public Student student123() {
        Student student = new Student();
        student.setId(123);
        student.setName("KK123");
        return student;
    }

    @Bean
    public Klass class1() {
        Klass klass = new Klass();
        List<Student> list = new ArrayList<>();
        list.add(student100);
        list.add(student123);
        klass.setStudents(list);
        return klass;
    }

    @Bean
    public School school() {
        return new School();
    }

    @Bean
    public MyService myService() {
        return new MyService();
    }
}
