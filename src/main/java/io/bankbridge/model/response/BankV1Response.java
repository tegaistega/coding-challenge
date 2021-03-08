package io.bankbridge.model.response;

import io.bankbridge.model.request.BankModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class BankV1Response {

    private String id;
    private String name;
    private String countryCode;
    private ArrayList products;
    private String auth;

    public BankV1Response(BankModel bankModel){
        this.id = bankModel.getBic();
        this.name = bankModel.getName();
        this.countryCode = bankModel.getCountryCode();
        this.products = bankModel.getProducts();
        this.auth = bankModel.getAuth();
    }

    @Override
    public String toString() {
        StringBuilder stringBuild = new StringBuilder("GetBankResponseV1{");
        stringBuild.append(", id='").append(id).append('\'');
        stringBuild.append("name='").append(name).append('\'');
        stringBuild.append(", countryCode='").append(countryCode).append('\'');
        stringBuild.append(", product=").append(products);
        stringBuild.append(", auth=").append(auth);
        stringBuild.append('}');
        return stringBuild.toString();
    }
}
