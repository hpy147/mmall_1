package com.hpy.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: hpy
 * Date: 2019-10-10
 * Description: <描述>
 */
@Getter@Setter
public class PasswordVO {
    private String password;
    private String salt;

    public PasswordVO() {}

    public PasswordVO(String password, String salt) {
        this.password = password;
        this.salt = salt;
    }

}
