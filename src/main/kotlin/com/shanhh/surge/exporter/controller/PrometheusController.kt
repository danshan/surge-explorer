package com.shanhh.surge.exporter.controller

import com.shanhh.surge.exporter.service.SurgeClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author honghao.shan
 * @since
 */
@RestController
class PrometheusController(val surgeClient: SurgeClient) {

    @GetMapping("/subscribe")
    fun metrics(): String {
        return surgeClient.getSpSubscriptionInfo("https://arkdy.com/link/HYcsnTt5xrQ73tVx?clash=1").orElse("UNKNOWN")
    }

}