package com.yunwei.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {

    /**
     * 小程序通过 wx.login() 获取的一次性凭证。
     */
    private String code;
}