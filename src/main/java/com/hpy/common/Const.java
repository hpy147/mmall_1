package com.hpy.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Author: hpy
 * Date: 2019-10-10
 * Description: <描述>
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    public interface Role {
        int ROLE_CUSTOMER = 0;  // 普通用户
        int ROLE_ADMIN = 1;     // 管理员
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc", "price_desc");
    }

    public interface Cart {
        Integer CHECKED = 1;     // 已选中
        Integer UN_CHECKED = 0;  // 未选中

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public enum ProductStatusEnum {
        ON_SALE(1, "在售"),
        UN_ON_SALE(2, "下架"),
        DELETE(3, "删除");

        private Integer code;
        private String desc;

        private ProductStatusEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        public Integer getCode() {
            return code;
        }
        public String getDesc() {
            return desc;
        }
    }

}
