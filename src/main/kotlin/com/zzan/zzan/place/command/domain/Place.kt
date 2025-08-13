package com.zzan.zzan.place.command.domain

import jakarta.persistence.*
import com.github.f4b6a3.ulid.UlidCreator

@Entity
@Table(name = "places")
class Place(
    @Id
    @Column(length = 26)
    val id: String = UlidCreator.getUlid().toString(),

    val name: String,

    val address: String,

    val phone: String? = null,

    val x: Double,

    val y: Double,
)