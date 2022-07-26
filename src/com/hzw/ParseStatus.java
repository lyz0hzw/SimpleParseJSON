package com.hzw;

public enum ParseStatus {
    PARSE_OK,
    PARSE_EXPECT_VALUE,
    PARSE_INVALID_VALUE,
    PARSE_ROOT_NOT_SINGULAR,
    PARSE_INVALID_STRING_ESCAPE,       // 字符串错误
    PARSE_MISS_COMMA_OR_SQUARE_BRACKET, // 数组错误

    PARSE_MISS_KEY,
    PARSE_INVALID_KEY,
    PARSE_MISS_COLON,
    PARSE_MISS_COMMA_OR_CURLY_BRACKET,

    PARSE_OBJECT_DUPLICATE_KEY
}
