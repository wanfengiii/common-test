package com.common.api.v1;

import com.common.api.dto.*;
import com.common.api.qo.UserQO;
import com.common.api.response.ApiError;
import com.common.api.response.DataResponse;
import com.common.domain.User;
import com.common.exceptions.RestApiException;
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

    @Autowired
    private PropertiesUtils propertiesUtil;

    @PostMapping("/login")
    public DataResponse<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return DataResponse.of(token);
    }

    @PutMapping("/password")
    @ApiOperationSupport(ignoreParameters = {"userPasswordDTO.username"})
    public DataResponse<String> changePassword(@Valid @RequestBody UserPasswordDTO userPasswordDTO) {
        userService.updatePassword(userPasswordDTO);
        return DataResponse.success();
    }

    @ApiOperationSupport(ignoreParameters = {"userPasswordDTO.userId"})
    @PutMapping("/updatePasswordByUsername")
    public DataResponse<String> updatePasswordByUsername(@Valid @RequestBody UserPasswordDTO userPasswordDTO) {
        userService.updatePasswordByUsername(userPasswordDTO);
        return DataResponse.success();
    }

    @PutMapping("/resetpassword")
    @PreAuthorize(USER_ADMIN_RESET_PASSWORD)
    public DataResponse<String> adminResetPassword(@Valid @RequestBody UserPasswordDTO userPasswordDTO) {
        if (userPasswordDTO.getUserId() == null) {
            throw new RestApiException(ApiError.METHOD_PARAM_REQUIRED, "userId");
        }
        userService.adminResetPassword(userPasswordDTO);
        return DataResponse.success();
    }

    @GetMapping("/list")
    @PreAuthorize(USER_LIST)
    public Page<UserDTO> listUsers(UserQO q, Pageable pageable) {
        return userService.findUsers(q, pageable);
    }

    @PostMapping
    @PreAuthorize(USER_CREATE)
    public DataResponse<String> createUser(@Valid @RequestBody UserDTO u) {
        User user = modelMapper.map(u, User.class);
        String token = userService.createUser(user);
        return DataResponse.of(token);
    }

    @PutMapping("/{id}")
    @PreAuthorize(USER_UPDATE)
    public DataResponse<String> updateUser(@PathVariable Long id, @RequestBody UserDTO u) {
        Optional<User> userOpt = userService.findUser(id);
        if (!userOpt.isPresent()) {
            return DataResponse.fail();
        }

        User user = userOpt.get();
        updateUserValue(user, u);

        userService.updateUser(user);
        return DataResponse.success();
    }

    // only the following props need to be updated, use a separated api for password reset
    private void updateUserValue(User from, UserDTO to) {
        from.setAvatar(to.getAvatar());
        from.setPhone(to.getPhone());
        from.setEmail(to.getEmail());
        from.setFirstname(to.getFirstname());
        from.setLastname(to.getLastname());
        from.setDistrict(to.getDistrict());
        from.setInstitute(to.getInstitute());
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

    @GetMapping("/commons/{type}")
//    @PreAuthorize(USER_LIST)
    public DataResponse<List<CodeValueDTO>> listCommons(@PathVariable String type, String district, String institute) {
        List<CodeValueDTO> list = new ArrayList<CodeValueDTO>();
        if ("institute".equals(type)) {
            list = propertiesUtil.getInstituteList(district,institute);
        } else if ("district".equals(type)) {
            list = propertiesUtil.getDistrictList(district);
        } else if ("usertype".equals(type)) {
            list = propertiesUtil.getUsertype();
        } else if ("roletype".equals(type)) {
            list = propertiesUtil.getRoletype();
        } else if ("zjtype".equals(type)) {
            list = propertiesUtil.getZjtype();
        } else if ("station".equals(type)) {
            list = propertiesUtil.getStation();
        } else if ("zwstatus".equals(type)) {
            list = propertiesUtil.getZwstatus();
        }
        return DataResponse.of(list);
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

}