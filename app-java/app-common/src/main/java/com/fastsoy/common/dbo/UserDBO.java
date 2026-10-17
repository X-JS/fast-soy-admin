package com.fastsoy.common.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class UserDBO {

    private Long id;

    private String userName;

    private String nickName;

}
