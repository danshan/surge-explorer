package com.shanhh.surge.exporter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * @author honghao.shan
 * @since
 */
@Configuration
@ConfigurationProperties(prefix = "exporter", ignoreUnknownFields = false)
class ExporterProperties {

    val surge = Surge()

    data class Surge(
        var url: String = "",
        var password: String = "",
    )

}