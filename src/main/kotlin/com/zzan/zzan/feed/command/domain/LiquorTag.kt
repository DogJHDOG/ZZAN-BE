package com.zzan.zzan.feed.command.domain

import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "liquor_tags")
class LiquorTag (
    @Id
    @Column(length = 26)
    val id: String = UlidCreator.getUlid().toString(),

    @Column(name = "liquor_id", length = 26)
    val liquorId: String, // 전통주 ID

    @Column(name = "image_id", length = 26)
    val imageId: String, // 이미지 ID

    @Column(name = "feed_id", length = 26)
    val feedId: String, // 태그 ID

    @Column(precision = 10, scale = 6)
    val x: Double, // 태그 위치 X 좌표 (0~1 범위 상대좌표)

    @Column(precision = 10, scale = 6)
    val y: Double // 태그 위치 Y 좌표 (0~1 범위 상대좌표)
)