package io.bankbridge;

import io.bankbridge.model.response.BankV1Response;
import io.bankbridge.model.response.BankV2Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankCallsTests {

    @Test
    public void bankV1ResponseAssertingUserInputs(){
        BankV1Response bankV1Response = new BankV1Response();
        String products[] = new String []{"payments", "account"};
        bankV1Response.setName("Bank of Love");
        bankV1Response.setId("color1234");
        bankV1Response.setCountryCode("NO");
        bankV1Response.setAuth("oauth");
        assertEquals("color1234", bankV1Response.getId());
        assertEquals("Bank of Love", bankV1Response.getName());
        assertEquals("NO", bankV1Response.getCountryCode());
        assertEquals("oauth", bankV1Response.getAuth());
    }

    @Test
    public void bankV2ResponseAssertingUserInputs(){
        BankV2Response bankV2Response = new BankV2Response();
        bankV2Response.setAuth("oauth");
        bankV2Response.setCountryCode("SE");
        bankV2Response.setId("color1234");
        bankV2Response.setName("Bank of Love");
        assertEquals("oauth", bankV2Response.getAuth());
        assertEquals("Bank of Love", bankV2Response.getName());
        assertEquals("SE", bankV2Response.getCountryCode());
        assertEquals("color1234", bankV2Response.getId());
    }
}
