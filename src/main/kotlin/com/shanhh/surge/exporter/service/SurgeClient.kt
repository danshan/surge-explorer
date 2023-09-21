package com.shanhh.surge.exporter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.shanhh.surge.exporter.config.ExporterProperties
import com.shanhh.surge.exporter.service.data.*
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * @author honghao.shan
 * @since
 */
@Service
class SurgeClient(val exporterProperties: ExporterProperties, val objectMapper: ObjectMapper) {

    val client = HttpClient.newBuilder().build()

    fun findBenchmarkResults(): Map<String, BenchmarkResult> {
        val body = sendGet("/v1/policies/benchmark_results")
        return objectMapper.readValue(body, BenchmarkResultsResp::class.java)
    }

    fun findPolicyGroups(): Map<String, List<Policy>> {
        val body = sendGet("/v1/policy_groups")
        return objectMapper.readValue(body, PolicyGroupsResp::class.java)
    }

    fun findDevices(): List<Device> {
        val body = sendGet("/v1/devices")
        return objectMapper.readValue(body, DeviceResp::class.java).devices
    }

    fun getTraffic(): TrafficResp {
        val body = sendGet("/v1/traffic")
        return objectMapper.readValue(body, TrafficResp::class.java)
    }

    fun sendGet(uri: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${exporterProperties.surge.url}${uri}"))
            .header("X-Key", exporterProperties.surge.password)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}