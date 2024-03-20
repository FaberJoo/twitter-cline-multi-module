package org.twitter.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.twitter.api.common.response.CommonResponse;

@RestController
@RequestMapping("/")
@Slf4j
public class HelloController {

    @GetMapping
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>(CommonResponse.success("Hello"), HttpStatus.OK);
    }
}
