package com.zzan.zzan.feed.command.domain

import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "liquor_tags")
data class LiquorTag(
    @Id
    @Column(length = 26)
    val id: String = UlidCreator.getUlid().toString(),

    @Column(name = "liquor_id", length = 26)
    val liquorId: String,

    @Column(name = "liquor_name", length = 50)
    val liquorName: String,

    @Column(name = "image_id", length = 26)
    val imageId: String,

    @Column(name = "feed_id", length = 26)
    val feedId: String,

    val score: Double? = null,

    // columnDefinition으로 정확한 SQL 타입 지정
    @Column(columnDefinition = "DECIMAL(10,8)")
    val tagX: Double,

    @Column(columnDefinition = "DECIMAL(10,8)")
    val tagY: Double
) {
    // JPA 요구사항을 위한 기본 생성자
    constructor() : this("", "", "", "", "", null, 0.0, 0.0)
}
