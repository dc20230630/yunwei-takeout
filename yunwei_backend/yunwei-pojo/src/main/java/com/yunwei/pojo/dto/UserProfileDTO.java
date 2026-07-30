package com.yunwei.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileDTO {

    @NotBlank(message = "请输入昵称")
    @Size(max = 32, message = "昵称不能超过32个字符")
    private String name;

    @NotBlank(message = "请上传头像")
    private String avatar;
}
