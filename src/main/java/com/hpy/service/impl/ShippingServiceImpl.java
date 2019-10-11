package com.hpy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.hpy.common.ResponseCode;
import com.hpy.common.ResponseResult;
import com.hpy.dao.ShippingMapper;
import com.hpy.pojo.Shipping;
import com.hpy.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
@Service(value = "shippingService")
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    @Transactional
    public ResponseResult add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map<String, Integer> resultMap = Maps.newHashMap();
            resultMap.put("shippingId", shipping.getId());
            return ResponseResult.createBySuccess("新建收货地址成功", resultMap);
        }
        return ResponseResult.createByError("新建收货地址失败");
    }

    @Override
    @Transactional
    public ResponseResult del(Integer userId, Integer shippingId) {
        if (shippingId == null) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int rowCount = shippingMapper.deleteByUserIdAndShippingId(userId, shippingId);
        if (rowCount > 0) {
            return ResponseResult.createBySuccess("收货地址删除成功");
        }
        return ResponseResult.createByError("收货地址删除失败");
    }

    @Override
    @Transactional
    public ResponseResult update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int updateCount = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (updateCount > 0) {
            return ResponseResult.createBySuccess("收货地址更新成功");
        }
        return ResponseResult.createByError("收货地址更新失败");
    }

    @Override
    public ResponseResult select(Integer userId, Integer shippingId) {
        if (shippingId == null) {
            return ResponseResult.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId, shippingId);
        if (shipping != null) {
            return ResponseResult.createBySuccess(shipping);
        }
        return ResponseResult.createByError("没有查询到该收货地址");
    }

    @Override
    public ResponseResult list(Integer userId, Integer pageNum, Integer pageSize) {
        List<Shipping> shippingList = shippingMapper.selectList(userId);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<Shipping> pageInfo = new PageInfo<>(shippingList);
        return ResponseResult.createBySuccess(pageInfo);
    }
}
