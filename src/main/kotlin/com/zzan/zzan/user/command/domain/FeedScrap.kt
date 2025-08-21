package com.zzan.zzan.user.command.domain

import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "feed_scraps")
class FeedScrap(
    @Id
    @Column(length = 26)
    val id: String = UlidCreator.getUlid().toString(),

    @Column(name = "user_id", length = 26)
    val userId: String, // 스크랩한 사용자 ID

    @Column(name = "feed_id", length = 26)
    val feedId: String, // 스크랩한 피드 ID
)
