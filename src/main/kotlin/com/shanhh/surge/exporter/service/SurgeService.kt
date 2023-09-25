package com.shanhh.surge.exporter.service

import com.google.common.util.concurrent.AtomicDouble
import com.shanhh.surge.exporter.config.ExporterProperties
import com.shanhh.surge.exporter.service.data.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import org.springframework.stereotype.Service


/**
 * @author honghao.shan
 * @since
 */
@Service
class SurgeService(
    final val surgeClient: SurgeClient,
    final val meterRegistry: MeterRegistry,
    final val exporterProperties: ExporterProperties
) {

    companion object {
        val GAUGE_CACHE = HashMap<String, AtomicDouble>()
    }

    fun registerBenchmarkResults() {
        val benchmarkResults = findBenchmarkResults()
        val groups = findPolicyGroups()

        for ((group, policies) in groups) {
            for (policy in policies) {
                val benchmark = benchmarkResults[policy.lineHash]
                if (benchmark != null) {
                    val tags = Tags.of(
                        "policy_group",
                        group,
                        "policy_name",
                        policy.name,
                        "policy_hash",
                        policy.lineHash,
                        "sp",
                        getSP(policy.name)
                    )
                    handleGauge("surge_benchmark", tags, benchmark.lastTestScoreInMS.toDouble())
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
            handleGauge("surge_client_active_connections", tags, device.activeConnections.toDouble())
            handleGauge("surge_client_current_in_speed", tags, device.currentInSpeed.toDouble())
            handleGauge("surge_client_current_out_speed", tags, device.currentOutSpeed.toDouble())
            handleGauge("surge_client_current_speed", tags, device.currentSpeed.toDouble())
            handleGauge("surge_client_in_bytes", tags, device.inBytes.toDouble())
            handleGauge("surge_client_out_bytes", tags, device.outBytes.toDouble())
            handleGauge("surge_client_total_bytes", tags, device.totalBytes.toDouble())
            handleGauge("surge_client_total_connections", tags, device.totalConnections.toDouble())
        }
    }

    fun registerTraffic() {
        val traffic = getTraffic()

        registerTraffic("surge_traffic_connector", traffic.connector)
        registerTraffic("surge_traffic_interface", traffic.interfaces)
    }

    fun registerTraffic(namePrefix: String, traffics: Map<String, Traffic>) {
        for ((name, traffic) in traffics) {
            val tags = Tags.of(
                "name", name,
            )
            handleGauge("${namePrefix}_in_bytes", tags, traffic.inBytes.toDouble())
            handleGauge("${namePrefix}_in_current_speed", tags, traffic.inCurrentSpeed.toDouble())
            handleGauge("${namePrefix}_in_max_speed", tags, traffic.inMaxSpeed.toDouble())
            handleGauge("${namePrefix}_out_bytes", tags, traffic.outBytes.toDouble())
            handleGauge("${namePrefix}_out_current_speed", tags, traffic.outCurrentSpeed.toDouble())
            handleGauge("${namePrefix}_out_max_speed", tags, traffic.outMaxSpeed.toDouble())
        }
    }

    fun registerSubscriptions() {
        for ((sp, subscription) in exporterProperties.subscriptions) {
            val subscriptionInfo = surgeClient.getSpSubscriptionInfo(subscription.url)
            if (subscriptionInfo.isPresent) {
                val tags = Tags.of(
                    "sp", sp,
                )
                val subInfo = buildSubInfo(subscriptionInfo.get())
                handleGauge("surge_subscription_upload", tags, subInfo.upload.toDouble())
                handleGauge("surge_subscription_download", tags, subInfo.download.toDouble())
                handleGauge("surge_subscription_total", tags, subInfo.total.toDouble())
            }
        }
    }

    private fun buildSubInfo(info: String): SubInfo {
        return SubInfo(
            upload = Regex("upload=(\\d+)").find(info)?.groupValues?.get(1)?.toLong() ?: 0L,
            download = Regex("download=(\\d+)").find(info)?.groupValues?.get(1)?.toLong() ?: 0L,
            total = Regex("total=(\\d+)").find(info)?.groupValues?.get(1)?.toLong() ?: 0L,
        )
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

    fun getTraffic(): TrafficResp {
        val results = surgeClient.getTraffic()
        return results
    }

    fun getSP(policyName: String): String {
        return if (policyName.startsWith('[')) policyName.substring(1, policyName.indexOf(']')) else policyName
    }


    fun handleGauge(name: String, tags: Tags, value: Double) {
        val gaugeKey = gaugeKey(name, tags)
        if (!GAUGE_CACHE.containsKey(gaugeKey)) {
            GAUGE_CACHE[gaugeKey] = AtomicDouble()
        }
        meterRegistry.gauge(name, tags, GAUGE_CACHE[gaugeKey]!!)?.set(value)
    }

    private fun gaugeKey(name: String, tags: Tags): String {
        return "${name}-" + tags.joinToString(":") { "${it.key}:${it.value}" }
    }
}