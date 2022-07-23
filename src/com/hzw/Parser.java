package com.hzw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    /* ws = *(%x20 / %x09 / %x0A / %x0D) */
    static void parse_whitespace(Context c) {
        String p = c.json;
        int index = 0;
        while (index < p.length() && ( p.charAt(index) == ' ' || p.charAt(index) == '\t'
                || p.charAt(index) == '\n' || p.charAt(index) == '\r')){
            index++;
        }
        c.json = p.substring(index);
    }

    static ParseStatus parse_literal(Context c, JsonStruct v, String literal, JsonType type) {
        if (c.json.length() < literal.length() || !c.json.substring(0, literal.length()).equals(literal))
            return ParseStatus.PARSE_INVALID_VALUE;
        c.json = c.json.substring(literal.length());
        v.type = type;
        switch (type){
            case LEPT_NULL:
                v.setNull();
                break;
            case LEPT_TRUE:
            case LEPT_FALSE:
                v.setBoolean(literal);
                break;
        }
        return ParseStatus.PARSE_OK;
    }

    static boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private static ParseStatus parse_string(Context c, JsonStruct v) {
        String p = c.json;
        int len = p.length(), index = 0;
        if (p.charAt(0) == '\"') index++;
        StringBuilder sb = new StringBuilder();
//        sb.append("\"");
        while (index < len){
            char ch = p.charAt(index++);
            switch (ch){
                case '\"':
                    v.setString(sb.toString());
                    c.json = p.substring(index);
                    return ParseStatus.PARSE_OK;
                case '\\': // \一个反斜杠
                    if (index == len - 1) return ParseStatus.PARSE_INVALID_STRING_ESCAPE;;
                    switch (p.charAt(index++)) {
                        case '\"': sb.append('\"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/':  sb.append('/' ); break;
                        case 'b':  sb.append('\b'); break;
                        case 'f':  sb.append('\f'); break;
                        case 'n':  sb.append('\n'); break;
                        case 'r':  sb.append('\r'); break;
                        case 't':  sb.append('\t'); break;
                        case 'u':
                            System.out.println("wei shi xian");
                            break;
                        default:
                            return ParseStatus.PARSE_INVALID_STRING_ESCAPE;
                    }
                    break;
                default:
                    sb.append(ch);
            }
        }
        return ParseStatus.PARSE_OK;
    }

    static ParseStatus parse_number(Context c, JsonStruct v) {
        String p = c.json;
        int len = p.length();
        int index = 0;
        if (index < len && p.charAt(index) == '-') index++;
        if(index < len && p.charAt(index) == '0') index++;
        else {
            if (!isDigit(p.charAt(index))) return ParseStatus.PARSE_INVALID_VALUE;
            while (index < len && isDigit(p.charAt(index))) index++;
        }
        if(index < len && p.charAt(index) == '.'){
            index++;
            if (index >= len || !isDigit(p.charAt(index))) return ParseStatus.PARSE_INVALID_VALUE;
            while (index < len && isDigit(p.charAt(index))) index++;
        }
        if (index < len && (p.charAt(index) == 'e' || p.charAt(index) == 'E')) {
            index++;
            if (index < len && (p.charAt(index) == '+' || p.charAt(index) == '-')) index++;
            if (index >= len || !isDigit(p.charAt(index))) return ParseStatus.PARSE_INVALID_VALUE;
            while (index < len && isDigit(p.charAt(index))) index++;
        }
        Double parseValue = null;
        try {
            parseValue = Double.parseDouble(p.substring(0, index));
        }catch (Exception e){
            e.printStackTrace();
        }
        c.json = p.substring(index);
        v.setNumber(parseValue);
        return ParseStatus.PARSE_OK;
    }

    private static ParseStatus parse_array(Context c, JsonStruct v) {
        String p = c.json;
        int len = p.length();
        ParseStatus ret = null;
        if (p.charAt(0) == '[') c.json = c.json.substring(1);
        parse_whitespace(c);
        if (c.json.charAt(0) == ']') {
            v.setArray(new ArrayList<>());
            c.json = c.json.substring(1);
            return ParseStatus.PARSE_OK;
        }
        List<JsonStruct> array = new ArrayList<>();
        while (true){
            JsonStruct element = new JsonStruct();
            if ((ret = parse_value(c, element)) != ParseStatus.PARSE_OK)
                return ret;
            parse_whitespace(c);
            p = c.json;
            array.add(element);
            if (p.charAt(0) == ',') {
                c.json = p.substring(1);
            }else if (p.charAt(0) == ']') {
                c.json = p.substring(1);
                v.setArray(array);
                return ParseStatus.PARSE_OK;
            } else
                return ParseStatus.PARSE_MISS_COMMA_OR_SQUARE_BRACKET;
        }
    }

    private static ParseStatus parse_object(Context c, JsonStruct v){
        Map<String, JsonStruct> obj = new HashMap<>();
        if(c.json.charAt(0) == '{') c.json = c.json.substring(1);
        parse_whitespace(c);
        if(c.json.charAt(0) == '}') {
            v.setObject(obj);
            c.json = c.json.substring(1);
            return ParseStatus.PARSE_OK;
        }
        ParseStatus ret;
        while (true){
            try {
                JsonStruct element = new JsonStruct();
                if (c.json.length() <= 0 || c.json.charAt(0) != '"') {
                    return ParseStatus.PARSE_MISS_KEY;
                }
                if ((ret = parse_value(c, element)) != ParseStatus.PARSE_OK)
                    return ret;
                parse_whitespace(c);
                if (element.getString() == null) {
                    return ParseStatus.PARSE_INVALID_KEY;
                }
                String key = element.getString();
                if(obj.containsKey(key)) return ParseStatus.PARSE_OBJECT_DUPLICATE_KEY;
                element = new JsonStruct();
                if (c.json.charAt(0) != ':') {
                    return ParseStatus.PARSE_MISS_COLON;
                }
                c.json = c.json.substring(1);
                if ((ret = parse_value(c, element)) != ParseStatus.PARSE_OK)
                    return ret;
                obj.put(key, element);
                parse_whitespace(c);
                if (c.json.charAt(0) == ',') {
                    c.json = c.json.substring(1);
                    parse_whitespace(c);
                } else if (c.json.charAt(0) == '}') {
                    v.setObject(obj);
                    c.json = c.json.substring(1);
                    return ParseStatus.PARSE_OK;
                } else
                    return ParseStatus.PARSE_MISS_COMMA_OR_CURLY_BRACKET;
            }catch (StringIndexOutOfBoundsException e){
                return ParseStatus.PARSE_MISS_COMMA_OR_CURLY_BRACKET;
            }
        }
    }

    /* value = null / false / true */
    /* 提示：下面代码没处理 false / true，将会是练习之一 */
    static ParseStatus parse_value(Context c, JsonStruct v) {
        parse_whitespace(c);
        if (c.json.length() == 0) return ParseStatus.PARSE_EXPECT_VALUE;
        switch (c.json.charAt(0)) {
            case 'n':  return parse_literal(c, v, "null", JsonType.LEPT_NULL);
            case 't':  return parse_literal(c, v, "true", JsonType.LEPT_TRUE);
            case 'f':  return parse_literal(c, v, "false", JsonType.LEPT_FALSE);
            case '\"': return parse_string(c, v);
            case '[': return parse_array(c, v);
            case '{': return parse_object(c, v);
            case '\0': return ParseStatus.PARSE_EXPECT_VALUE;
            default:
                if(c.json.charAt(0) == '-' || (c.json.charAt(0) >= '0' && c.json.charAt(0) <= '9')){
                    return parse_number(c, v);
                }
                return ParseStatus.PARSE_INVALID_VALUE;
        }
    }



    ParseStatus parse(JsonStruct v, String json) {
        Context c = new Context();
        c.json = json;
        ParseStatus ret;
        //init(v);
        parse_whitespace(c);
        if ((ret = parse_value(c, v)) == ParseStatus.PARSE_OK) {
            parse_whitespace(c);
            if (!c.json.equals("") ) {
                v.type = JsonType.LEPT_NULL;
                ret = ParseStatus.PARSE_ROOT_NOT_SINGULAR;
            }
        }
        return ret;
    }
}
