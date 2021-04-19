package com.fish.api.v1;

import com.common.api.response.DataResponse;
import com.fish.domain.mysql.Category;
import com.fish.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "订单Api", tags = "订单Api")
@RestController
@RequestMapping("/v1/order")
public class OrderController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation("查询订单")
    @GetMapping
    public DataResponse<List<Category>> getOrderList(@RequestParam(required = false) String code) {
        return DataResponse.of(categoryService.getAllCategories(code));
    }

    @ApiOperation("编辑订单")
    @PostMapping
    public DataResponse editOrder(@RequestBody Category category){
        categoryService.saveCategory(category);
        return DataResponse.success() ;
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public DataResponse deleteOrder(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return DataResponse.success() ;
    }
}
