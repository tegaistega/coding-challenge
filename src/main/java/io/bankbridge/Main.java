package io.bankbridge;
import static spark.Service.ignite;
import static spark.Spark.get;
import static spark.Spark.port;

import io.bankbridge.handler.MockRemotes;
import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;
import spark.Service;

public class Main {

	public static void main(String[] args) throws Exception {
		
		startApplicationServerOnPort();
		startMockRemoteServerOn();
	}

	static void startApplicationServerOnPort() throws Exception{
		Service localhost_8080 = ignite()
				.port(8080)
				.threadPool(20);
		BanksCacheBased.init();
		BanksRemoteCalls.init();

		localhost_8080.get("v1/banks/all", BanksCacheBased::handle);
		localhost_8080.get("v2/banks/all", BanksRemoteCalls::handle);
	}

	static void startMockRemoteServerOn(){
		Service localhost_1234 = ignite().port(1234);
		MockRemotes.startMockRemoteServerOnPort(localhost_1234);
	}
}