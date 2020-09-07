package com.chinalife.search.client;

import com.chinalife.client.api.ClientApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("client-service")
public interface ClientClient extends ClientApi {
}
