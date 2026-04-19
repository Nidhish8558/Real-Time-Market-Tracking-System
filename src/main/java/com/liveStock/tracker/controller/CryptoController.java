package com.liveStock.tracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CryptoController {

    @GetMapping("/cryptoTracker")
    public String dashboard() {
        return "dashboard";
    }
}
