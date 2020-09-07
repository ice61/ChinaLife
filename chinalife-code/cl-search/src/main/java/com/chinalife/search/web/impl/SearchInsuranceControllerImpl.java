package com.chinalife.search.web.impl;

import com.chinalife.search.service.SearchInsuranceService;
import com.chinalife.search.vo.InsurancePageResult;
import com.chinalife.search.web.SearchInsuranceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SearchInsuranceControllerImpl implements SearchInsuranceController {

    @Autowired
    private SearchInsuranceService searchInsuranceService;


    @Override
    @GetMapping("/insurance/page")
    public ResponseEntity<InsurancePageResult> search(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "from",required = false)String from,
            @RequestParam(value = "to",required = false)String to
            ) {
        return ResponseEntity.ok(searchInsuranceService.search(page,rows,sortBy,desc,key,from,to,null,null));
    }

    @Override
    @GetMapping("/insurance/page/person")
    public ResponseEntity<InsurancePageResult> searchByPerson(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "from",required = false)String from,
            @RequestParam(value = "to",required = false)String to,
            @CookieValue("CL_TOKEN")String token
            ) {
        return ResponseEntity.ok(searchInsuranceService.search(page,rows,sortBy,desc,key,from,to,token,null));
    }

    @Override
    @GetMapping("/insurance/page/clerkId")
    public ResponseEntity<InsurancePageResult> searchByClerkId(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",defaultValue = "false")Boolean desc,
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "from",required = false)String from,
            @RequestParam(value = "to",required = false)String to,
            @RequestParam("id") String clerkId
    ) {
        return ResponseEntity.ok(searchInsuranceService.search(page,rows,sortBy,desc,key,from,to,null,clerkId));
    }


}
