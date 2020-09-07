package com.chinalife.manauth.client;

import com.chinalife.clerk.api.ClerkApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("clerk-service")
public interface ClerkClient extends ClerkApi {
}
