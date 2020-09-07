package com.chinalife.search.web.impl;

import com.chinalife.search.service.ScoreGraphService;
import com.chinalife.search.web.ScoreGraphController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ScoreGraphControllerImpl implements ScoreGraphController {

    @Autowired
    private ScoreGraphService scoreGraphService;

    @Override
    @GetMapping("/score/all")
    public ResponseEntity<Map<String, Long>> scoreGraphAll(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam(value = "interval", defaultValue = "1") int interval
    ) {
        return ResponseEntity.ok(scoreGraphService.scoreGraph(from,to,interval,null,null));
    }

    @Override
    @GetMapping("/score/clerkId")
    public ResponseEntity<Map<String, Long>> scoreGraphClerk(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @RequestParam("id") String clerkId,
            @RequestParam(value = "interval", defaultValue = "1") int interval
    ) {
        return ResponseEntity.ok(scoreGraphService.scoreGraph(from,to,interval,null,clerkId));
    }

    @Override
    @GetMapping("/score/person")
    public ResponseEntity<Map<String, Long>> scoreGraphPerson(
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            @CookieValue("CL_TOKEN") String token,
            @RequestParam(value = "interval", defaultValue = "1") int interval
    ) {
        return ResponseEntity.ok(scoreGraphService.scoreGraph(from,to,interval,token,null));
    }

    @Override
    @GetMapping("/score/rank")
    public ResponseEntity<List<Map<String, Long>>> scoreRank(
            @RequestParam(value = "from", required = false)String from,
            @RequestParam(value = "to", required = false)String to,
            @RequestParam(value = "num",defaultValue = "5") int num,
            @RequestParam(value = "desc",defaultValue = "true")Boolean desc) {
        return ResponseEntity.ok(scoreGraphService.scoreRand(from,to,num,desc));
    }
}
