package com.chinalife.manauth.client;

import com.chinalife.auth.api.AuthApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("auth-service")
public interface AuthClient extends AuthApi {
}
