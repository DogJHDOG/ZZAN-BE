package com.zzan.zzan

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<ZzanApplication>().with(TestcontainersConfiguration::class).run(*args)
}
