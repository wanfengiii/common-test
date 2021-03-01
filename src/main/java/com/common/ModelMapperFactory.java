package com.common;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public abstract class ModelMapperFactory {

    public static ModelMapper getObject() {
        ModelMapper modelMapper = new ModelMapper();
        // use strict strategy here, coz basicly what we need is properties copy
        // the default matching strategy does not fit our needs
        // eg: property value of "cameraId" will be copied to property "id" with default strategy configured
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

}
