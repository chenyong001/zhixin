package com.tansci.utils;

import com.alibaba.fastjson.JSONArray;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
/**
 * @Author chenyong
 * @Date 2022/9/19 11:26
 * @Version 1.0
 */
public class HttpClientUtil {

  public static String sendGetRequest(String url, Map<String, String> headerParams, Map<String, String> bodyParams) {
    RestTemplate client = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    Iterator var5 = headerParams.keySet().iterator();

    while(var5.hasNext()) {
      String key = (String)var5.next();
      headers.add(key, (String)headerParams.get(key));
    }

    HttpMethod method = HttpMethod.GET;
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity(bodyParams, headers);
    ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class, new Object[0]);
    return (String)response.getBody();
  }

  public static String sendPostRequest(String url, Map<String, String> headerParams, Map<String, String> bodyParams) {
    RestTemplate client = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    Iterator var5 = headerParams.keySet().iterator();

    while(var5.hasNext()) {
      String key = (String)var5.next();
      headers.add(key, (String)headerParams.get(key));
    }

    HttpMethod method = HttpMethod.POST;
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity(bodyParams, headers);
    ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class, new Object[0]);
    return (String)response.getBody();
  }

  public static String sendPostRequest2(String url, Map<String, String> headerParams, Map<String, Object> bodyParams) {
    RestTemplate client = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    Iterator var5 = headerParams.keySet().iterator();

    while(var5.hasNext()) {
      String key = (String)var5.next();
      headers.add(key, (String)headerParams.get(key));
    }

    HttpMethod method = HttpMethod.POST;
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity(bodyParams, headers);
    ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class, new Object[0]);
    return (String)response.getBody();
  }


}
