package com.common.api.v1;

import com.common.api.dto.*;
import com.common.api.qo.UserQO;
import com.common.api.response.ApiError;
import com.common.api.response.DataResponse;
import com.common.domain.User;
import com.common.exceptions.RestApiException;
import com.common.security.Auth;
import com.common.service.UserService;
import com.common.util.PropertiesUtils;
import io.swagger.annotations.ApiOperationSupport;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.common.api.response.ApiError.USERNAME_NOT_FOUND;
import static com.common.security.AccessControlExpression.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/login")
    public DataResponse<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return DataResponse.of(token);
    }

    @PutMapping("/password")
    public DataResponse<String> changePassword(@Valid @RequestBody UserPasswordDTO userPasswordDTO) {
        userService.updatePassword(userPasswordDTO);
        return DataResponse.success();
    }

    @PutMapping("/resetpassword/{userId}")
    @PreAuthorize(USER_ADMIN_RESET_PASSWORD)
    public DataResponse<String> adminResetPassword(@PathVariable Long id) {
        userService.adminResetPassword(id);
        return DataResponse.success();
    }

    @GetMapping("/list")
    @PreAuthorize(USER_LIST)
    public Page<UserDTO> listUsers(UserQO q, Pageable pageable) {
        return userService.findUsers(q, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize(USER_LIST)
    public UserDTO getUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.findUser(id);
        if (!userOpt.isPresent()) {
            throw new RestApiException(USERNAME_NOT_FOUND);
        }
        UserDTO user = modelMapper.map(userOpt.get(), UserDTO.class);
        user.setPassword(null);
        return user;
    }

    @PostMapping
    @PreAuthorize(USER_CREATE)
    public DataResponse<String> createUser(@Valid @RequestBody UserMsgDTO u) {
        User user = modelMapper.map(u, User.class);
        String token = userService.createUser(user);
        return DataResponse.of(token);
    }

    @PutMapping("/{id}")
    public DataResponse<String> updateUser(@PathVariable Long id, @RequestBody UserMsgDTO u) {
        Optional<User> userOpt = userService.findUser(id);
        if (!userOpt.isPresent()) {
            return DataResponse.fail();
        }
        String username = Auth.getUsername();
        if(username.equals(u.getUsername())){
            throw new RestApiException(USERNAME_NOT_FOUND);
        }
        User user = userOpt.get();
        updateUserValue(user, u);

        userService.updateUser(user);
        return DataResponse.success();
    }

    /**
     * 用户删除
     */
    @DeleteMapping("/{id}")
    @PreAuthorize(USER_DELETE)
    public DataResponse<String> delete(@PathVariable Long id) {
        Optional<User> userOpt = userService.findUser(id);
        if (!userOpt.isPresent()) {
            return DataResponse.fail();
        }
        userService.deleteData(id);
        return DataResponse.success();
    }

    // only the following props need to be updated, use a separated api for password reset
    private void updateUserValue(User from, UserMsgDTO to) {
        from.setPhone(to.getPhone());
        from.setEmail(to.getEmail());
        from.setAddress(to.getAddress());
    }


    @GetMapping("/me")
    public UserInfoDTO whoami() {
        return userService.whoami();
    }

    @GetMapping("/{id}/role")
    @PreAuthorize(USER_LIST)
    public List<RoleDTO> getUserRole(@PathVariable Long id) {
        return userService.findUserRole(id);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize(USER_ROLE)
    public DataResponse<String> updateUserRole(@PathVariable Long id, @RequestBody List<Long> roles) {
        Optional<User> userOpt = userService.findUser(id);
        if (!userOpt.isPresent()) {
            return DataResponse.fail();
        }
        userService.updateUserRole(id, roles);
        return DataResponse.success();
    }
}