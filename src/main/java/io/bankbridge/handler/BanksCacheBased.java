package io.bankbridge.handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.bankbridge.model.response.BankV1Response;
import io.bankbridge.model.response.BankV2Response;
import lombok.var;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.bankbridge.model.request.BankModel;
import io.bankbridge.model.BankModelList;
import spark.Request;
import spark.Response;

public class BanksCacheBased {


	public static CacheManager cacheManager;

	public static void init() throws Exception {
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder().withCache("banks", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, BankModel.class, ResourcePoolsBuilder.heap(20)))
				.build();
		cacheManager.init();

		Cache cache = cacheManager.getCache("banks", String.class, BankModel.class);
		try {
			BankModelList models = new ObjectMapper().readValue(
					Thread.currentThread().getContextClassLoader().getResource("banks-v1.json"), BankModelList.class);
			for (BankModel model : models.banks) {
				cache.put(model.bic, model);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static String handle(Request request, Response response) {
		List<BankV1Response> resultResponse = new ArrayList<>();

		for (Cache.Entry<String, BankModel> entry : cacheManager.getCache("banks", String.class, BankModel.class)) {
			resultResponse.add(new BankV1Response(entry.getValue()));
		}
		try {
			String value = new ObjectMapper().writeValueAsString(resultResponse);
			return value;
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while processing request");
		}
	}

	public static Object pageContentSizingForPagination(Request request, Response response) throws IOException {

		List<BankV1Response> bankV1Responses = new ArrayList<>();
		List<BankV1Response> bankV1Responses1 = new ArrayList<>();

		int defaultPageContentSize =5;
		int userRequestSizeValue = 0;
		cacheManager.getCache("banks", String.class, BankModel.class).forEach(entry ->{
			bankV1Responses.add(new BankV1Response(entry.getValue()));
		});

		try{
			String userRequestSize = request.params(":size");

			if(userRequestSize == null || userRequestSize.isEmpty()){
				userRequestSizeValue = defaultPageContentSize;
				if(userRequestSizeValue<=0 || userRequestSizeValue>bankV1Responses.size()){
					response.status(400);
					String statusMessage = "Bad Request: Enter a value greater than zero and less than 20";
					return statusMessage;

				}else{
					for(int i = 0; i<userRequestSizeValue; i++){
						bankV1Responses1.add(bankV1Responses.get(i));
					}
				}
			}else {
				userRequestSizeValue = Integer.parseInt(userRequestSize);
				if(userRequestSizeValue<=0 || userRequestSizeValue>bankV1Responses.size()){
					response.status(400);
					return "Bad Request: Enter a value greater than zero and less than 20";

				}else{
					for(int i = 1; i<userRequestSizeValue; i++){
						bankV1Responses1.add(bankV1Responses.get(i));
					}
				}
			}
			String valueAsString;
			valueAsString = new ObjectMapper().writeValueAsString(bankV1Responses1);
			return valueAsString;
		}catch (JsonProcessingException jsonProcessingException){
			throw new RuntimeException("Error while processing request");
		}
	}

	public static Object filterByCountryCode(Request request, Response response){
		List<BankV1Response> bankV1ResponseList = new ArrayList<>();
		List<BankV1Response> bankV1ResponseList1 = new ArrayList<>();

		for (Cache.Entry<String, BankModel> entry : cacheManager.getCache("banks", String.class, BankModel.class)) {
			bankV1ResponseList.add(new BankV1Response(entry.getValue()));
		}

		try{
			String countryCode;
			countryCode = request.params(":countryCode");
			if(countryCode == null){
				bankV1ResponseList1.addAll(bankV1ResponseList);
			}else {
				for (BankV1Response bank : bankV1ResponseList) {
					if (bank.getCountryCode().toUpperCase().equals((countryCode.toUpperCase()))) {
						bankV1ResponseList1.add(bank);
					}
				}
			}
			return new ObjectMapper().writeValueAsString(bankV1ResponseList1);
		}catch (JsonProcessingException jsonProcessingException){
			throw  new RuntimeException("Error while processing request");
		}
	}


}
