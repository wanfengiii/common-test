package com.fish.api.v1;

import com.common.api.response.DataResponse;
import com.fish.domain.mysql.Category;
import com.fish.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "分类相关Api", tags = "分类相关Api")
@RestController
@RequestMapping("/v1/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation("查询分类列表")
    @GetMapping
    public DataResponse<List<Category>> getLiftEventList(@RequestParam(required = false) String code,
                                                         @RequestParam Long entId) {
        return DataResponse.of(categoryService.getAllCategories(code,entId));
    }
}
