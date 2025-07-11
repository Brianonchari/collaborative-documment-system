package com.vissibl.documentcommenting.mappers

import com.vissibl.documentcommenting.dto.CommentResponse
import com.vissibl.documentcommenting.dto.CreateCommentRequest
import com.vissibl.documentcommenting.model.Comment
import com.vissibl.documentcommenting.model.CommentContext
import com.vissibl.documentcommenting.model.Document

object CommentsMapper {
    fun toEntity(req: CreateCommentRequest, document: Document): Comment {
        return Comment(
            document = document,
            text = req.text,
            context = CommentContext(req.contextType, req.contextReference)
        )
    }

    fun toResponse(comment: Comment): CommentResponse {
        return CommentResponse(
            id = comment.id!!,
            text = comment.text,
            contextType = comment.context.type,
            contextReference = comment.context.reference
        )
    }
}