package com.hzw;

import org.junit.Test;

public class TestJsonStruct {

    @Test
    public void testBoolean(){
        testJsonStruct("true");
        testJsonStruct("false");
        testJsonStruct("null");
    }

    @Test
    public void test1() {
        testJsonStruct("-1000.");
        testJsonStruct("-.");
        testJsonStruct("-0.5");
        testJsonStruct("-1.78");
        testJsonStruct("-0");
        testJsonStruct("-0.0");
        testJsonStruct("1E10");
        testJsonStruct("1E+10");
        testJsonStruct("-1E10");
        testJsonStruct("1.234E+10");
        testJsonStruct("1.234E-10");
        testJsonStruct("1e-10000");
        testJsonStruct("4.9406564584124654e-324");
        testJsonStruct("-2.2250738585072014e-308");
        testJsonStruct("-1.7976931348623157e+308");
    }
    @Test
    public void testString(){
        testJsonStruct("\"hello world\"");
        testJsonStruct("\"\"");
        testJsonStruct("\"Hello\"");
        testJsonStruct("\"Hello\\nWorld\"");
        testJsonStruct("\"\\\" \\\\ / \\b \\f \\n \\r \\t\"");
        testJsonStruct("\"Hello\\u0000World\"");
        testJsonStruct("\"\\u0024\"");         /* Dollar sign U+0024 */
        testJsonStruct("\"\\u00A2\"");     /* Cents sign U+00A2 */
        testJsonStruct("\"\\u20AC\""); /* Euro sign U+20AC */
        testJsonStruct("\"\\uD834\\uDD1E\"");  /* G clef sign U+1D11E */

    }
    @Test
    public  void  testArray(){
        testJsonStruct("[ null , false , true , 123 , \"abc\" ]");
        testJsonStruct("[ [ ] , [ 0 ] , [ 0 , 1 ] , [ 0 , 1 , 2 ] ]");
    }
    @Test
    public  void  testObject(){
        testJsonStruct(" { " +
                "\"n\" : null , " +
                "\"f\" : false , " +
                "\"t\" : true , " +
                "\"i\" : 123 , " +
                "\"s\" : \"abc\", " +
                "\"a\" : [ 1, 2, 3 ]," +
                "\"o\" : { \"1\" : 1, \"2\" : 2, \"3\" : 3 }" +
                " } ");
    }
    @Test
    public void test_parse_miss_key() {
        testJsonStruct("{:1,");
        testJsonStruct("{1:1,");
        testJsonStruct("{true:1,");
        testJsonStruct("{false:1,");
        testJsonStruct("{null:1,");
        testJsonStruct("{[]:1,");
        testJsonStruct("{{}:1,");
        testJsonStruct("{\"a\":1,");
    }
    @Test
    public void test_parse_miss_colon() {
        testJsonStruct("{\"a\"}");
        testJsonStruct("{\"a\",\"b\"}");
    }
    @Test
    public void test_parse_miss_comma_or_curly_bracket() {
        testJsonStruct("{\"a\":1");
        testJsonStruct("{\"a\":1]");
        testJsonStruct("{\"a\":1 \"b\"");
        testJsonStruct("{\"a\":{}");
    }
    @Test
    public void test_parse_invalid_value() {
        testJsonStruct("nul");
        testJsonStruct("?");
        /* invalid number */
        testJsonStruct( "+0");
        testJsonStruct( "+1");
        testJsonStruct( ".123"); /* at least one digit before '.' */
        testJsonStruct( "1.");   /* at least one digit after '.' */
        testJsonStruct( "INF");
        testJsonStruct( "inf");
        testJsonStruct( "NAN");
        testJsonStruct( "nan");
        testJsonStruct( "[1,]");
        testJsonStruct( "[\"a\", nul]");
    }

    public void testJsonStruct(String json){
        JsonStruct value = new JsonStruct();
        ParseStatus reason = JsonStruct.parse(value, json);
        if(reason == ParseStatus.PARSE_OK) {
            System.out.printf(value.value + "\t\tType: " + value.type);
            System.out.println();
            System.out.println("Serial : " + value.stringify());
        }
        else System.out.println(reason);
    }
    

    @Test
    public void temp(){
        StringBuilder sb = new StringBuilder();
        sb.append("");
        System.out.println(sb.toString());
        System.out.println(sb.length());
    }
}
