package com.highv.ecommerce.common.image.service

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.common.image.dto.ImageUrlResponse
import com.highv.ecommerce.infra.s3.S3Manager
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ImageService(
    private val s3Manager: S3Manager
) {

    fun uploadImage(file: MultipartFile, id: Long): ImageUrlResponse {
        s3Manager.uploadFile(file)
        val imageUrl = s3Manager.getFile(file.originalFilename)
        return ImageUrlResponse(imageUrl = imageUrl)
    }

    fun uploadImages(files: List<MultipartFile>, id: Long): List<ImageUrlResponse> {

        if (files.size > 9) {
            throw CustomRuntimeException(409, "이미지는 최대 9장만 등록 가능합니다.")
        } else if (files.isEmpty()) {
            throw CustomRuntimeException(409, "이미지를 등록해주세요")
        }

        val imageUrls: MutableList<ImageUrlResponse> = mutableListOf()

        files.forEach {
            s3Manager.uploadFile(it)
            val imageUrl = s3Manager.getFile(it.originalFilename)
            imageUrls.add(ImageUrlResponse(imageUrl = imageUrl))
        }

        return imageUrls
    }
}