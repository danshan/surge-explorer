package com.shanhh.surge.exporter.service

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

    @Scheduled(fixedRate = 1000)
    fun fetchDevices() {
        surgeService.registerDevices()
    }

    @Scheduled(fixedRate = 1000)
    fun fetchTraffic() {
        surgeService.registerTraffic()
    }

    @Scheduled(fixedRate = 60_000)
    fun fetchSubscriptions() {
        surgeService.registerSubscriptions()
    }
}