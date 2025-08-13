package com.zzan.zzan.feed.command.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import com.github.f4b6a3.ulid.UlidCreator
import java.time.LocalDateTime

@Entity
@Table(name = "feeds")
class Feed(
    @Id
    @Column(length = 26)
    val id: String = UlidCreator.getUlid().toString(),

    @Column(name = "user_id", length = 26)
    val userId: String, // 작성자 ID

    @Column(name = "image_url")
    val imageUrl: String, // 피드 대표 이미지 URL

    val star: Int = 0, // 피드 평점 (0~5)

    @Column(columnDefinition = "TEXT")
    val text: String, // 피드 내용

    @Column(name = "place_id", length = 26)
    val placeId: String?, // 피드 장소 ID

    @Column(name = "buy_place_id", length = 26)
    val buyPlaceId : String? = null, // 전통주 구매 장소 ID

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    val deletedAt: LocalDateTime? = null
)