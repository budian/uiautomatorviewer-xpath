package com.newland.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * HTTP调用
 * 
 * @author hongxh
 * 
 */
public class MyHttpClient {
	
	public static String post(String url,File file,String fileName,HashMap<String, Object> paramMap) throws ClientProtocolException, IOException {  
       System.out.println(url);
		String result = "{'return_message':'服务器连接失败！','return_code':0}";
		HttpClient httpclient = new DefaultHttpClient();  
        HttpPost post = new HttpPost(url);  
        FileBody fileBody = new FileBody(file);  
        MultipartEntity entity = new MultipartEntity();
        System.out.println(entity.getContentEncoding());
        //文件参数
        if(file!=null){
            entity.addPart("pageImage", fileBody);  
            entity.addPart("fileName", new StringBody(fileName));  
        }
        //其他参数
        if(!paramMap.isEmpty()){
        	String param = new JSONObject(paramMap).toString();
//        	new JSONObject(paramMap).toString(),"multipart/form-data", "UTF-8")
        	String val = URLEncoder.encode(param, "UTF-8");
        	entity.addPart("model", new StringBody(param, Charset.forName(HTTP.UTF_8)));  
        }
        post.setEntity(entity);  
        HttpResponse response = httpclient.execute(post);  
        if(HttpStatus.SC_OK==response.getStatusLine().getStatusCode()){    
            HttpEntity entitys = response.getEntity();  
            if (entity != null) {  
                System.out.println(entity.getContentLength());  
//                System.out.println(EntityUtils.toString(entitys));  
                result = EntityUtils.toString(entitys);
                System.out.println(result);
            }  
        }  
        httpclient.getConnectionManager().shutdown();  
        return result;
    }  
	
	public static String callHttpReal(String url, HashMap<String, Object> paramMap) throws Exception {
		if (paramMap == null) {
			paramMap = new HashMap<String, Object>();
		}
		String result = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost;
		httpPost = new HttpPost(url);
		// String json = new JSONObject(paramMap).toString();
		Gson gson = new Gson();
		String json = gson.toJson(paramMap);
		System.out.println( url);
		System.out.println( "请求：---------------------begin");
		System.out.println( json);
		System.out.println( "请求：---------------------end");
		// 将JSON进行UTF-8编码,以便传输中文
		/*
		 * httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json"); String
		 * encoderJson = URLEncoder.encode(json, HTTP.UTF_8); StringEntity
		 * myEntity = new StringEntity(encoderJson);// 构造请求数据
		 * myEntity.setContentType("text/json");
		 * myEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
		 * "application/json")); httpPost.setEntity(myEntity);
		 */

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("model", json));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
				"utf-8");
		httpPost.setEntity(entity);

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		result = httpClient.execute(httpPost, responseHandler);
		httpClient.getConnectionManager().shutdown();
		return result;
	}
	
//	public void upload(String localFile){
//		          File file = new File(localFile);
//		          PostMethod filePost = new PostMethod("url");
//		          HttpClient client = new DefaultHttpClient();
//		          
//		          try {
//		              // 通过以下方法可以模拟页面参数提交
//		              filePost.setParameter("userName", "");
//		              filePost.setParameter("passwd", "");
//		 
//		             Part[] parts = { new FilePart(file.getName(), file) };
//		             filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
//		             
//		             client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
//		             
//		             int status = client.executeMethod(filePost);
//		             if (status == HttpStatus.SC_OK) {
//	                 System.out.println("上传成功");
//		             } else {
//		                 System.out.println("上传失败");
//		             }
//		         } catch (Exception ex) {
//		             ex.printStackTrace();
//		         } finally {
//		             filePost.releaseConnection();
//		         }
//		     }
}
