package io.bankbridge.handler;

import spark.Service;

public class MockRemotes {

	public static void startMockRemoteServerOnPort(Service http) {


		http.get("/bes", (request, response) -> "{\n" +
				"\"bic\":\"PARIATURDEU0XXX\",\n" + 
				"\"name\":\"Banco de espiritu santo\",\n" + 
				"\"countryCode\":\"GB\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/cs", (request, response) -> "{\n" +
				"\"bic\":\"CUPIDATATSP1XXX\",\n" + 
				"\"name\":\"Credit Sweets\",\n" + 
				"\"countryCode\":\"CH\",\n" + 
				"\"auth\":\"open-id\"\n" + 
				"}");
		http.get("/rbf", (request, response) -> "{\n" +
				"\"bic\":\"DOLORENOR2XXX\",\n" + 
				"\"name\":\"Royal Bank of Fun\",\n" + 
				"\"countryCode\":\"GB\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/bcd", (request, response) -> "{\n" +
				"\"bic\":\"DESERUNTSP3XXX\",\n" + 
				"\"name\":\"Banco Con Deserts\",\n" + 
				"\"countryCode\":\"SP\",\n" + 
				"\"auth\":\"ssl-certificate\"\n" + 
				"}");
		http.get("/mbn", (request, response) -> "{\n" +
				"\"bic\":\"MOLLITNOR4XXX\",\n" + 
				"\"name\":\"Mbanken\",\n" + 
				"\"countryCode\":\"NO\",\n" + 
				"\"auth\":\"open-id\"\n" + 
				"}");
		http.get("/mbs", (request, response) -> "{\n" +
				"\"bic\":\"MOLLITSWE5XXX\",\n" + 
				"\"name\":\"Mbanken\",\n" + 
				"\"countryCode\":\"SE\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/br", (request, response) -> "{\n" +
				"\"bic\":\"REPSP6XXX\",\n" + 
				"\"name\":\"Banco Republico\",\n" + 
				"\"countryCode\":\"SP\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/amt", (request, response) -> "{\n" +
				"\"bic\":\"ANIMDEU7XXX\",\n" + 
				"\"name\":\"Animat\",\n" + 
				"\"countryCode\":\"DE\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/bdr", (request, response) -> "{\n" +
				"\"bic\":\"DODEU8XXX\",\n" + 
				"\"name\":\"Bank Dariatur\",\n" + 
				"\"countryCode\":\"CH\",\n" + 
				"\"auth\":\"open-id\"\n" + 
				"}");
		http.get("/bds", (request, response) -> "{\n" +
				"\"bic\":\"DOLORENOR9XXX\",\n" + 
				"\"name\":\"Bank Dolores\",\n" + 
				"\"countryCode\":\"NO\",\n" + 
				"\"auth\":\"ssl-certificate\"\n" + 
				"}");
		http.get("/con", (request, response) -> "{\n" +
				"\"bic\":\"CONSSWE10XXX\",\n" + 
				"\"name\":\"Constantie Bank\",\n" + 
				"\"countryCode\":\"SE\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/nnb", (request, response) -> "{\n" +
				"\"bic\":\"NONNOR11XXX\",\n" + 
				"\"name\":\"Norway National Bank\",\n" + 
				"\"countryCode\":\"NO\",\n" + 
				"\"auth\":\"ssl-certificate\"\n" + 
				"}");
		http.get("/nsb", (request, response) -> "{\n" +
				"\"bic\":\"NSAVNOR12XXX\",\n" + 
				"\"name\":\"National Savings Bank\",\n" + 
				"\"countryCode\":\"NO\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/bnu", (request, response) -> "{\n" +
				"\"bic\":\"MOLLITSP13XXX\",\n" + 
				"\"name\":\"Bank Nulla\",\n" + 
				"\"countryCode\":\"PT\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/onb", (request, response) -> "{\n" +
				"\"bic\":\"VELITDEU14XXX\",\n" + 
				"\"name\":\"One Nations Bank\",\n" + 
				"\"countryCode\":\"DE\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/fgg", (request, response) -> "{\n" +
				"\"bic\":\"FIRSTSP15XXX\",\n" + 
				"\"name\":\"First Guarantee Group\",\n" + 
				"\"countryCode\":\"PT\",\n" + 
				"\"auth\":\"ssl-certificate\"\n" + 
				"}");
		http.get("/blc", (request, response) -> "{\n" +
				"\"bic\":\"ULLAMCOSP16XXX\",\n" + 
				"\"name\":\"Bank Ullamco\",\n" + 
				"\"countryCode\":\"SP\",\n" + 
				"\"auth\":\"ssl-certificate\"\n" + 
				"}");
		http.get("/lnb", (request, response) -> "{\n" +
				"\"bic\":\"NULLASP17XXX\",\n" + 
				"\"name\":\"Last National Bank\",\n" + 
				"\"countryCode\":\"NO\",\n" + 
				"\"auth\":\"ssl-certificate\"\n" + 
				"}");
		http.get("/scu", (request, response) -> "{\n" +
				"\"bic\":\"SOARCDEU18XXX\",\n" + 
				"\"name\":\"Soar Credit Union\",\n" + 
				"\"countryCode\":\"DE\",\n" + 
				"\"auth\":\"oauth\"\n" + 
				"}");
		http.get("/csh", (request, response) -> "{\n" +
				"\"bic\":\"ETSWE19XXX\",\n" + 
				"\"name\":\"Cash Financial\",\n" + 
				"\"countryCode\":\"SE\",\n" + 
				"\"auth\":\"ssl-certificate\"\n" + 
				"}");
	}

}