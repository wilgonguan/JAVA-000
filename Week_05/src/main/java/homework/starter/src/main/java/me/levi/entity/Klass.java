package me.levi.entity;

import lombok.Data;

import java.util.List;

/**
 * @author Levi
 * @date 2020/11/16 10:29
 */
@Data
public class Klass {

    List<Student> students;

    public void dong() {
        System.out.println(this.getStudents());
    }
}
