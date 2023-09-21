package com.shanhh.surge.exporter.controller

import com.shanhh.surge.exporter.service.SurgeService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author honghao.shan
 * @since
 */
@RestController
class PrometheusController(val surgeService: SurgeService) {

    @GetMapping("/metrics")
    fun metrics() {
        return surgeService.registerBenchmarkResults()
    }

}