package com.common.api.dto;

import com.common.domain.Permission;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class PermissionTreeDTO {

    /** 节点ID */
    private Long id;

    /** 节点名称 */
    private String name;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<PermissionTreeDTO> children;

    public PermissionTreeDTO()
    {

    }

    public PermissionTreeDTO(Permission permission)
    {
        this.id = permission.getId();
        this.name = permission.getName();
        this.children = permission.getChildren().stream().map(PermissionTreeDTO::new).collect(Collectors.toList());
    }

}
