package io.bankbridge.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.model.BankModelList;
import io.bankbridge.model.request.BankModel;
import io.bankbridge.model.response.BankV1Response;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class BanksCacheBased {


	public static CacheManager cacheManager;

	public static void init() throws Exception {
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder().withCache("banks", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, BankModel.class, ResourcePoolsBuilder.heap(20)))
				.build();
		cacheManager.init();

		Cache<String, BankModel> cache = cacheManager.getCache("banks", String.class, BankModel.class);
		BankModelList models = new ObjectMapper().readValue(
				Thread.currentThread().getContextClassLoader().getResource("banks-v1.json"), BankModelList.class);
		for (BankModel model : models.banks) {
			cache.put(model.bic, model);
		}
	}

	public static String handle() {
		List<BankV1Response> resultResponse = new ArrayList<>();

		for (Cache.Entry<String, BankModel> entry : cacheManager.getCache("banks", String.class, BankModel.class)) {
			resultResponse.add(new BankV1Response(entry.getValue()));
		}
		try {
			return new ObjectMapper().writeValueAsString(resultResponse);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}
	}

	public static Object pageContentSizingForPagination(Request request, Response response) {

		//Create a temporary list to store the single returned bank data
		List<BankV1Response> bankV1Responses = new ArrayList<>();

		//Create a list to store the combined returned bank data
		List<BankV1Response> bankV1Responses1 = new ArrayList<>();

		//initialize the default page content size assuming a user didn't enter one
		int defaultPageContentSize =5;

		//initialize the page content size that is returned to the user
		int userRequestSizeValue;
		cacheManager.getCache("banks", String.class, BankModel.class).forEach(entry -> bankV1Responses.add(new BankV1Response(entry.getValue())));

		try{
			// initialize the page content size a user inputs,
			// which is collected as a parameter
			String userRequestSize = request.params(":size");

			// check if the page content size inputted by the user is equal
			// check if the page content size inputted by the user is empty
			 if(userRequestSize == null || userRequestSize.isEmpty()){

				// if the user inputs a zero value or something less or nothing at all,
				// then assign the default value to be the page content size and store
				// it as as the request value
				userRequestSizeValue = defaultPageContentSize;

				//	loop through the banks in the bank response model v1
				//	and while i is less than the request value, add the new bank
				//	data to the response list bankV1Responses1
				for(int i = 0; i<userRequestSizeValue; i++){
					bankV1Responses1.add(bankV1Responses.get(i));
				}

			//	convert the page content size to an int value and store value in
			//	userRequestSizeValue
			}else {
				userRequestSizeValue = Integer.parseInt(userRequestSize);

				//check if the parsed value is less than or equal to zero or greater than the number of banks data
				if(userRequestSizeValue<=0 || userRequestSizeValue>bankV1Responses.size()){

					// if the above condition is true then return a bad request error.
					response.status(400);
					return "Bad Request: Enter a value greater than zero and less than 20";

					// if the above condition is false, then do the loop
				}else{

					for(int i = 0; i<userRequestSizeValue; i++){

						// add each bankv1Responses to the bankV1Responses list
						bankV1Responses1.add(bankV1Responses.get(i));
					}
				}
			}

			// initialize a string value to store returned bank data
			String valueAsString;

			// collect the new object as a string and store it in the the valueAsString variable
			valueAsString = new ObjectMapper().writeValueAsString(bankV1Responses1);
			return valueAsString;
		}catch (JsonProcessingException jsonProcessingException){
			throw new RuntimeException("Error while processing request");
		}
	}

	// Method to filter banks by their country code
	public static Object filterByCountryCode(Request request, Response countryCodeResponse){

		//Create a temporary list to store the single returned bank data
		List<BankV1Response> bankV1ResponseList = new ArrayList<>();

		//Create a list to store the combined returned bank data
		List<BankV1Response> bankV1ResponseList1 = new ArrayList<>();

		for (Cache.Entry<String, BankModel> entry : cacheManager.getCache("banks", String.class, BankModel.class)) {
			bankV1ResponseList.add(new BankV1Response(entry.getValue()));
		}

		try{
			// initialize the country code parameter a user inputs,
			// 	which is collected as a parameter
			String countryCode;
			countryCode = request.params(":countryCode");

			// check if the country code is null or the does not exist.
			//  return bad request
			if(countryCode == null || !countryCode.contains(countryCode)){
				countryCodeResponse.status(400);
				return "Bad Request: Invalid country code entered";
			}
			else {
				//loop through the bank to get the banks with the inputted country code
				for (BankV1Response bank : bankV1ResponseList) {

					// get all the banks with the inputted country code

					if (bank.getCountryCode().equalsIgnoreCase((countryCode.toUpperCase()))) {

						// add the banks to the list bankV1ResponseList1
						bankV1ResponseList1.add(bank);
					}
				}
			}
			// collect the new object as a string
			// and return the object bankV1ResponseList1 as a list
			 return new ObjectMapper().writeValueAsString(bankV1ResponseList1);
		}catch (JsonProcessingException jsonProcessingException){
			throw  new RuntimeException("Error while processing request");
		}
	}

	// Method to filter by Auth
	public static Object filterByAuth(Request request, Response authResponse) {

		//Create a temporary list to store the single returned bank data
		List<BankV1Response> bankV1ResponseList = new ArrayList<>();

		//Create a list to store the combined returned bank data
		List<BankV1Response> bankV1ResponseList1 = new ArrayList<>();

		cacheManager.getCache("banks", String.class, BankModel.class).forEach(entry -> bankV1ResponseList.add(new BankV1Response(entry.getValue())));
		try {

			// initialize the parameter a user inputs,
			//	which is collected as a parameter
			String auth = request.params(":auth");

			//check if auth is null or non-existing
			if (auth == null){
				authResponse.status(400);
				return "Bad Request: You have entered an invalid auth type";
			}

			// if auth exists, loop through the banks data and fetch the banks
			// that have the auth type and add it to the list of banks
			else {
				bankV1ResponseList.forEach(bank -> {
					if (bank.getAuth().equals(auth)) {
						bankV1ResponseList1.add(bank);
					}
				});
			}
			// collect the new object as a string
			// and return the object bankV1ResponseList1 as a list
			return new ObjectMapper().writeValueAsString(bankV1ResponseList1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}
	}

	public static Object filterByProduct(Request request, Response productResponse) {
		//Create a temporary list to store the single returned bank data
		List<BankV1Response> bankV1ResponseList = new ArrayList<>();

		// Create a list to store the combined returned bank data
		List<BankV1Response> bankV1ResponseList1 = new ArrayList<>();

		cacheManager.getCache("banks", String.class, BankModel.class).forEach(entry -> bankV1ResponseList.add(new BankV1Response(entry.getValue())));

		try {
			//initialize the parameter a user inputs,
			//which is collected as a parameter
			String product = request.params(":product");

			// check if the parameter is valid
			if (product == null){
				productResponse.status(400);
				return "Bad Request: You have entered an invalid product parameter";
			}


			// if the parameter is valid, loop through the banks and get the banks
			// that have the parameters and add them to a list
			else {
				bankV1ResponseList.forEach(bank -> {
					if (bank.getProducts().contains(product)) {
						bankV1ResponseList1.add(bank);
					}
				});
			}

			// collect the new object as a string
			//and return the object bankV1ResponseList1 as a list
			return new ObjectMapper().writeValueAsString(bankV1ResponseList1);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}
	}

}
