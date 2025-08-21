package com.zzan.zzan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing


@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
class ZzanApplication

fun main(args: Array<String>) {
    runApplication<ZzanApplication>(*args)
}
