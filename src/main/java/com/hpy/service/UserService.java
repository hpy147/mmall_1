package com.hpy.service;

import com.hpy.common.ResponseResult;
import com.hpy.pojo.User;

/**
 * Author: hpy
 * Date: 2019-10-10
 * Description: <描述>
 */
public interface UserService {
    ResponseResult login(String username, String password);

    ResponseResult checkValid(String str, String type);

    ResponseResult register(User user);

    ResponseResult forGetQuestion(String username);

    ResponseResult checkAnswer(String username, String question, String answer);

    ResponseResult forgetRestPassword(String username, String passwordNew, String token);

    ResponseResult resetPassword(User user, String passwordOld, String passwordNew);

    ResponseResult updateInformation(User currentUser, User updateUser);

    ResponseResult getInformation(Integer id);
}
