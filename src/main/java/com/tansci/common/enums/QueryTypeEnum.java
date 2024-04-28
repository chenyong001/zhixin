package com.tansci.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum QueryTypeEnum implements CommonEnumClass<Integer,String>{
    QUERY_TYPE_TEXT(1,"文本检索"),
    QUERY_TYPE_OBJECT(2,"对象检索");

    private final Integer code;

    private final String message;
}
