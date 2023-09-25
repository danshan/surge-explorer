package com.shanhh.surge.exporter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.UrlEscapers
import com.shanhh.surge.exporter.config.ExporterProperties
import com.shanhh.surge.exporter.service.data.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

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

    fun findGroupTestResults(): GroupTestResultsResp {
        val body = sendGet("/v1/policy_groups/test_results")
        return objectMapper.readValue(body, GroupTestResultsResp::class.java)
    }

    fun getGroupSelected(groupName: String): Map<String, String> {
        val body = sendGet("/v1/policy_groups/select?group_name=${UrlEscapers.urlFragmentEscaper().escape(groupName)}")
        return objectMapper.readValue(body, GroupSelectedResp::class.java)
    }

    fun getSpSubscriptionInfo(url: String): Optional<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() == HttpStatus.MOVED_PERMANENTLY.value() || response.statusCode() == HttpStatus.FOUND.value()) {
            return getSpSubscriptionInfo(response.headers().firstValue("Location").get())
        }
        return response.headers().firstValue("Subscription-Userinfo")
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