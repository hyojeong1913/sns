package com.example.sns.fixture;

import com.example.sns.model.entity.UserEntity;

// 테스트용 userEntity
public class UserEntityFixture {

    public static UserEntity get(String userName, String password) {

        UserEntity result = new UserEntity();
        result.setId(1);
        result.setUserName(userName);
        result.setPassword(password);

        return result;
    }
}
