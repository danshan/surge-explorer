package com.shanhh.surge.exporter.service.data

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author honghao.shan
 * @since
 */
class PolicyGroupsResp : HashMap<String, List<Policy>>()

class BenchmarkResultsResp : HashMap<String, BenchmarkResult>()

class GroupTestResultsResp: HashMap<String, List<String>>()

class GroupSelectedResp: HashMap<String, String>()

data class Policy(
    val isGroup: Boolean,
    val name: String,
    val typeDescription: String,
    val lineHash: String,
    val enabled: Boolean
)

data class BenchmarkResult(
    val lastTestErrorMessage: String?,
    val lastTestScoreInMS: Int,
    val testing: Int,
)

data class DeviceResp(val devices: List<Device>)

data class Device(
    val dnsName: String?,
    val activeConnections: Long,
    val currentInSpeed: Long,
    val currentOutSpeed: Long,
    val currentSpeed: Long,
    val dhcpLastIP: String?,
    val displayIPAddress: String?,
    val dhcpGatewayEnabled: Boolean,
    val dhcpIcon: String?,
    val dhcpLastSeenTimestamp: Long,
    val dhcpWaitingToReconnect: Boolean,
    val physicalAddress: String?,
    val identifier: String,
    val inBytes: Long,
    val inBytesStat: BytesStat?,
    val outBytes: Long,
    val outBytesStat: BytesStat?,
    val name: String,
    val sourceIp: String?,
    val topHostBySingleConnectionTraffic: String?,
    val totalBytes: Long,
    val totalConnections: Long,
    val vendor: String?
)

data class BytesStat(
    val h6: Long,
    val h12: Long,
    val h24: Long,
    val m5: Long,
    val m15: Long,
    val m60: Long,
    val today: Long,
)

data class TrafficResp(
    val connector: Map<String, Traffic>,
    val startTime: Double,
    @JsonProperty("interface") val interfaces: Map<String, Traffic>
)

data class Traffic(
    @JsonProperty("in") val inBytes: Long,
    val inMaxSpeed: Long,
    val inCurrentSpeed: Long,
    @JsonProperty("out") val outBytes: Long,
    val outMaxSpeed: Long,
    val outCurrentSpeed: Long,
    val lineHash: String?
)
