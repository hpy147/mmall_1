package com.hpy.service.impl;

import com.google.common.collect.Lists;
import com.hpy.common.Const;
import com.hpy.common.ResponseCode;
import com.hpy.common.ResponseResult;
import com.hpy.dao.CartMapper;
import com.hpy.dao.ProductMapper;
import com.hpy.pojo.Cart;
import com.hpy.pojo.Product;
import com.hpy.service.CartService;
import com.hpy.util.BigdecimalUtils;
import com.hpy.util.PropertyUtils;
import com.hpy.vo.CartProductVO;
import com.hpy.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
@Service(value = "cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;

    @Override
    @Transactional
    public ResponseResult add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 查询要添加的商品是否状态正常
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ResponseResult.createByError("商品不存在");
        }
        if (product.getStatus() == Const.ProductStatusEnum.UN_ON_SALE.getCode()) {
            return ResponseResult.createByError("商品已删除");
        }
        if (product.getStatus() == Const.ProductStatusEnum.DELETE.getCode()) {
            return ResponseResult.createByError("商品已删除");
        }

        // 根据用户ID和商品ID查询购物车，看该商品是否已经被添加过
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 如果count为负数，不允许添加该商品
            if (count < 1) {
                return ResponseResult.createByError("添加商品失败，无效的参数");
            }
            // 商品未加入过购物车
            // 添加商品到购物车
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(cartItem);
        } else {
            // 如果count为负数，防止购物车商品数量更新后，出现负数的结果
            if (cart.getQuantity() + count < 0) {
                return ResponseResult.createByError("添加商品失败，无效的参数");
            }
            // 商品已经在购物车中
            // 更新购物车中该商品的数量
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    public ResponseResult list(Integer userId) {
        CartVO cartVO = this.assembleCartVO(userId);
        return ResponseResult.createBySuccess(cartVO);
    }

    @Override
    @Transactional
    public ResponseResult deleteProduct(Integer userId, Integer... productIds) {
        if (productIds.length == 0) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> productIdsList = Arrays.asList(productIds);
        cartMapper.deleteByUserIdAndProductIds(userId, productIdsList);
        return this.list(userId);
    }

    @Override
    @Transactional
    public ResponseResult update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null || count < 0) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        // 先查询出该条商品记录
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    @Transactional
    public ResponseResult selectOrUnSelect(Integer userId, Integer checked, Integer productId) {
        cartMapper.updateCheckedOrUnCheckedProduct(userId, checked, productId);
        return this.list(userId);
    }

    @Override
    public ResponseResult getCartProductCount(Integer userId) {
        int resultCount = cartMapper.selectCartProductCountByUserId(userId);
        return ResponseResult.createBySuccess(resultCount);
    }


    // 购物车核心方法
    private CartVO assembleCartVO(Integer userId) {

        CartVO cartVO = new CartVO();

        // 根据用户ID获取购物车信息
        List<Cart> cartList = cartMapper.selectByUserId(userId);

        // 购物车中被选中的商品总价
        BigDecimal cartTotalPrice = new BigDecimal("0");

        List<CartProductVO> cartProductVOList = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(cartList)) {
            // 将购物车的所有商品信息封装成CartProductVo
            for (Cart cartItem : cartList) {
                // 如果商品数量为0，不在前台购物车中显示该商品详情
                if (cartItem.getQuantity() > 0) {
                    CartProductVO cartProductVO = new CartProductVO();
                    cartProductVO.setProductId(cartItem.getProductId());

                    // 根据商品ID获取商品信息
                    Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());

                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductChecked(cartItem.getChecked());

                    // 设置商品购买的数量
                    if (cartItem.getQuantity() > product.getStock()) {
                        // 购买的数量大于库存数，将商品购买数量设置为库存数
                        cartItem.setQuantity(product.getStock());
                        cartMapper.updateByPrimaryKeySelective(cartItem);
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    } else {
                        // 购买的数量小于等于库存数，正常
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }
                    cartProductVO.setQuantity(cartItem.getQuantity());

                    // 计算该商品总价: 商品单价 * 购买数量
                    cartProductVO.setProductTotalPrice(BigdecimalUtils.multiply(product.getPrice().doubleValue(), cartProductVO.getQuantity().doubleValue()));
                    cartProductVOList.add(cartProductVO);

                    // 如果该商品被选中，就将该商品总价 -> 增加到整个购物车总价中
                    if (cartProductVO.getProductChecked() == Const.Cart.CHECKED) {
                        cartTotalPrice = BigdecimalUtils.add(cartTotalPrice.doubleValue(), cartProductVO.getProductTotalPrice().doubleValue());
                    }
                }
            }
        }

        cartVO.setCartProductVoList(cartProductVOList);
        cartVO.setCartTotalPrice(cartTotalPrice);
        cartVO.setAllChecked(this.getAllCheckedStatus(userId));
        cartVO.setImageHost(PropertyUtils.getProperty("ftp.server.http.prefix"));

        return cartVO;
    }

    // 判断购物车商品是否被全选
    private Boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckStatusByUserId(userId) == 0;
    }


}
