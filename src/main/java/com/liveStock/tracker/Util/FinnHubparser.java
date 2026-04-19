package com.liveStock.tracker.Util;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class FinnHubparser {

    private final ObjectMapper mapper = new ObjectMapper();
}
