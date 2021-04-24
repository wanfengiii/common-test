package com.fish.service;

import com.common.api.response.ApiError;
import com.common.exceptions.RestApiException;
import com.common.security.Auth;
import com.common.util.BeanUtil;
import com.fish.domain.mysql.Category;
import com.fish.domain.mysql.Product;
import com.fish.repository.CategoryRepository;
import com.fish.repository.ProductRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;


@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    public List<Category> getAllCategories(String code,Long entId) {
        if (StringUtils.isNotBlank(code)) {
            return categoryRepository.findChildByCode(code);
        } else {
            return categoryRepository.findParent(entId);
        }
    }

    public List<Category> getAllCategories(String code) {
        return getAllCategories(code, Auth.getEntId());
    }

    @Transactional
    public boolean saveCategory(Category saveVo) {


        Category c = new Category();
        if (saveVo.getId() == null) {
            Optional<Category> cate = categoryRepository.findByCode(saveVo.getCode());
            if (cate.isPresent()) {
                throw new RestApiException(ApiError.SAVE_FAILED, "产品类型" + saveVo.getCode() + "已存在！");
            }
            Long entId = Auth.getEntId();
            c.setEntId(entId);
        } else {
            c = categoryRepository.findById(saveVo.getId())
                    .orElseThrow(() -> new RestApiException(ApiError.SAVE_FAILED, saveVo.getId() + "不存在！"));
        }

        if (StringUtils.isNotBlank(saveVo.getParent())) {
            categoryRepository.findByCode(saveVo.getParent())
                    .orElseThrow(() -> new RestApiException(ApiError.SAVE_FAILED, saveVo.getParent() + "不存在！"));
        }

        BeanUtil.copyPropertiesQuietly(saveVo, c);
        categoryRepository.save(c);
        return true;
    }

    @Transactional
    public boolean deleteCategory(Long id) {
        Category opc = categoryRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ApiError.DELETE_FAILED, id + "不存在！"));

        List<String> num = categoryRepository.findCategoryByParent(opc.getCode());
        if (num.size() > 0) {
            throw new RestApiException(ApiError.DELETE_FAILED, "产品类型" + opc.getCode() + "下存在子类！");
        }

        List<Product> lt = productRepository.findByCategory(opc.getCode());

        if(!CollectionUtils.isEmpty(lt)){
            throw new RestApiException(ApiError.DELETE_FAILED, "产品类型" + opc.getCode() + "下存在产品！");
        }
        categoryRepository.deleteById(id);
        return true;
    }
}
