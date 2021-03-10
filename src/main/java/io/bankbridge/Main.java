package io.bankbridge;
import static spark.Service.ignite;
import static spark.Spark.get;
import static spark.Spark.port;

import io.bankbridge.handler.LogHandlerClass;
import io.bankbridge.handler.MockRemotes;
import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;
import spark.Service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		localhost_8080.get("/v1/banks/filter-by-country-code/:countryCode", (request3, response3) -> BanksCacheBased.filterByCountryCode(request3));
		localhost_8080.get("/v1/banks/filter-by-country-code/", (request2, response2) -> BanksCacheBased.filterByCountryCode(request2));
		localhost_8080.get("/v2/banks/filter-by-country-code/:countryCode", (request, response) -> BanksRemoteCalls.filterByCountryCode(request));
		localhost_8080.get("/v2/banks/filter-by-country-code/", (request, response) -> BanksRemoteCalls.filterByCountryCode(request));
		localhost_8080.get("/v1/banks/filter-by-auth/", (request1, response1) -> BanksCacheBased.filterByAuth(request1));
		localhost_8080.get("/v1/banks/filter-by-auth/:auth", (request, response) -> BanksCacheBased.filterByAuth(request));
		localhost_8080.get("/v2/banks/filter-by-auth/:auth", (request3, response3) -> BanksRemoteCalls.filterByAuth(request3));
		localhost_8080.get("/v2/banks/filter-by-auth/", (request2, response2) -> BanksRemoteCalls.filterByAuth(request2));
		localhost_8080.get("/v1/banks/filter-by-product/", (request1, response1) -> BanksCacheBased.filterByProduct(request1));
		localhost_8080.get("/v1/banks/filter-by-product/:product", (request, response) -> BanksCacheBased.filterByProduct(request));
	}

	static void startMockRemoteServerOn(){
		Service localhost_1234 = ignite().port(1234);
		MockRemotes.startMockRemoteServerOnPort(localhost_1234);
	}
}