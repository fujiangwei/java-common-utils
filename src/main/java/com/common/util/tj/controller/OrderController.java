package com.common.util.tj.controller;

import com.common.util.tj.annotation.AnnDemo;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @date
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @RequestMapping("/all")
    @AnnDemo(value = "all", isAop = false)
    public List<String> findAll() {
        List<String> list = new ArrayList<>();
        return list;
    }

    @RequestMapping("/page")
    @AnnDemo(value = "page")
    public List<String> findPage(@RequestParam("username") String username) {
        List<String> listPage = Lists.newArrayList(username);
        return listPage;
    }
}