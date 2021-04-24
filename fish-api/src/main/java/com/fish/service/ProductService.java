package com.fish.service;

import com.common.api.response.ApiError;
import com.common.exceptions.RestApiException;
import com.common.file.FileService;
import com.common.security.Auth;
import com.fish.api.dto.ProductDTO;
import com.fish.api.qo.ProductQO;
import com.fish.domain.mysql.Product;
import com.fish.repository.ProductRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private FileService fileService;
    @Autowired
    private ProductRepository productRepository;

    public String uploadImage(MultipartFile certFile, String code) throws IOException {
        String path = getFilePath(certFile,code);
        try (InputStream in = certFile.getInputStream()) {
            fileService.write(path, in);
        }
        return path;
    }

    public void deleteImage(String path){
        fileService.deleteFile(path);
    }

    public String getFilePath(MultipartFile certFile,String code){
        String fileName = certFile.getOriginalFilename();
        return  "/images/fish/product/" + code + "/" + fileName;
    }

    public Page<ProductDTO> getProduct(ProductQO qo, Pageable pageable){
        if(null == qo.getEntId()){
            qo.setEntId(1L);
        }
        return productRepository.getProduct(qo,pageable);
    }

    public ProductDTO getProductById(Long id){
        ProductDTO productDto = new ProductDTO();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ApiError.RESOURCE_NOT_FOUND,id));
        BeanUtils.copyProperties(product,productDto);
        return productDto;
    }

    public void editProduct(ProductDTO productDto){
        if(null == productDto.getId()){
            Product product = new Product();
            BeanUtils.copyProperties(productDto,product);
            product.setIsTurn(0);
            product.setStatus(1);
            product.setEntId(Auth.getEntId());
            productRepository.save(product);
        }else{
            Product product =  productRepository.findById(productDto.getId())
                    .orElseThrow(() -> new RestApiException(ApiError.RESOURCE_NOT_FOUND,productDto.getId()));
            BeanUtils.copyProperties(productDto,product);
            productRepository.save(product);
        }
    }

    public void deleteProduct(Long id){
        Product product =  productRepository.findById(id)
                .orElseThrow(() -> new RestApiException(ApiError.RESOURCE_NOT_FOUND,id));
        List<String> images = new ArrayList<>();
        if(StringUtils.isNotBlank(product.getImage1())){
            images.add(product.getImage1());
        }
        if(StringUtils.isNotBlank(product.getImage2())){
            images.add(product.getImage2());
        }
        if(StringUtils.isNotBlank(product.getImage3())){
            images.add(product.getImage3());
        }
        fileService.deleteFilesQuietly(images);
        productRepository.delete(product);
    }

}
