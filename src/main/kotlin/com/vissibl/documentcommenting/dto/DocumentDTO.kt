package com.vissibl.documentcommenting.dto

import java.util.UUID

data class CreateDocumentRequest(
    val title: String,
    val content: String
)

data class DocumentResponse(
    val id: UUID,
    val title: String,
    val content: String
)

data class PagedResponse<T>(
    val pageNumber: Int,
    val size: Int,
    val totalNumberOfElements: Long,
    val totalPages: Int,
    val content: List<T>
)