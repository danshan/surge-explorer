package com.shanhh.surge.exporter.service.data

import com.shanhh.surge.exporter.service.SurgeService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author honghao.shan
 * @since
 */
@Component
class SurgeScheduler(val surgeService: SurgeService) {

    @Scheduled(fixedRate = 5000)
    fun fetchBenchmarks() {
        surgeService.registerBenchmarkResults()
    }
}