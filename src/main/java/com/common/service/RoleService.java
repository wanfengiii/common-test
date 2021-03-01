package com.common.service;

import com.common.api.dto.RoleDTO;
import com.common.api.response.ApiError;
import com.common.domain.Role;
import com.common.domain.RolePermission;
import com.common.exceptions.RestApiException;
import com.common.repository.RolePermissionRepository;
import com.common.repository.RoleRepository;
import com.common.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    @Autowired
    private PropertiesUtils propertiesUtil;

    public Page<Role> findRoles(String q, Pageable pageable) {
        return (StringUtils.isBlank(q) ? roleRepository.findAll(pageable) : roleRepository.findRoles(q, pageable));
    }
    
    public List<RoleDTO> findRoles(String q) {
    	List<Role> roles = (StringUtils.isBlank(q) ? roleRepository.findAll() : roleRepository.findRoles(q));
    	List<RoleDTO> dtos = roles.stream().map(e -> {
    		RoleDTO dto = new RoleDTO();
    		dto.setName(e.getName());
    		dto.setCode(e.getCode());
    		dto.setId(e.getId());
            return dto;
        }).collect(Collectors.toList());
    	
    	
        return dtos;
    }
    
    public List<RoleDTO> findRolesByType(String usertype) {
    	
    	List<Role> roles = (StringUtils.isBlank(usertype) ? roleRepository.findAll() : roleRepository.findRolesByCodeLike(propertiesUtil.getRoletypeValue(usertype) + '%'));
    	List<RoleDTO> dtos = roles.stream().map(e -> {
    		RoleDTO dto = new RoleDTO();
    		dto.setName(e.getName());
    		dto.setCode(e.getCode());
    		dto.setId(e.getId());
            return dto;
        }).collect(Collectors.toList());
    	
    	
        return dtos;
    }
    
    
    /**
     * 新增角色
     * @param p
     * @return
     */
    public void createRole(Role p) {
    	
        boolean codeExist = roleRepository.existsByCode(p.getCode());
        if (codeExist) {
            throw new RestApiException(ApiError.RESOURCE_FOUND, p.getCode());
        }
        
        boolean nameExist = roleRepository.existsByName(p.getName());
        if (nameExist) {
            throw new RestApiException(ApiError.RESOURCE_FOUND, p.getName());
        }


        roleRepository.save(p);
    }
    
    /**
     * 更新角色
     * @param u
     * @return
     */
    public void updateRole(RoleDTO u, Role p) {
    	
		if (!u.getCode().equals(p.getCode())) {
			boolean codeExist = roleRepository.existsByCode(u.getCode());
			if (codeExist) {
				throw new RestApiException(ApiError.RESOURCE_FOUND, u.getCode());
			}
		}
		
		if (!u.getName().equals(p.getName())) {
			boolean nameExist = roleRepository.existsByName(u.getName());
			if (nameExist) {
				throw new RestApiException(ApiError.RESOURCE_FOUND, u.getName());
			}
		}
		 BeanUtils.copyProperties(u, p);
        roleRepository.save(p);
    }

    /**
     * 角色删除
     * @param id
     */
    public void deleteData(Long id) {
    	//删除角色
    	rolePermissionRepository.deleteByRoleId(id);
    	rolePermissionRepository.flush();
        //删除角色
        roleRepository.deleteById(id);
    }

    /**
     * 授权
     * @param id
     * @param type 
     * @param menuIds
     */
	public void authority(Long id, String type, Long[] menuIds) {
		//删除角色权限
    	rolePermissionRepository.deleteByRoleIdAndType(id,type);
    	rolePermissionRepository.flush();
    	//新增授权
		if (menuIds != null && menuIds.length > 0) {
			List<RolePermission> list = new ArrayList<RolePermission>();
			for (Long menuId : menuIds) {
				RolePermission rp = new RolePermission();
				rp.setRoleId(id);
				rp.setPermissionId(menuId);
				list.add(rp);
			}
			rolePermissionRepository.saveAll(list);
		}
	}
	
	/**
	 * 获取某用户权限
	 */
	public Object[] authorityOne(Long id, String type) {
		List<RolePermission> list = rolePermissionRepository.authorityOne(id,type);
		List<Long> idList = list.stream().map(e ->{
		return e.getPermissionId();
		}
		).collect(Collectors.toList());
		
		return idList.toArray();
	}
}