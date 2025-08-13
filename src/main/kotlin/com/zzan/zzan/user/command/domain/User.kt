package com.zzan.zzan.user.command.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import com.github.f4b6a3.ulid.UlidCreator
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @Column(length = 26)
    val id: String = UlidCreator.getUlid().toString(),

    @Column(name = "kakao_id", unique = true)
    val kakaoId: String,

    @Column(name = "profile_image_url")
    val profileImageUrl: String?,

    val nickname: String?,

    @Column(name = "profile_updated_at")
    val profileUpdatedAt: LocalDateTime?,

    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    val deletedAt: LocalDateTime? = null
)