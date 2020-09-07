package com.chinalife.client.client;

import com.chinalife.insurance.api.InsuranceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("insurance-service")
public interface InsuranceClient extends InsuranceApi {
}
