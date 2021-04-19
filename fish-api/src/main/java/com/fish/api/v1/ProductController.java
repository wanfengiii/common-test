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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "产品相关Api", tags = "产品相关Api")
@RestController
@RequestMapping("/v1/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation("查询产品列表")
    @GetMapping
    public DataResponse<Page<ProductDto>> getLiftEventList(ProductQO qo, Pageable pageable) {
        return DataResponse.of(productService.getProduct(qo, pageable));
    }

}
