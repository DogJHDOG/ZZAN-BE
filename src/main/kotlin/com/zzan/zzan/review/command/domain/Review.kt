package com.zzan.zzan.review.command.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import com.github.f4b6a3.ulid.UlidCreator
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
class Review(
    @Id
    @Column(length = 26)
    val id: String = UlidCreator.getUlid().toString(),

    @Column(name = "user_id", length = 26)
    val userId: String, // 작성자 ID

    @Column(name = "liquor_id", length = 26)
    val liquorId: String, // 전통주 ID

    val star: Int, // 전통주 평점 (0~5)

    @Column(columnDefinition = "TEXT")
    val comment: String?, // 리뷰 내용

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    val deletedAt: LocalDateTime? = null
)