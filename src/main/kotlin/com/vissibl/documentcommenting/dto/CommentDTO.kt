package com.vissibl.documentcommenting.dto

import com.vissibl.documentcommenting.enums.ContextType
import java.util.UUID

data class CreateCommentRequest(
    val documentId: UUID,
    val text: String,
    val contextType: ContextType,
    val contextReference: String
)

data class CommentResponse(
    val id: UUID,
    val text: String,
    val contextType: ContextType,
    val contextReference: String
)