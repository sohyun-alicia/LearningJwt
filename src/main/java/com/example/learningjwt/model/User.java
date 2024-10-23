package com.example.learningjwt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@Table(name="tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String username;
    public String password;
    public String roles;    // USER, ADMIN

    public List<String> getRoleList() {
        if (!this.roles.isEmpty()) {
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }
}
