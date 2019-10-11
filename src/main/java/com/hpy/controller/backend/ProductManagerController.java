package com.hpy.controller.backend;

import com.google.common.collect.Maps;
import com.hpy.common.Const;
import com.hpy.common.ResponseResult;
import com.hpy.pojo.Product;
import com.hpy.pojo.User;
import com.hpy.service.FileService;
import com.hpy.service.ProductService;
import com.hpy.util.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
@RestController
@RequestMapping("/manager/product")
public class ProductManagerController {

    @Autowired
    private ProductService productService;
    @Autowired
    private FileService fileService;

    @PostMapping("/save")
    public ResponseResult save(HttpSession session, Product product) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return productService.saveOrUpdate(product);
        }
        return response;
    }

    @PostMapping("/set_sale_status")
    public ResponseResult setSaleStatus(HttpSession session, Integer productId, Integer status) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return productService.setProductStatus(productId, status);
        }
        return response;
    }

    @PostMapping("/detail")
    public ResponseResult getDetail(HttpSession session, Integer productId) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return productService.managerProductDetail(productId);
        }
        return response;
    }

    @PostMapping("/list")
    public ResponseResult list(HttpSession session,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return productService.getProductList(pageNum, pageSize);
        }
        return response;
    }

    @PostMapping("/search")
    public ResponseResult search(HttpSession session, String productName, String productId,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {
            return productService.searchProduct(productName, productId, pageNum, pageSize);
        }
        return response;
    }

    @PostMapping("/upload")
    public ResponseResult upload(HttpSession session,
                                 @RequestParam(value = "upload_file") MultipartFile file) {
        ResponseResult response = this.checkAdmin(session);
        if (response.isSuccess()) {

            String path = session.getServletContext().getRealPath("upload");
            String fileName = fileService.upload(file, path);

            if (StringUtils.isBlank(fileName)) {
                return ResponseResult.createByError("上传失败");
            }

            String url = PropertyUtils.getProperty("ftp.server.http.prefix") + fileName;

            Map<String, String> fileMap = Maps.newHashMap();
            fileMap.put("uri", fileName);
            fileMap.put("url", url);
            return ResponseResult.createBySuccess(fileMap);
        }
        return response;
    }

    @PostMapping("/richtext_img_upload")
    public Map richTextImgUpload(HttpSession session, HttpServletResponse response,
                                 @RequestParam(value = "upload_file") MultipartFile file) {

        Map<String, Object> resultMap = Maps.newHashMap();

        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }

        ResponseResult responseResult = this.checkAdmin(session);
        if (responseResult.isSuccess()) {

            String path = session.getServletContext().getRealPath("upload");
            String fileName = fileService.upload(file, path);

            if (StringUtils.isBlank(fileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "文件上传失败");
                return resultMap;
            }

            String url = PropertyUtils.getProperty("ftp.server.http.prefix") + fileName;
            resultMap.put("success", true);
            resultMap.put("msg", "文件上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;

        }
        String msg = responseResult.getMsg();
        resultMap.put("success", false);
        resultMap.put("msg", msg);
        return resultMap;
    }


    // 判断登陆和权限
    private ResponseResult checkAdmin(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ResponseResult.createByError("用户未登陆");
        }
        if (user.getRole() == Const.Role.ROLE_ADMIN) {
            return ResponseResult.createBySuccess();
        } else {
            return ResponseResult.createByError("权限不足");
        }
    }

}
