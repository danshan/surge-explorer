package com.shanhh.surge.exporter.service

import com.shanhh.surge.exporter.service.data.BenchmarkResult
import com.shanhh.surge.exporter.service.data.Device
import com.shanhh.surge.exporter.service.data.Policy
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import org.springframework.stereotype.Service

/**
 * @author honghao.shan
 * @since
 */
@Service
class SurgeService(val surgeClient: SurgeClient, final val meterRegistry: MeterRegistry) {

    fun registerBenchmarkResults() {
        val benchmarkResults = findBenchmarkResults()
        val groups = findPolicyGroups()

        for ((group, policies) in groups) {
            for (policy in policies) {
                val benchmark = benchmarkResults[policy.lineHash]
                if (benchmark != null) {
                    val tags = Tags.of(
                        "policy_group", group,
                        "policy_name", policy.name,
                        "policy_hash", policy.lineHash,
                    )
                    meterRegistry.gauge("surge_benchmark", tags, benchmark.lastTestScoreInMS)
                }
            }
        }
    }

    fun registerDevices() {
        val devices = findClients()

        for (device in devices) {
            val deviceKey = "${device.identifier}-${device.sourceIp}-${device.physicalAddress}-${device.name}"
            val tags = Tags.of(
                "identifier", device.identifier,
                "source_ip", device.sourceIp ?: "",
                "physical_address", device.physicalAddress ?: "",
                "name", device.name,
            )
            meterRegistry.gauge("surge_client_active_connections", tags, device.activeConnections)
            meterRegistry.gauge("surge_client_current_in_speed", tags, device.currentInSpeed)
            meterRegistry.gauge("surge_client_current_out_speed", tags, device.currentOutSpeed)
            meterRegistry.gauge("surge_client_current_speed", tags, device.currentSpeed)
            meterRegistry.gauge("surge_client_in_bytes", tags, device.inBytes)
            meterRegistry.gauge("surge_client_out_bytes", tags, device.outBytes)
            meterRegistry.gauge("surge_client_total_bytes", tags, device.totalBytes)
            meterRegistry.gauge("surge_client_total_connections", tags, device.totalConnections)
        }
    }

    fun findBenchmarkResults(): Map<String, BenchmarkResult> {
        val results = surgeClient.findBenchmarkResults()
        return results
    }

    fun findPolicyGroups(): Map<String, List<Policy>> {
        val results = surgeClient.findPolicyGroups()
        return results
    }

    fun findClients(): List<Device> {
        val results = surgeClient.findDevices()
        return results
    }

}