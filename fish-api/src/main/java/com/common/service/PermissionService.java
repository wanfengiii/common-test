package com.common.service;

import com.common.api.dto.PermissionTreeDTO;
import com.common.domain.Permission;
import com.common.repository.PermissionRepository;
import com.common.security.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<PermissionTreeDTO>  permissionTree(String type){
    	List<Permission> list = permissionRepository.findByTypeLikeOrderBySort(type+"%");
    	List<Permission> menuTrees = buildPermissionTree(list);
        return menuTrees.stream().map(PermissionTreeDTO::new).collect(Collectors.toList());
    }

    /**
     * if the current user has this permission?
     */
    public boolean hasPermission(String permission) {
        return hasPermission(null, permission);
    }

    public boolean hasPermission(Authentication authentication, String permission) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return false;
            }
        }
        String username = Auth.getUsername(authentication);
        List<Permission> permissions = permissionRepository.findUserPermissions(username);
        return permissions.stream()
                .anyMatch(p -> (
                                permission.equalsIgnoreCase(p.getCode()) || Permission.ALL_PERMISSION.equals(p.getCode())
                        )
                );
    }

    /**
     * 构建前端所需要树结构
     * 
     * @return 树结构列表
     */
    public List<Permission> buildPermissionTree(List<Permission> list)
    {
        List<Permission> returnList = new ArrayList<Permission>();
        for (Iterator<Permission> iterator = list.iterator(); iterator.hasNext();)
        {
        	Permission t = (Permission) iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
//            if (null != t.getParentId() && t.getParentId() == 0)
        	if (null == t.getParentId() && !t.getCode().equals(Permission.ALL_PERMISSION))
            {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = list;
        }
        return returnList;
    }
    
    /**
     * 递归列表
     * 
     * @param list
     * @param t
     */
    private void recursionFn(List<Permission> list, Permission t)
    {
        // 得到子节点列表
        List<Permission> childList = getChildList(list, t);
        t.setChildren(childList);
        for (Permission tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                // 判断是否有子节点
                Iterator<Permission> it = childList.iterator();
                while (it.hasNext())
                {
                	Permission n = (Permission) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }
    
    /**
     * 得到子节点列表
     */
    private List<Permission> getChildList(List<Permission> list, Permission t)
    {
        List<Permission> tlist = new ArrayList<Permission>();
        Iterator<Permission> it = list.iterator();
        while (it.hasNext())
        {
        	Permission n = (Permission) it.next();
            if ((null != n.getParentId()) && (n.getParentId().longValue() == t.getId().longValue()))
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<Permission> list, Permission t)
    {
        return getChildList(list, t).size() > 0 ? true : false;
    }
}