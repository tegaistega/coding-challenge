package io.bankbridge.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.model.request.BankModel;
import io.bankbridge.model.response.BankV1Response;
import io.bankbridge.model.response.BankV2Response;
import spark.Request;
import spark.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

		//Create a list to store the combined returned bank data
		List<BankV2Response> bankV2Responses = new ArrayList<>();
		for (Object bank : config.keySet()) {
			bankV2Responses.add(getBankV2ResponseFromRemoteCalls((String) config.get(bank)));
		}
		try{
			// initialize a default page content size
			int defaultPageContentSize = 5;

			// initialize the page content size a user inputs,
			// which is collected as a parameter
			String userRequestSize = request.params(":size");
			int userRequestSizeValue;

			// check if the page content size inputted by the user is null
			// check if the page content size inputted by the user is empty

			if (userRequestSize == null) {
				// if the user inputs a zero value or something less or nothing at all,
				// then assign the default value to be the page content size and store
				// it as as the request value
				userRequestSizeValue = defaultPageContentSize;

				//	convert the page content size to an int value and store value in
				//	userRequestSizeValue
			} else {
				userRequestSizeValue = Integer.parseInt(userRequestSize);
			}

			//Create a list to store the combined returned bank data
			ArrayList<BankV2Response> bankV2ResponseList;

			//check if the parsed value is less than or equal to zero or greater than the number of banks data
			if((userRequestSizeValue <= 0) || (userRequestSizeValue > bankV2Responses.size())){
				response.status(400);
				return "Bad Request: Enter a value greater than zero and less than 20";

				// if the above condition is false, then do the loop
			}else{
				bankV2ResponseList = IntStream.range(0, userRequestSizeValue)
						.mapToObj(bankV2Responses::get)
						.collect(Collectors.toCollection(ArrayList::new));
			}
			// collect the new object as a string returned in the list
			return new ObjectMapper().writeValueAsString(bankV2ResponseList);
		}catch (JsonProcessingException jsonProcessingException){
			throw new RuntimeException("Error while processing request");
		}
	}

	// Method to filter v2 banks by their country code
	public static Object filterByCountryCode(Request request, Response countryCodeResponse) throws IOException {
		//Create a temporary list to store the single returned bank data
		List<BankV2Response> bankV2ResponseList = new ArrayList<>();

		//Create a list to store the combined returned bank data
		List<BankV2Response> bankV2ResponseList1 = new ArrayList<>();

		for(Object bank: config.keySet()){
			bankV2ResponseList.add(getBankV2ResponseFromRemoteCalls(((String) config.get(bank))));
		}
		try {
			// initialize the country code parameter a user inputs,
			// 	which is collected as a parameter
			String countryCode = request.params(":countryCode");

			// check if the country code is null or the does not exist.
			//  return bad request
			if(countryCode == null){
				countryCodeResponse.status(400);
				return "Bad Request: You have entered an invalid country code";
			}else {
				//loop through the bank to get the banks with the inputted country code
				bankV2ResponseList.forEach(bank ->{

					// get all the banks with the inputted country code and ignore case and transform case
					if(bank.getCountryCode().equalsIgnoreCase(countryCode)){
						bankV2ResponseList1.add(bank);
					}
				});
			}
			// collect the new object as a string
			// and return the object bankV1ResponseList1 as a list
			return new ObjectMapper().writeValueAsString(bankV2ResponseList1);

		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}
	}

	// Method to filter v2 bank by Auth
	public static Object filterByAuth(Request request, Response authResponse) throws IOException {
		//Create a temporary list to store the single returned bank data
		List<BankV2Response> bankV2ResponseList = new ArrayList<>();

		//Create a list to store the combined returned bank data
		List<BankV2Response> bankV2ResponseList1 = new ArrayList<>();

		for(Object bank: config.keySet()){
			bankV2ResponseList.add(getBankV2ResponseFromRemoteCalls((String) config.get(bank)));
		}
		try {

			// initialize the parameter a user inputs,
			//	which is collected as a parameter
			String auth;
			auth = request.params(":auth");

			//check if auth is null or non-existing
			if (auth == null || !auth.contains(auth) || auth.isEmpty()){
				authResponse.status(400);
				return "Bad Request: You have entered an invalid auth type";
			}
			// if auth exists, loop through the banks data and fetch the banks
			// that have the auth type and add it to the list of banks
			else {
				bankV2ResponseList.forEach(bank -> {
					if (bank.getAuth().equals(auth)) {
						bankV2ResponseList1.add(bank);
					}
				});
			}
			// collect the new object as a string
			// and return the object bankV1ResponseList1 as a list
			return new ObjectMapper().writeValueAsString(bankV2ResponseList1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}
	}

}
