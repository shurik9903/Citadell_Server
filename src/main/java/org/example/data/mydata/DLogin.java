package org.example.data.mydata;

import org.example.data.entity.ELogin;

public class DLogin {

    private Integer user_ID;
    private String user_login;
    private String user_password;
    private String user_permission;
    private String Msg;

    public DLogin(){}

    public DLogin(ELogin login){
        this.user_ID = login.getUser_ID();
        this.user_login = login.getUser_login();
        this.user_password = login.getUser_password();
    }

    public DLogin(String Login, String Password){
        this.user_login = Login;
        this.user_password = Password;
    }

    public DLogin(String Msg, int ID, String UserLogin){
        this.Msg = Msg;
        this.user_ID = ID;
        this.user_login = UserLogin;
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

    public String getUser_permission() {
        return user_permission;
    }

    public void setUser_permission(String user_permission) {
        this.user_permission = user_permission;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }
}
