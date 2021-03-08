package io.bankbridge.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankModel {
	
	public String bic;
	public String name;
	public String countryCode;
	public String auth;
	public ArrayList products;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("BankModel{");
		sb.append("bic='").append(bic).append('\'');
		sb.append(", name='").append(name).append('\'');
		sb.append(", countryCode='").append(countryCode).append('\'');
		sb.append(", auth='").append(auth).append('\'');
		sb.append(", products=").append(products);
		sb.append('}');
		return sb.toString();
	}
}
