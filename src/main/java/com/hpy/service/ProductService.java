package com.hpy.service;

import com.hpy.common.ResponseResult;
import com.hpy.pojo.Product;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
public interface ProductService {
    ResponseResult getProductDetail(Integer productId);

    ResponseResult getProductByKeywordCategory(Integer categoryId, String keyword, String orderBy, Integer pageNum, Integer pageSize);

    ResponseResult saveOrUpdate(Product product);

    ResponseResult setProductStatus(Integer productId, Integer status);

    ResponseResult managerProductDetail(Integer productId);

    ResponseResult getProductList(Integer pageNum, Integer pageSize);

    ResponseResult searchProduct(String productName, String productId, Integer pageNum, Integer pageSize);
}
