package com.intro.user.entity;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("users")
public class User {
    private int id;

    private String email;

    private String username;

    private String salt;

    private String password;

    @TableField("is_email_confirmed")
    private boolean isEmailConfirmed;

    @TableField("last_login_ip")
    private String lastLoginIP;

    @TableField("last_login_time")
    private String lastLoginTime;
}