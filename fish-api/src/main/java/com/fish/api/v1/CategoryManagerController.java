package com.fish.api.v1;

import com.common.api.response.DataResponse;
import com.fish.domain.mysql.Category;
import com.fish.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "（商家）分类管理Api", tags = "（商家）分类管理Api")
@RestController
@RequestMapping("/v1/category/manager")
public class CategoryManagerController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation("查询分类列表")
    @GetMapping
    public DataResponse<List<Category>> getLiftEventList(@RequestParam(required = false) String code) {
        return DataResponse.of(categoryService.getAllCategories(code));
    }

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
