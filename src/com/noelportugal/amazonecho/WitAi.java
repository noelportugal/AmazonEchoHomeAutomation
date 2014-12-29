/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noelportugal.amazonecho;

import java.net.URLEncoder;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author nportuga
 */
public class WitAi {
    private final HttpClient httpclient = HttpClientBuilder.create().build();
    private String token;
    
    public WitAi(String token){
        this.token = token;
    }
    
    public String getJson(String text){
        String output = "";
        try {
                        
            HttpGet httpGet = new HttpGet("https://api.wit.ai/message?v=20141226&q=" + URLEncoder.encode(text, "UTF-8"));
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            StatusLine responseStatus = httpResponse.getStatusLine();
            int statusCode = responseStatus.getStatusCode();
            if (statusCode == 200) {
                httpResponse.getEntity();
                output = new BasicResponseHandler().handleResponse(httpResponse);
            }
        }catch(Exception e){
            System.err.println("httpGet Error: " + e.getMessage());
        }
        
        return output;
    }
    
    public String getStatus(String text){
        String ret = "off";
        try{
        
            String json = getJson(text);

            Object obj = JSONValue.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray values = (JSONArray) jsonObject.get("outcomes");
            JSONObject outcome = (JSONObject)values.get(0);

            JSONObject on_off = (JSONObject)outcome.get("entities");
            JSONArray on_off_values = (JSONArray) on_off.get("on_off");
            JSONObject value = (JSONObject)on_off_values.get(0);
            ret = value.get("value").toString();
        
        }catch (Exception e){
          ret = "off";  
        }
        
        
        return ret.toLowerCase();
    }
    
    public WitEntity getEntity(String text){
        WitEntity witEntity = new WitEntity();
        
        try{
            String json = getJson(text);
            
            //System.out.println(json);

            Object obj = JSONValue.parse(json);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray values = (JSONArray) jsonObject.get("outcomes");
            JSONObject outcome = (JSONObject)values.get(0);

            JSONObject entities = (JSONObject)outcome.get("entities");

            JSONArray subject = (JSONArray) entities.get("message_subject");
            JSONObject subject_value = (JSONObject)subject.get(0);
            witEntity.setSubject(subject_value.get("value").toString());

            JSONArray state = (JSONArray) entities.get("on_off");
            JSONObject state_value = (JSONObject)state.get(0);
            witEntity.setState(state_value.get("value").toString());
        
        }catch (Exception e){
            witEntity = null;  
        }
        
        return witEntity;
    }
    
}
