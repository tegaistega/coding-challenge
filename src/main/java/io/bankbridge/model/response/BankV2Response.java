package io.bankbridge.model.response;

import io.bankbridge.model.request.BankModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class BankV2Response {

    private String id;
    private String name;
    private String countryCode;
    private String auth;
    private ArrayList products;

    public BankV2Response(BankModel bankModel){
        this.id = bankModel.getBic();
        this.name = bankModel.getName();
        this.countryCode = bankModel.getCountryCode();
        this.auth = bankModel.getAuth();
        this.products = bankModel.getProducts();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuild = new StringBuilder("GetBankResponseV2{");
        stringBuild.append("name='").append(name).append('\'');
        stringBuild.append(", id='").append(id).append('\'');
        stringBuild.append(", countryCode='").append(countryCode).append('\'');
        stringBuild.append(", products='").append(products).append('\'');
        stringBuild.append('}');
        return stringBuild.toString();
    }
}
