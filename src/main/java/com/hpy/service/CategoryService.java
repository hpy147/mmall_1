package com.hpy.service;

import com.hpy.common.ResponseResult;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
public interface CategoryService {
    ResponseResult addCategory(String categoryName, Integer parentId);

    ResponseResult getChildrenParallelCategory(Integer categoryId);

    ResponseResult setCategoryName(Integer categoryId, String categoryName);

    ResponseResult getCategoryAndDeepChildrenCategory(Integer categoryId);
}
