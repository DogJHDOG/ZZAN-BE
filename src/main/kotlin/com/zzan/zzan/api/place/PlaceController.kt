package com.zzan.zzan.api.place

import com.zzan.zzan.api.place.dto.PlaceResponse
import com.zzan.zzan.api.place.dto.ViewBoxRequest
import com.zzan.zzan.common.response.ApiResponse
import com.zzan.zzan.place.query.handler.PlaceQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PlaceController(
    private val placeQueryService: PlaceQueryService
) {
    @GetMapping("/places")
    fun getPlaces(request: ViewBoxRequest): ApiResponse<List<PlaceResponse>> {
        val viewBox = request.toViewBox()
        return ApiResponse.ok(placeQueryService.getPlacesInViewBox(viewBox))
    }
}
