package com.fish.api.v1;

import com.common.api.response.DataResponse;
import com.fish.api.dto.ProductDto;
import com.fish.api.qo.ProductQO;
import com.fish.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Api(value = "产品相关Api", tags = "产品相关Api")
@RestController
@RequestMapping("/v1/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation("上传图片")
    @PostMapping("/image")
    public DataResponse<String> uploadImage(MultipartFile certFile,@RequestParam String code) throws Exception{
        return DataResponse.of(productService.uploadImage(certFile,code)) ;
    }

    @ApiOperation("删除图片")
    @DeleteMapping("/image")
    public DataResponse deleteImage(@RequestParam String path){
        productService.deleteImage(path);
        return DataResponse.success();
    }

    @ApiOperation("查询产品列表")
    @GetMapping
    public DataResponse<Page<ProductDto>> getLiftEventList(ProductQO qo, Pageable pageable) {
        return DataResponse.of(productService.getProduct(qo, pageable));
    }

    @ApiOperation("编辑新增产品")
    @PostMapping
    public DataResponse editProduct(@RequestBody @Valid ProductDto productDto){
        productService.editProduct(productDto);
        return DataResponse.success() ;
    }

    @ApiOperation("删除产品")
    @DeleteMapping("/{id}")
    public DataResponse deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return DataResponse.success() ;
    }
}
