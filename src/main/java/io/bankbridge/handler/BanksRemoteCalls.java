package io.bankbridge.handler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.bankbridge.model.request.BankModel;
import io.bankbridge.model.response.BankV2Response;
import spark.Request;
import spark.Response;

public class BanksRemoteCalls {

	private static Map config;

	public static void init() throws Exception {
		config = new ObjectMapper()
				.readValue(Thread.currentThread().getContextClassLoader().getResource("banks-v2.json"), Map.class);
	}

	public static String handle(Request request, Response response) throws IOException {
		List<BankV2Response> resultResponseV2 = new ArrayList<>();

		for(Object bank: config.keySet()){
			resultResponseV2.add(getBankV2ResponseFromRemoteCalls((String) config.get(bank)));
		}
		try{
			return new ObjectMapper().writeValueAsString(resultResponseV2);
		}catch (JsonProcessingException jsonProcessingException){
			throw new RuntimeException("Error while processing request");
		}
	}

	public static BankV2Response getBankV2ResponseFromRemoteCalls(String resourceName) throws IOException{
		ObjectMapper objectMapper = new ObjectMapper();

		return new BankV2Response(objectMapper.readValue(makeRemoteCalls(resourceName), BankModel.class));
	}

	public static String makeRemoteCalls(String url) throws IOException{
		URL objectUrl = new URL(url);
		HttpURLConnection urlConnection = (HttpURLConnection) objectUrl.openConnection();
		urlConnection.setRequestMethod("GET");
		int responseCode = urlConnection.getResponseCode();
		if( responseCode == HttpURLConnection.HTTP_OK){
			BufferedReader bufferedReaderInput = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			String inputLine;
			StringBuffer stringBufferResponse = new StringBuffer();

			while((inputLine = bufferedReaderInput.readLine()) != null){
				stringBufferResponse.append(inputLine);
			}
			bufferedReaderInput.close();

			return stringBufferResponse.toString();
		}else{
			return "bad";
		}
	}

	public static Object pageContentSizingForPagination(Request request, Response response) throws IOException{

		List<BankV2Response> bankV2Responses = new ArrayList<>();
		for(Object bank: config.keySet()){
			bankV2Responses.add(getBankV2ResponseFromRemoteCalls((String) config.get(bank)));
		}
		try{
			int defaultPageContentSize = 5;
			String userRequestSize = request.params(":size");
			int userRequestSizeValue = 0;

			if(userRequestSize == null || userRequestSize.isEmpty()){
				userRequestSizeValue = defaultPageContentSize;
			}else {
				userRequestSizeValue = Integer.parseInt(userRequestSize);
			}

			List<BankV2Response> bankV2ResponseList = new ArrayList<>();

			if(userRequestSizeValue<=0 || userRequestSizeValue>bankV2Responses.size()){
				response.status(400);
				return "Bad Request: Enter a value greater than zero and less than 20";

			}else{
				for(int i = 1; i<userRequestSizeValue; i++){
					bankV2ResponseList.add(bankV2Responses.get(i));
				}
			}
			return new ObjectMapper().writeValueAsString(bankV2ResponseList);
		}catch (JsonProcessingException jsonProcessingException){
			throw new RuntimeException("Error while processing request");
		}
	}

}
