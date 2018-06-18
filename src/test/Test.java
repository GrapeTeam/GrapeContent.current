package test;

import org.json.simple.JSONObject;

import common.java.security.codec;

public class Test {
	public static void main(String[] arg){
		// 企务公开发布内容
		String info = "s:eyJpbWFnZSI6IiIsImlzdmlzYmxlIjowLCJvZ2lkIjoiNTk0MzM2ZTZjNmMyMDQxMTFjOGFhOTNlIiwic29ydCI6MCwib2lkIjoiIiwicmVhZENvdW50IjowLCJjb250ZW50Ijoi5rWL6K@wVIiwic2xldmVsIjowLCJkZXNwIjoi5rWL6K@wVIiwid2JpZCI6IjEiLCJvd25pZCI6MCwiYXR0cmlkIjowLCJzb3VjZSI6IuS8geWKoeWFrOW8gCIsInN1YnN0YXRlIjowLCJzdWJOYW1lIjpudWxsLCJtYWluTmFtZSI6Iua1i@wivlSIsIm1hbmFnZWlkIjowLCJmYXRoZXJpZCI6MCwiaXNkZWxldGUiOjAsInN0YXRlIjpudWxsLCJhdXRob3IiOiLmtYvor5UiLCJ0aW1lIjoxNTI4ODMyNDYwMDAwLCJhdHRyaWJ1dGUiOiIwIiwiaXNTdWZmaXgiOjB9";
		String ArticleInfo = codec.DecodeFastJSON(info);
		
		System.out.println(ArticleInfo);
	}
	
}
