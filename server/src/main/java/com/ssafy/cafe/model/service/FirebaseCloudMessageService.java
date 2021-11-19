package com.ssafy.cafe.model.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.ssafy.cafe.model.dto.FcmMessage;
import com.ssafy.cafe.model.dto.FcmMessage.Message;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class FirebaseCloudMessageService {
	private static final Logger logger = LoggerFactory.getLogger(FirebaseCloudMessageService.class);

	private final String API_URL = "https://fcm.googleapis.com/v1/projects/mobile-thru-project-final/message:send";
	public final ObjectMapper objectMapper;
	
	public void sendMessageTo(String targetToken, String title, String body) throws IOException {
		String message = makeMessage(targetToken, title, body);
		
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
		Request request = new Request.Builder()
				.url(API_URL)
				.post(requestBody)
				.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
				.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
				.build();
		
		Response response = client.newCall(request).execute();
		
		System.out.println(response.body().string());
	}
	
	private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
		
		Message message = new FcmMessage.Message(null, targetToken);
		
		Map<String, String> data = new HashMap<>();
		data.put("title", title);
		data.put("body", body);
		message.setData(data);
		
		FcmMessage fcmMessage = new FcmMessage(false, message);
		
		return objectMapper.writeValueAsString(fcmMessage);
	}
	
	private String getAccessToken() throws IOException {
		String firebaseConfigPath = "firebase/mobile-thru-project-final.json";
		
		GoogleCredentials googleCredentials = GoogleCredentials
				.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
				.createScoped("https://www.googleapis.com/auth/cloud-platform");
		
		googleCredentials.refreshIfExpired();
		
		return googleCredentials.getAccessToken().getTokenValue();
		
	}
	
private List<String> clientTokens = new ArrayList<>();
    
    public FirebaseCloudMessageService(ObjectMapper objectMapper){
    	this.objectMapper = objectMapper;
    }

    
    // 클라이언트 토큰 관리
    public void addToken(String token) {
        clientTokens.add(token);
    }
    
 // 등록된 모든 토큰을 이용해서 broadcasting
    public int broadCastMessage(String title, String body) throws IOException {
       for(String token: clientTokens) {
    	   logger.debug("broadcastmessage : {},{},{}",token, title, body);
           sendMessageTo(token, title, body);
       }
       return clientTokens.size();
    }
}
