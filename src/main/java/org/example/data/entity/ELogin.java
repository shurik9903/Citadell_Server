package org.example.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "\"users\"")
public class ELogin implements Serializable{
    @Id
    @Column(name = "\"id\"")
    private Integer user_ID;

    @Column(name = "\"login\"")
    private String user_login;

    @Column(name = "\"password\"")
    private String user_password;

    @Column(name = "\"permission\"")
    private String user_permission;

    public ELogin(){}

    public ELogin(String Login, String Password){

        this.user_login = Login;
        this.user_password = Password;
    }

    public ELogin(int ID, String UserLogin){

        this.user_ID = ID;
        this.user_login = UserLogin;
    }

    public String getUser_permission() {
        return user_permission;
    }

    public void setUser_permission(String user_permission) {
        this.user_permission = user_permission;
    }


    public Integer getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(Integer user_ID) {
        this.user_ID = user_ID;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_login() {
        return user_login;
    }

    public void setUser_login(String user_login) {
        this.user_login = user_login;
    }
}
