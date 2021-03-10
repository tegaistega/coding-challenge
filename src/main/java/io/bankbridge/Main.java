package io.bankbridge;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;
import io.bankbridge.handler.LogHandlerClass;
import io.bankbridge.handler.MockRemotes;
import spark.Service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Service.ignite;

public class Main {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void main(String[] args) throws Exception {
		LogHandlerClass.setupLogger();
		logger.info("Logger started successfully");
		try{
			throw new java.io.IOException("couldn't read file");
		}catch(IOException ioException){
			logger.log(Level.ALL, "File read error");
		}

		startApplicationServerOnPort();
		startMockRemoteServerOn();

		BanksRemoteCalls.init();
	}

	static void startApplicationServerOnPort() throws Exception{
		Service localhost_8080 = ignite()
				.port(8080)
				.threadPool(20);
		BanksCacheBased.init();
		BanksRemoteCalls.init();

		localhost_8080.get("v1/banks/all", (request1, response1) -> BanksCacheBased.handle());
		localhost_8080.get("v2/banks/all", (request4, response4) -> BanksRemoteCalls.handle());
		localhost_8080.get("/v1/banks/:size", BanksCacheBased::pageContentSizingForPagination);
		localhost_8080.get("/v1/banks/", BanksCacheBased::pageContentSizingForPagination);
		localhost_8080.get("/v2/banks/", BanksRemoteCalls::pageContentSizingForPagination);
		localhost_8080.get("/v2/banks/:size", BanksRemoteCalls::pageContentSizingForPagination);
		localhost_8080.get("/v1/banks/filter-by-country-code/:countryCode", BanksCacheBased::filterByCountryCode);
		localhost_8080.get("/v1/banks/filter-by-country-code/", BanksCacheBased::filterByCountryCode);
		localhost_8080.get("/v2/banks/filter-by-country-code/:countryCode", (request, response) -> BanksRemoteCalls.filterByCountryCode(request));
		localhost_8080.get("/v2/banks/filter-by-country-code/", (request, response) -> BanksRemoteCalls.filterByCountryCode(request));
		localhost_8080.get("/v1/banks/filter-by-auth/", BanksCacheBased::filterByAuth);
		localhost_8080.get("/v1/banks/filter-by-auth/:auth", BanksCacheBased::filterByAuth);
		localhost_8080.get("/v2/banks/filter-by-auth/:auth", (request3, response3) -> BanksRemoteCalls.filterByAuth(request3));
		localhost_8080.get("/v2/banks/filter-by-auth/", (request2, response2) -> BanksRemoteCalls.filterByAuth(request2));
		localhost_8080.get("/v1/banks/filter-by-product/", BanksCacheBased::filterByProduct);
		localhost_8080.get("/v1/banks/filter-by-product/:product", BanksCacheBased::filterByProduct);
	}

	static void startMockRemoteServerOn(){
		Service localhost_1234 = ignite().port(1234);
		MockRemotes.startMockRemoteServerOnPort(localhost_1234);
	}
}