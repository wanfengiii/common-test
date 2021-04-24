package com.fish.repository;

import com.fish.domain.mysql.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    @Query(value = "SELECT c FROM Category c where c.entId = ?1 and c.parent is null or c.parent = '' order by  sort")
    List<Category> findParent(Long entId);

    @Query(value = "SELECT c FROM Category c where c.parent = ?1 order by  sort")
    List<Category> findChildByCode(String code);

    @Query(value = "SELECT distinct c.name FROM Category c where c.parent = ?1")
    List<String> findCategoryByParent(String parent);

    Optional<Category> findById(Long id);
    
    Long countByCode(String code);

    @Query(value = "SELECT name FROM Category where code = ?1")
    String findNameByCode(String code);

    Optional<Category> findByCode(String code);
}