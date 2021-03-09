package io.bankbridge.handler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.bankbridge.Main;
import io.bankbridge.model.request.BankModel;
import io.bankbridge.model.response.BankV2Response;
import spark.Request;
import spark.Response;

public class BanksRemoteCalls {

	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static Map config;


	public static void init() throws Exception {

		config = new ObjectMapper()
				.readValue(Thread.currentThread().getContextClassLoader().getResource("banks-v2.json"), Map.class);
	}

	public static String handle() throws IOException {
		List<BankV2Response> resultResponseV2 = new ArrayList<>();

		for(Object bank: config.keySet()){
			resultResponseV2.add(getBankV2ResponseFromRemoteCalls((String) config.get(bank)));
		}
		try{
			return new ObjectMapper().writeValueAsString(resultResponseV2);
		}catch (JsonProcessingException jsonProcessingException){
			logger.severe("Exception occurred");
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
			StringBuilder stringBuilderResponse = new StringBuilder();

			while((inputLine = bufferedReaderInput.readLine()) != null){
				stringBuilderResponse.append(inputLine);
			}
			bufferedReaderInput.close();

			return stringBuilderResponse.toString();

		}else{
			return "bad";
		}
	}

	public static Object pageContentSizingForPagination(Request request, Response response) throws IOException{

		List<BankV2Response> bankV2Responses = new ArrayList<>();
		for (Object bank : config.keySet()) {
			bankV2Responses.add(getBankV2ResponseFromRemoteCalls((String) config.get(bank)));
		}
		try{
			int defaultPageContentSize = 5;
			String userRequestSize = request.params(":size");
			int userRequestSizeValue = 0;

			if (userRequestSize == null) {
				userRequestSizeValue = defaultPageContentSize;
			} else {
				userRequestSizeValue = Integer.parseInt(userRequestSize);
			}

			ArrayList<BankV2Response> bankV2ResponseList;

			if((userRequestSizeValue <= 0) || (userRequestSizeValue > bankV2Responses.size())){
				response.status(400);
				String statusMessage = "Bad Request: Enter a value greater than zero and less than 20";
				return statusMessage;

			}else{
				bankV2ResponseList = IntStream.range(0, userRequestSizeValue)
						.mapToObj(bankV2Responses::get)
						.collect(Collectors.toCollection(ArrayList::new));
			}
			return new ObjectMapper().writeValueAsString(bankV2ResponseList);
		}catch (JsonProcessingException jsonProcessingException){
			throw new RuntimeException("Error while processing request");
		}
	}

	public static Object filterByCountryCode(Request request) throws IOException {
		List<BankV2Response> bankV2ResponseList = new ArrayList<>();
		List<BankV2Response> bankV2ResponseList1 = new ArrayList<>();

		for(Object bank: config.keySet()){
			bankV2ResponseList.add(getBankV2ResponseFromRemoteCalls(((String) config.get(bank))));
		}
		try {
			String countryCode = request.params(":countryCode");
			if(countryCode == null){
				bankV2ResponseList1.addAll(bankV2ResponseList);
			}else {
				bankV2ResponseList.forEach(bank ->{
					if(bank.getCountryCode().toUpperCase().equals(countryCode.toUpperCase())){
						bankV2ResponseList1.add(bank);
					}
				});
			}
			return new ObjectMapper().writeValueAsString(bankV2ResponseList1);

		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}
	}

	public static Object filterByAuth(Request request, Response response) throws IOException {
		List<BankV2Response> bankV2ResponseList = new ArrayList<>();
		List<BankV2Response> bankV2ResponseList1 = new ArrayList<>();

		for(Object bank: config.keySet()){
			bankV2ResponseList.add(getBankV2ResponseFromRemoteCalls((String) config.get(bank)));
		}
		try {
			String auth;
			auth = request.params(":auth");
			if (auth == null){
				bankV2ResponseList1.addAll(bankV2ResponseList);
			}
			else {
				bankV2ResponseList.forEach(bank -> {
					if (bank.getAuth().equals(auth)) {
						bankV2ResponseList1.add(bank);
					}
				});
			}
			return new ObjectMapper().writeValueAsString(bankV2ResponseList1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}
	}

}
