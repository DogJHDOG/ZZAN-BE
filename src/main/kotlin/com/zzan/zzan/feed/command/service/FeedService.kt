package com.zzan.zzan.feed.command.service

import com.zzan.zzan.api.feed.dto.*

interface FeedService {
    fun createFeed(request: CreateFeedRequest): FeedDetailResponse
    fun updateFeed(feedId: String, request: UpdateFeedRequest): FeedDetailResponse
    fun deleteFeed(feedId: String)
}
