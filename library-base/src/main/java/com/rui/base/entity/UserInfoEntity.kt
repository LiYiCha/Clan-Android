package com.rui.base.entity

import java.io.Serializable

/**
 * ******************************
 * *@Author
 * *date ：
 * *description:用户信息
 * *******************************
 */
class UserInfoEntity(
    var accessToken: String,
    var token_type: String,
    var refreshToken: String,
    var expires_in: Long,
    var scope: String
) : Serializable {
    override fun toString(): String {
        return "UserInfoEntity{" +
                "accessToken='" + accessToken + '\'' +
                ", token_type='" + token_type + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", scope='" + scope +
                '}'
    }
}