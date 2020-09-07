package com.chinalife.search.client;

import com.chinalife.clerk.api.ScoreApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("clerk-service")
public interface ScoreClient extends ScoreApi {
}
