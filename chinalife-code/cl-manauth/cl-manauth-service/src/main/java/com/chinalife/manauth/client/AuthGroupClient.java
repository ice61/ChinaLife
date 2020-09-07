package com.chinalife.manauth.client;


import com.chinalife.auth.api.AuthGroupApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("auth-service")
public interface AuthGroupClient extends AuthGroupApi {
}
