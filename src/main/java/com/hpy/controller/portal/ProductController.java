package com.hpy.controller.portal;

import com.hpy.common.ResponseResult;
import com.hpy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping("/detail")
    public ResponseResult detail(Integer productId) {
        return productService.getProductDetail(productId);
    }

    @RequestMapping("/list")
    public ResponseResult list(Integer categoryId, String keyword, String orderBy,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        return productService.getProductByKeywordCategory(categoryId, keyword, orderBy, pageNum, pageSize);
    }
}
