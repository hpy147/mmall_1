package com.hpy.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hpy.common.ResponseCode;
import com.hpy.common.ResponseResult;
import com.hpy.dao.CategoryMapper;
import com.hpy.pojo.Category;
import com.hpy.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
@Service(value = "categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public ResponseResult addCategory(String categoryName, Integer parentId) {
        if (StringUtils.isBlank(categoryName) || parentId == null) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ResponseResult.createBySuccess("添加分类成功");
        }
        return ResponseResult.createByError("添加分类失败");
    }

    @Override
    public ResponseResult getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectByParentId(categoryId);
        return ResponseResult.createBySuccess(categoryList);
    }

    @Override
    @Transactional
    public ResponseResult setCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 判断要更新的品类名称是否已存在
        int resultCount = categoryMapper.selectByCategoryName(categoryName);
        if (resultCount > 0) {
            return ResponseResult.createByError("该品类名称已存在");
        }
        // 更新品类
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int updateCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (updateCount > 0) {
            return ResponseResult.createBySuccess("品类更新成功");
        }
        return ResponseResult.createByError("品类更新失败");
    }

    @Override
    public ResponseResult getCategoryAndDeepChildrenCategory(Integer categoryId) {
        // 算出所有子节点，存入Set集合中
        Set<Integer> categoryIdSet = Sets.newHashSet();
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
           categoryIdSet.add(categoryId);
        }
        this.findChildCategory(categoryIdSet, categoryId);

        // Set集合转换成List集合
        List<Integer> categoryIdList = Lists.newArrayList();
        categoryIdList.addAll(categoryIdSet);
        // 排序
        Collections.sort(categoryIdList);

        return ResponseResult.createBySuccess(categoryIdList);
    }

    /**
     * 递规查询出所有子节点的ID
     */
    private void findChildCategory(Set<Integer> categoryIdSet, Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectByParentId(categoryId);
        for (Category category : categoryList) {
            categoryIdSet.add(category.getId());
            this.findChildCategory(categoryIdSet, category.getId());
        }
    }

}
