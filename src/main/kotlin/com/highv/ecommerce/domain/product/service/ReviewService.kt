package com.highv.ecommerce.domain.product.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.product.dto.CreateReviewRequest
import com.highv.ecommerce.domain.product.dto.ReviewResponse
import com.highv.ecommerce.domain.product.entity.QReview.review
import com.highv.ecommerce.domain.product.entity.Review
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.product.repository.ReviewRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,

    ) {
    private val reviews = mutableListOf<Review>()


    fun addReview(productId: Long, reviewRequest: CreateReviewRequest,buyerId:Long): DefaultResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product id $productId not found")

        val review = Review(
            buyerId = buyerId,
            product = product,
            rate = reviewRequest.rate,
            content = reviewRequest.content
        )

        reviewRepository.save(review)
        return DefaultResponse("Review added successfully")
    }

    fun getReviews(productId: Long): List<ReviewResponse> {
        return  reviewRepository.findAllByProductId(productId).map { review ->
            ReviewResponse(
                id = review.id!!,
                rate = review.rate,
                content = review.content
            )
        }
    }
}