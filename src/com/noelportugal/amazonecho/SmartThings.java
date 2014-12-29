/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.noelportugal.amazonecho;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author nportuga
 */
public class SmartThings {
    private String client_id;
    private String api_key;
    private String bearer;
    private final HttpClient httpclient = HttpClientBuilder.create().build();
    
    public SmartThings(String client_id, String api_key, String bearer){
        this.client_id = client_id;
        this.api_key = api_key;
        this.bearer = bearer;
    }
    
    public void authenticate(){
    
    }
    
    public String setApp(String url, String body){
        String output = "";
        try {
                       
            HttpPut httpPut = new HttpPut(url);
            httpPut.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + bearer );
            
            StringEntity xmlEntity = new StringEntity(body);
            httpPut.setEntity(xmlEntity);
            
            HttpResponse httpResponse = httpclient.execute(httpPut);
            httpResponse.getEntity();
            output = new BasicResponseHandler().handleResponse(httpResponse);
            
            if (xmlEntity != null) {
                EntityUtils.consume(xmlEntity);
            }

        }catch(Exception e){
            System.err.println("setApp Error: " + e.getMessage());
        }
        
        return output;
        
    }
    
    
}
