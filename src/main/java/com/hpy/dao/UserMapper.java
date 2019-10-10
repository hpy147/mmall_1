package com.hpy.dao;

import com.hpy.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByUsername(String username);

    int checkByUsername(String str);

    int checkByEmail(String str);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username") String username,
                    @Param("question") String question,
                    @Param("answer") String answer);

    int updatePasswordByUsername(@Param("password") String password,
                                 @Param("username") String username);

    int checkPasswordByUserId(@Param("id") Integer id,
                              @Param("password") String password);

    int updatePasswordByUserId(@Param("id") Integer id,
                               @Param("password") String password);

    int checkEmailByUserId(@Param("id") Integer id,
                           @Param("email") String email);
}