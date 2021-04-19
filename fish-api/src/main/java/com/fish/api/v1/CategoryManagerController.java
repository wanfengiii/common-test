package com.fish.api.v1;

import com.common.api.response.DataResponse;
import com.fish.domain.mysql.Category;
import com.fish.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "分类相关Api", tags = "分类相关Api")
@RestController
@RequestMapping("/v1/category/manager")
public class CategoryManagerController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation("编辑新增分类")
    @PostMapping
    public DataResponse editCategory(@RequestBody Category category){
        categoryService.saveCategory(category);
        return DataResponse.success() ;
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public DataResponse deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return DataResponse.success() ;
    }
}
