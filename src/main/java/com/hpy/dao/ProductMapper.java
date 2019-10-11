package com.hpy.dao;

import com.hpy.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectByNameAndCategoryIds(@Param("categoryIdList") List<Integer> categoryIdList,
                                             @Param("keyword") String keyword);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("name") String productName,
                                           @Param("id") String productId);
}