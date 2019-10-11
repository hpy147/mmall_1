package com.hpy.controller.backend;

import com.hpy.common.Const;
import com.hpy.common.ResponseResult;
import com.hpy.pojo.User;
import com.hpy.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
@RestController
@RequestMapping("/manager/category")
public class CategoryManagerController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/add_category")
    public ResponseResult addCategory(HttpSession session, String categoryName, Integer parentId) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return categoryService.addCategory(categoryName, parentId);
        }
        return response;
    }

    @RequestMapping("/get_category")
    public ResponseResult getCategory(HttpSession session,
                                      @RequestParam(defaultValue = "0") Integer categoryId) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return categoryService.getChildrenParallelCategory(categoryId);
        }
        return response;
    }

    @RequestMapping("/set_category_name")
    public ResponseResult setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return categoryService.setCategoryName(categoryId, categoryName);
        }
        return response;
    }

    @RequestMapping("/get_deep_category")
    public ResponseResult getDeepCategory(HttpSession session,
                                          @RequestParam(defaultValue = "0") Integer categoryId) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return categoryService.getCategoryAndDeepChildrenCategory(categoryId);
        }
        return response;
    }


    // 判断登陆和权限
    private ResponseResult checkAdmin(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResponseResult.createByError("用户未登陆");
        }
        if (user.getRole() == Const.Role.ROLE_ADMIN) {
            return ResponseResult.createBySuccess();
        } else {
            return ResponseResult.createByError("权限不足");
        }
    }

}
