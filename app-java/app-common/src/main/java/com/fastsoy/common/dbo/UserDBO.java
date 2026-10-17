package com.fastsoy.common.dbo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class UserDBO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userName;

    private String password;

    private String nickName;

    private String userGender;

    private String userEmail;

    private String userPhone;

    private Integer statusType;

    /** 最后登录时间 */
    private java.time.OffsetDateTime lastLogin;

    /** 令牌版本号，密码修改后递增使已签发 token 失效 */
    private Integer tokenVersion;

    /** 首次登录是否需修改密码 */
    private Boolean mustChangePassword;

    private java.time.OffsetDateTime createdAt;

    private java.time.OffsetDateTime updatedAt;
}
