package com.hpy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.hpy.common.Const;
import com.hpy.common.ResponseCode;
import com.hpy.common.ResponseResult;
import com.hpy.dao.CategoryMapper;
import com.hpy.dao.ProductMapper;
import com.hpy.pojo.Category;
import com.hpy.pojo.Product;
import com.hpy.service.CategoryService;
import com.hpy.service.ProductService;
import com.hpy.util.DateTimeUtils;
import com.hpy.util.PropertyUtils;
import com.hpy.vo.ProductDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.Response;
import java.util.List;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
@Service(value = "productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryService categoryService;

    @Override
    public ResponseResult getProductDetail(Integer productId) {
        if (productId == null) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ResponseResult.createByError("商品不存在");
        }
        if (product.getStatus() == Const.ProductStatusEnum.UN_ON_SALE.getCode()) {
            return ResponseResult.createByError("商品已下架");
        }
        if (product.getStatus() == Const.ProductStatusEnum.DELETE.getCode()) {
            return ResponseResult.createByError("商品已删除");
        }
        // 商品正常状态  Product -> ProductDetailVO
        ProductDetailVO productDetailVO = this.assembleProductDetailVO(product);
        return ResponseResult.createBySuccess(productDetailVO);
    }

    @Override
    public ResponseResult getProductByKeywordCategory(Integer categoryId, String keyword, String orderBy, Integer pageNum, Integer pageSize) {
        // 要查询的两个条件不能全部为空
        if (categoryId == null && StringUtils.isBlank(keyword)) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 分页
        PageHelper.startPage(pageNum, pageSize);

        // categoryIdList初始值设置为null，为mybatis查询做准备
        List<Integer> categoryIdList = null;

        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                // 没有查找到商品分类，也没有传入关键字，就返回一个空的PageInfo给前端，不返回error
                List<Product> productList = Lists.newArrayList();
                PageInfo<Product> pageInfo = new PageInfo<>(productList);
                return ResponseResult.createBySuccess(pageInfo);
            }
            if (category != null) {
                // 算出该节点和其下的所有子节点的ID
                categoryIdList = (List<Integer>) categoryService.getCategoryAndDeepChildrenCategory(categoryId).getData();
            }
        }

        // 设置关键字
        if (StringUtils.isBlank(keyword)) {
            // keyword如果没有值就设置为null，为mybatis查询做准备
            keyword = null;
        } else {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        // 设置排序
        // orderBy = price_asc 或者 price_desc
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }

        List<Product> productList = productMapper.selectByNameAndCategoryIds(categoryIdList, keyword);

        // Product -> ProductListVO
        List<ProductDetailVO> productDetailVOList = Lists.newArrayList();
        for (Product product : productList) {
            ProductDetailVO productDetailVO = this.assembleProductDetailVO(product);
            productDetailVOList.add(productDetailVO);
        }
        PageInfo<ProductDetailVO> pageInfo = new PageInfo<>(productDetailVOList);
        return ResponseResult.createBySuccess(pageInfo);
    }


    // backend ========================


    @Override
    @Transactional
    public ResponseResult saveOrUpdate(Product product) {
        if (product == null) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 将第一张子图设置为主图
        String subImages = product.getSubImages();
        if (StringUtils.isNotBlank(subImages)) {
            String[] subtImagesArray = subImages.split(",");
            product.setMainImage(subtImagesArray[0]);
        }

        // 判断是新增还是修改
        if (product.getId() == null) {
            // 新增
            int rowCount = productMapper.insert(product);
            if (rowCount > 0) {
                return ResponseResult.createBySuccess("商品添加成功");
            }
            return ResponseResult.createByError("商品添加失败");
        } else {
            // 修改
            int updateCount = productMapper.updateByPrimaryKeySelective(product);
            if (updateCount > 0) {
                return ResponseResult.createBySuccess("商品修改成功");
            }
            return ResponseResult.createByError("商品修改失败");
        }
    }

    @Override
    @Transactional
    public ResponseResult setProductStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int updateCount = productMapper.updateByPrimaryKeySelective(product);
        if (updateCount > 0) {
            return ResponseResult.createBySuccess("商品状态修改成功");
        }
        return ResponseResult.createByError("商品状态修改失败");
    }

    @Override
    public ResponseResult managerProductDetail(Integer productId) {
        if (productId == null) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ResponseResult.createByError("商品不存在");
        }
        ProductDetailVO productDetailVO = this.assembleProductDetailVO(product);
        return ResponseResult.createBySuccess(productDetailVO);
    }

    @Override
    public ResponseResult getProductList(Integer pageNum, Integer pageSize) {
        List<Product> productList = productMapper.selectList();

        // product -> productDetailVO
        List<ProductDetailVO> productDetailVOList = Lists.newArrayList();
        for (Product product : productList) {
            ProductDetailVO productDetailVO = this.assembleProductDetailVO(product);
            productDetailVOList.add(productDetailVO);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<ProductDetailVO> pageInfo = new PageInfo<>(productDetailVOList);
        return ResponseResult.createBySuccess(pageInfo);
    }

    @Override
    public ResponseResult searchProduct(String productName, String productId, Integer pageNum, Integer pageSize) {
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }

        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);

        // product -> productDetailVO
        List<ProductDetailVO> productDetailVOList = Lists.newArrayList();
        for (Product product : productList) {
            ProductDetailVO productDetailVO = this.assembleProductDetailVO(product);
            productDetailVOList.add(productDetailVO);
        }
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<ProductDetailVO> pageInfo = new PageInfo<>(productDetailVOList);
        return ResponseResult.createBySuccess(pageInfo);
    }

    // Product -> ProductDetailVO
    private ProductDetailVO assembleProductDetailVO(Product product) {
        ProductDetailVO productDetailVO = new ProductDetailVO();
        // 值拷贝
        BeanUtils.copyProperties(product, productDetailVO);
        // 设置商品的父分类的ID
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category != null) {
            productDetailVO.setParentCategoryId(category.getParentId());
        }
        // 设置时间
        productDetailVO.setCreateTime(DateTimeUtils.dateToStr(product.getCreateTime()));
        productDetailVO.setUpdateTime(DateTimeUtils.dateToStr(product.getUpdateTime()));
        // 设置图片地址前缀
        productDetailVO.setImageHost(PropertyUtils.getProperty("ftp.server.http.prefix"));
        return productDetailVO;
    }

}
