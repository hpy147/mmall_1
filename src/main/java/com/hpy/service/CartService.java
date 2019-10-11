package com.hpy.service;

import com.hpy.common.ResponseResult;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
public interface CartService {
    ResponseResult add(Integer userId, Integer productId, Integer count);

    ResponseResult list(Integer userId);

    ResponseResult deleteProduct(Integer userId, Integer... productIds);

    ResponseResult update(Integer userId, Integer productId, Integer count);

    ResponseResult selectOrUnSelect(Integer userId, Integer checked, Integer productId);

    ResponseResult getCartProductCount(Integer userId);
}
