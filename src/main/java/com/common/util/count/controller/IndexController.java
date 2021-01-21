package com.common.util.count.controller;

import com.common.util.count.annotation.RequestLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 * @date
 */
@RestController
public class IndexController {

    private String id;

    @RequestLimit(count = 4)
    @GetMapping("/tj_index")
    public String index() {
        return "access count limit index";
    }

    @RequestLimit(count = 5)
    @GetMapping("/tj_test")
    public String test(String param) {
        return "access count limit test";
    }
}