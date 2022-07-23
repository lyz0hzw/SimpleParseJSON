package com.hzw;

import java.util.List;
import java.util.Map;

public class JsonStruct {
    JsonType type;
    Object value;

    public JsonStruct(){
        value = null;
        type = JsonType.LEPT_NULL;
    }

    static ParseStatus parse(JsonStruct value, String json){
        Context ctx = new Context();
        ctx.json = json;
        value.type = JsonType.LEPT_NULL;
        return Parser.parse_value(ctx, value);
    }

    public String stringify(){
        Context c = new Context();
        stringifyValue(c, this);
        return c.sb.toString();
    }

    public JsonType getType() {
        return type;
    }
    public Boolean getBoolean(){
        return type == JsonType.LEPT_FALSE || type == JsonType.LEPT_TRUE ? (Boolean) value : null;
    }
    public void setBoolean(String bool){
        type =  bool.equals("true") ? JsonType.LEPT_TRUE : JsonType.LEPT_FALSE;
        value = bool.equals("true");
    }
    public Double getNumber(){
        return type == JsonType.LEPT_NUMBER ? (Double) value : null;
    }
    public void setNumber(Double number){
        type =  JsonType.LEPT_NUMBER;
        value = number;
    }
    public String getString(){
        return type == JsonType.LEPT_STRING ? (String) value : null;
    }

    public void setString(String str) {
        type =  JsonType.LEPT_STRING;
        value = str;
    }

    public void setArray(List<JsonStruct> array) {
        type =  JsonType.LEPT_ARRAY;
        value = array;
    }

    public List<JsonStruct> getArray(){
        return type == JsonType.LEPT_ARRAY && value != null ? (List<JsonStruct>) value : null;
    }
    public void setNull() {
        type = JsonType.LEPT_NULL;
        value = null;
    }

    public int get_array_size() {
        return getArray().size();
    }

    public JsonStruct get_array_element(int index) {
        return getArray().get(index);
    }

    public void setObject(Map<String, JsonStruct> obj){
        type = JsonType.LEPT_OBJECT;
        value = obj;
    }
    public Map<String, JsonStruct> getObject(){
        return type == JsonType.LEPT_OBJECT ? (Map<String, JsonStruct>)value : null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    private void stringifyValue(Context c, JsonStruct v) {
        switch (v.type) {
            case LEPT_NULL:   c.sb.append("null"); break;
            case LEPT_FALSE:  c.sb.append("false"); break;
            case LEPT_TRUE:   c.sb.append("true"); break;
            case LEPT_ARRAY:
                c.sb.append("[");
                if (v.get_array_size() != 0){
                    for (int i = 0; i < v.get_array_size(); ++i) {
                        c.sb.append(v.get_array_element(i).stringify());
                        c.sb.append(",");
                    }
                    c.sb.deleteCharAt(c.sb.length() - 1);
                }
                c.sb.append("]");
                break;
            case LEPT_STRING:
                stringify_string(c, v.getString());
                break;
            case LEPT_NUMBER:
                c.sb.append(v.getNumber());
                break;
            case LEPT_OBJECT:
                c.sb.append("{");
                Map<String, JsonStruct> map = v.getObject();
                if (map.size() != 0){
                    for(String key : map.keySet()){
                        stringify_string(c, key);
                        c.sb.append(':');
                        stringifyValue(c, map.get(key));
                        c.sb.append(",");
                    }
                    c.sb.deleteCharAt(c.sb.length() - 1);
                }
                c.sb.append("}");
                break;
            default:
                c.sb.append("xxx");
                break;
        }
    }
    private void stringify_string(Context c, String s) {
        c.sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '\"': c.sb.append("\\\""); break;
                case '\\': c.sb.append("\\\\"); break;
                case '\b': c.sb.append("\\b"); break;
                case '\f': c.sb.append("\\f"); break;
                case '\n': c.sb.append("\\n"); break;
                case '\r': c.sb.append("\\r"); break;
                case '\t': c.sb.append("\\t"); break;
                default:
                    c.sb.append(s.charAt(i));
                    break;
            }
        }
        c.sb.append('"');
    }


    static class Result{
        ParseStatus status;
        JsonStruct struct;
    }
}
