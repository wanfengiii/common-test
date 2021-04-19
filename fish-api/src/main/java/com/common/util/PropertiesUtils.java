package com.common.util;

import com.common.api.dto.CodeValueDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix="commons")
public class PropertiesUtils {

	private List<CodeValueDTO> district;

	private List<CodeValueDTO> superviseArea;

	private List<CodeValueDTO> drugGroup;

	private List<CodeValueDTO> mixGroup;
	
	private Map<String,List<CodeValueDTO>> institute;
	
	private List<CodeValueDTO> usertype;
	
	private List<CodeValueDTO> roletype;
	
	private String defalutPassword;
	
    private List<CodeValueDTO> zjtype;
	
	private List<CodeValueDTO> station;
	
	private List<CodeValueDTO> zwstatus;
	private List<CodeValueDTO> problemType;
	private List<CodeValueDTO> result;
	private List<CodeValueDTO> channel;
	private List<CodeValueDTO> handlerType;
	private List<CodeValueDTO> level;
	private List<CodeValueDTO> foodCheckItem;
	private List<CodeValueDTO> consultResult;
	private List<CodeValueDTO> consultBusiness;
	private List<CodeValueDTO> complaintLevel;
	private List<CodeValueDTO> complaintProblemType;
	private List<CodeValueDTO> complaintResult;







	/**
	 * 获取区域列名
	 * @return 
	 */
	public String getDistrictText(String value){
		
		for(CodeValueDTO dto : district) {
	    	
	    	if(dto.getValue().equals(value)){
	    		return dto.getText();
	    	}
	    }
		return null;
	}
	
	/**
	 * 获取用戶类型列名
	 * @return 
	 */
	public String getUsertypeText(String value){
		
		for(CodeValueDTO dto : usertype) {
	    	
	    	if(dto.getValue().equals(value)){
	    		return dto.getText();
	    	}
	    }
		return null;
	}
	
	/**
	 * 获取角色类型CODE
	 * @return 
	 */
	public String getRoletypeValue(String text){
		
		for(CodeValueDTO dto : roletype) {
	    	
	    	if(dto.getText().equals(text)){
	    		return dto.getValue();
	    	}
	    }
		return null;
	}
	
	/**
	 * 获取所列表
	 * @return 
	 */
	public List<CodeValueDTO> getInstituteList(String key,String value){
		
		if(StringUtils.isBlank(value)) {
			if("普陀区".equals(key)) {
				return institute.get("putuo");
			}
			return institute.get(key);
		} else {
			List<CodeValueDTO> list = institute.get(key);
			if("普陀区".equals(key)) {
				list = institute.get("putuo");
			}
			for (CodeValueDTO dto : list) {
				if(dto.getValue().equals(value)){
					List<CodeValueDTO> listResult = new ArrayList<CodeValueDTO>();
					listResult.add(dto);
					return listResult;
				}
			}
			return null;
		}
		
	}
	
	/**
	 * 获取区域列表
	 * @return 
	 */
	public List<CodeValueDTO> getDistrictList(String value){
		
		if(StringUtils.isBlank(value)) {
			return district;
		} else {
			for (CodeValueDTO dto : district) {
				if(dto.getValue().equals(value)){
					List<CodeValueDTO> listResult = new ArrayList<CodeValueDTO>();
					listResult.add(dto);
					return listResult;
				}
			}
			return null;
		}
		
	}

}