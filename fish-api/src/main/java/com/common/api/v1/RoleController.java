package com.common.api.v1;

import com.common.api.dto.PermissionTreeDTO;
import com.common.api.dto.RoleDTO;
import com.common.api.response.DataResponse;
import com.common.domain.Role;
import com.common.repository.RoleRepository;
import com.common.service.PermissionService;
import com.common.service.RoleService;
import com.common.util.PageUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.common.security.AccessControlExpression.*;

@RestController
@RequestMapping("/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;
    

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/list")
    @PreAuthorize(ROLE_LIST)
    public DataResponse<List<RoleDTO>> listRoles(@RequestParam(required = false) String q) {
        List<RoleDTO> roles = roleService.findRoles(q);
        return DataResponse.of(roles);
    }
    
    /**
     * 根据用户类型检索角色list
     * @return
     */
    @GetMapping("/listByUserType")
    @PreAuthorize(ROLE_LIST)
    public DataResponse<List<RoleDTO>> listRolesByType(@RequestParam(required = false) String usertype) {
        List<RoleDTO> roles = roleService.findRolesByType(usertype);
        return DataResponse.of(roles);
    }
    
    @GetMapping("/roleList")
    @PreAuthorize(ROLE_LIST)
    public Page<RoleDTO> rolesList(@RequestParam(required = false) String q, Pageable pageable) {
        Page<Role> roles = roleService.findRoles(q, pageable);
        return PageUtils.transform(roles, RoleDTO.class);
    }
    
    /**
     * 权限列表
     * @param type:web\app
     * @return
     */
    @GetMapping("/authority")
    @PreAuthorize(ROLE_AUTHORITY)
    public List<PermissionTreeDTO> permissions(@RequestParam String type) {
    	return permissionService.permissionTree(type);
    }
    

    @PostMapping
    @PreAuthorize(ROLE_CREATE)
    public DataResponse<String> createRole(@Valid @RequestBody RoleDTO u) {
    	Role role = modelMapper.map(u, Role.class);
    	roleService.createRole(role);
        return DataResponse.of(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ROLE_UPDATE)
    public DataResponse<String> updateRole(@PathVariable Long id, @RequestBody RoleDTO u) {
        Optional<Role> roleOpt = roleRepository.findById(id);
        if (!roleOpt.isPresent()) {
            return DataResponse.fail();
        }

        Role role = roleOpt.get();

        u.setId(id);
        roleService.updateRole(u, role);
        return DataResponse.success();
    }
    
    /**
     * 角色删除
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(ROLE_DELETE)
    public DataResponse<String> delete(@PathVariable Long id) {
    	Optional<Role> roleOpt = roleRepository.findById(id);
        if (!roleOpt.isPresent()) {
            return DataResponse.fail();
        }
        roleService.deleteData(id);
        return DataResponse.success();
    }
    
    /**
     * 授权
     */
    @PostMapping("/authority/{id}")
    @PreAuthorize(ROLE_AUTHORITY)
    public DataResponse<String> authority(@PathVariable Long id, @RequestBody RoleDTO dto){
    	Optional<Role> roleOpt = roleRepository.findById(id);
        if (!roleOpt.isPresent()) {
            return DataResponse.fail();
        }
        roleService.authority(id,dto.getType(),dto.getMenuIds());
    	return DataResponse.success();
    }
    
    /**
     * 权限列表
     * @param type:web\app
     * @return
     */
    @GetMapping("/authority/{id}")
    @PreAuthorize(ROLE_AUTHORITY)
    public Object[] authorityOne(@PathVariable Long id,@RequestParam String type) {
    	return roleService.authorityOne(id,type);
    }
}