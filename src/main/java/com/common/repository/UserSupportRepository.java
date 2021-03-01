package com.common.repository;

import com.common.api.dto.UserDTO;
import com.common.api.qo.UserQO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSupportRepository {

    public Page<UserDTO> findUsers(UserQO q, Pageable pageable);

}