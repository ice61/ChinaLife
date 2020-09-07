package com.chinalife.search.web.impl;

import com.chinalife.search.service.InsuranceGraphService;
import com.chinalife.search.web.InsuranceGraphController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class InsuranceGraphControllerImpl implements InsuranceGraphController {

    @Autowired
    private InsuranceGraphService insuranceGraphService;

    @Override
    @GetMapping("/sort/all")
    public ResponseEntity<Map<String, Long>> sortGraphAll(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to
    ) {
        return ResponseEntity.ok(insuranceGraphService.sortGraph(from, to, null, null));
    }

    @Override
    @GetMapping("/sort/clerkId")
    public ResponseEntity<Map<String, Long>> sortGraphClerk(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam("id") String clerkId
    ) {
        return ResponseEntity.ok(insuranceGraphService.sortGraph(from, to, null, clerkId));
    }

    @Override
    @GetMapping("/sort/person")
    public ResponseEntity<Map<String, Long>> sortGraphPersosn(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @CookieValue("CL_TOKEN") String token
    ) {
        return ResponseEntity.ok(insuranceGraphService.sortGraph(from,to,token,null));
    }
}
