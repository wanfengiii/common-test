package com.common.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeValueDTO {

	 /**
     * code
     */
    private String value;

    /**
     * value
     */
    private String text;
    
}
