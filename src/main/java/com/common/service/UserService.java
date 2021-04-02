package com.common.service;

import com.common.api.dto.*;
import com.common.api.qo.UserQO;
import com.common.api.response.ApiError;
import com.common.domain.Permission;
import com.common.domain.User;
import com.common.domain.UserRole;
import com.common.exceptions.RestApiException;
import com.common.repository.PermissionRepository;
import com.common.repository.RoleRepository;
import com.common.repository.UserRepository;
import com.common.repository.UserRoleRepository;
import com.common.security.Auth;
import com.common.security.JwtTokenProvider;
import com.common.util.PageUtils;
import com.common.util.PropertiesUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class UserService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private PropertiesUtils propertiesUtil;

    public Optional<User> findUser(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

    public String login(LoginDTO loginDTO) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                loginDTO.getPassword());
        authenticationManager.authenticate(token);
        return jwtTokenProvider.createToken(loginDTO.getUsername());
    }

    /**
     * change my own password
     */
    public void updatePassword(UserPasswordDTO userPasswordDTO) {
        String username = Auth.getUsername();

        if(!checkPassword(userPasswordDTO.getNewPassword())){
            throw new RestApiException(ApiError.BAD_PASSWORD);
        }

        if (!isPasswordValid(username, userPasswordDTO.getPassword())) {
            throw new RestApiException(ApiError.BAD_CREDENTIALS);
        }
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new RestApiException(ApiError.USERNAME_NOT_FOUND);
        }
        User u = optionalUser.get();
        u.setPassword(passwordEncoder.encode(userPasswordDTO.getNewPassword()));
        userRepository.save(u);
    }

    /**
     * admin reset user password
     */
    public void adminResetPassword(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new RestApiException(ApiError.USERNAME_NOT_FOUND);
        }
        User u = optionalUser.get();
        u.setPassword(passwordEncoder.encode(propertiesUtil.getDefalutPassword()));
        userRepository.save(u);
    }

    private boolean isPasswordValid(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return false;
        }
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(token);
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    private boolean checkPassword(String password){
        if(StringUtils.isBlank(password)){
            return false;
        }
        String reg="^\\d+$";
        if(password.length() < 6 || password.matches(reg)){
            return false;
        }
        return true;
    }

    public String createUser(User u) {
        String username = u.getUsername();
        boolean userExist = userRepository.existsByUsername(username);
        if (userExist) {
            throw new RestApiException(ApiError.USERNAME_EXIST, username);
        }

        u.setPassword(passwordEncoder.encode(u.getPassword()));
        u.setType("cus");
        u.setCoin(0);
        userRepository.save(u);
        return jwtTokenProvider.createToken(username);
    }

    public void updateUser(User u) {
        userRepository.save(u);
    }

    public UserInfoDTO whoami() {
        String username = Auth.getUsername();
        Optional<User> optUser = userRepository.findByUsername(username);
        if (!optUser.isPresent()) {
            throw new RestApiException(ApiError.USERNAME_NOT_FOUND);
        }
        return enrich(optUser.get());
    }

    private static Comparator<Permission> permissionComparator = new Comparator<Permission>() {

        @Override
        public int compare(Permission o1, Permission o2) {
            return o1.getSort() - o2.getSort();
        }

    };

    private static Comparator<MenuDTO> menuComparator = new Comparator<MenuDTO>() {

        @Override
        public int compare(MenuDTO o1, MenuDTO o2) {
            return o1.getSort() - o2.getSort();
        }

    };

    private UserInfoDTO enrich(User u) {
        UserInfoDTO userInfo = new UserInfoDTO();
        UserDTO user = modelMapper.map(u, UserDTO.class);
        user.setPassword(null);
        userInfo.setUser(user);

        List<Permission> permissions = permissionRepository.findUserPermissions(u.getUsername());
        enrichMenus(userInfo, permissions);
        enrichFunctions(userInfo, permissions);
        return userInfo;
    }

    private void enrichMenus(UserInfoDTO user, List<Permission> permissions) {
        List<MenuDTO> webMenus = getMenus(permissions, Permission.TYPE_WEB_MENU);
        user.setWebMenus(webMenus);

        List<MenuDTO> appMenus = getMenus(permissions, Permission.TYPE_APP_MENU);
        user.setAppMenus(appMenus);
    }

    private List<Permission> getMenuPermissions(List<Permission> permissions, String type) {
        if (hasAllPermission(permissions)) {
            return permissionRepository.findByType(type);
        }
        return permissions.stream()
                .filter(p -> type.equals(p.getType()))
                .collect(Collectors.toList());
    }

    private List<MenuDTO> getMenus(List<Permission> permissions, String type) {
        List<MenuDTO> menus = new ArrayList<>();

        permissions = getMenuPermissions(permissions, type);
        permissions.stream()
                .sorted(permissionComparator)
                .forEach(p -> this.addMenu(menus, p));
        sortMenus(menus);

        return menus;
    }

    private boolean hasAllPermission(List<Permission> permissions) {
        return permissions.stream()
                .anyMatch(p -> Permission.ALL_PERMISSION.equals(p.getCode()));
    }

    private void enrichFunctions(UserInfoDTO user, List<Permission> permissions) {
        if (hasAllPermission(permissions)) {
            permissions = permissionRepository.findByType(Permission.TYPE_FUNCTION);
        }
        List<String> webFunctions = permissions.stream()
                .filter(p -> Permission.TYPE_FUNCTION.equals(p.getType()))
                .map(p -> p.getCode())
                .collect(Collectors.toList());
        user.setFunctions(webFunctions);

        if (hasAllPermission(permissions)) {
            permissions = permissionRepository.findByType(Permission.TYPE_APP_FUNCTION);
        }
        List<String> appFunctions = permissions.stream()
                .filter(p -> Permission.TYPE_APP_FUNCTION.equals(p.getType()))
                .map(p -> p.getCode())
                .collect(Collectors.toList());
        user.setAppFunctions(appFunctions);
    }

    private void addMenu(List<MenuDTO> menus, Permission p) {
        Long parentId = p.getParentId();
        if (parentId == null) {
            menus.add(transform(p));
            return;
        }

        MenuDTO parent = findMenu(menus, parentId);
        if (parent == null) {
            log.error("error finding parent menu {}", parentId);
            return;
        }

        List<MenuDTO> children = parent.getChildren();
        if (children == null) {
            children = new ArrayList<>();
            parent.setChildren(children);
        }
        children.add(transform(p));
    }

    private MenuDTO findMenu(List<MenuDTO> menus, Long id) {
        if (CollectionUtils.isEmpty(menus)) {
            return null;
        }

        for (MenuDTO m : menus) {
            if (id.equals(m.getId())) {
                return m;
            }
        }

        for (MenuDTO m : menus) {
            MenuDTO n = findMenu(m.getChildren(), id);
            if (n != null) {
                return n;
            }
        }

        return null;
    }

    private MenuDTO transform(Permission p) {
        return modelMapper.map(p, MenuDTO.class);
    }

    private void sortMenus(List<MenuDTO> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return;
        }
        menus.sort(menuComparator);
        for (MenuDTO m : menus) {
            sortMenus(m.getChildren());
        }
    }

    public List<RoleDTO> findUserRole(Long userId) {
        return roleRepository.findByUserId(userId)
                .stream()
                .map(r -> modelMapper.map(r, RoleDTO.class))
                .collect(Collectors.toList());
    }

    public void updateUserRole(Long userId, List<Long> roles) {
        long deleted = userRoleRepository.deleteByUserId(userId);
        userRoleRepository.flush();
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        List<UserRole> userRoles = roles.stream()
                .map(roleId -> new UserRole(userId, roleId))
                .collect(Collectors.toList());
        userRoles = userRoleRepository.saveAll(userRoles);
        log.debug("role of user {} updated, {} roles deleted, {} roles added", userId, deleted, userRoles.size());
    }

    public List<String> findAccounts4AndroidMsg() {
        return userRepository.findUserByPermission(Permission.VIOLATION_PUSH_MSG_ANDROID)
                .stream()
                .map(u -> u.getUsername())
                .distinct()
                .collect(Collectors.toList());
    }

    public Page<UserDTO> findUsers(UserQO q, Pageable pageable) {

        Page<UserDTO> data = userRepository.findUsers(q, pageable);
        return PageUtils.transform(data, e -> {
            //密码置空
            e.setPassword(null);
            //角色列表
            if(StringUtils.isNotBlank(e.getRoleId())){
                String[] ids = e.getRoleId().split(",");
                List<Long> longList = Arrays.asList(ids)
                        .stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                //集合转换为数组
                Long[] idArr =  longList.toArray(new Long[]{});
                e.setRoles(roleRepository.findByIdIn(idArr));
            }

            return e;
        });
//        return PageUtils.transform(data, UserDTO.class);
    }

    /**
     * 用户删除
     */
    public void deleteData(Long id) {
        //删除用户角色
        userRoleRepository.deleteByUserId(id);
        userRoleRepository.flush();
        //删除用户
        userRepository.deleteById(id);
    }

    public String getUserType() {
        String username = Auth.getUsername();
        Optional<User> optUser = userRepository.findByUsername(username);
        if (!optUser.isPresent()) {
            throw new RestApiException(ApiError.USERNAME_NOT_FOUND);
        }else{
            User user = optUser.get();
            return user.getType();
        }
    }
}