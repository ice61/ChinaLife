package com.chinalife.auth.api;

import com.chinalife.auth.vo.AuthGroup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface AuthGroupApi {

    @GetMapping("/find/groups")
    List<AuthGroup> findAuthGroupByIds(@RequestParam("ids") List<Integer> ids);

}
