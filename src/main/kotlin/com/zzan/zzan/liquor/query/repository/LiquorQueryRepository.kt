package com.zzan.zzan.liquor.query.repository

import com.zzan.zzan.api.liquor.dto.LiquorDetailResponse
import com.zzan.zzan.liquor.command.domain.Liquor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface LiquorQueryRepository : JpaRepository<Liquor, String> {

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                l.id,
                l.name,
                l.type,
                l.score,
                l.description,
                l.food_pairing,
                l.volume,
                l.content,
                l.awards,
                l.etc,
                l.image_url,
                l.brewery
            FROM liquors l
            WHERE l.id = :id
        """,
    )
    fun getLiquorById(id: String): LiquorDetailResponse?
}
