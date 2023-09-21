package com.shanhh.surge.exporter.service.data

/**
 * @author honghao.shan
 * @since
 */
data class Policy(
    val isGroup: Boolean,
    val name: String,
    val typeDescription: String,
    val lineHash: String,
    val enabled: Boolean
)
