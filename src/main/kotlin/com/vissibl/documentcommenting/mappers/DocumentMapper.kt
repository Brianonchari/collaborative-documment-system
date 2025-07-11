package com.vissibl.documentcommenting.mappers

import com.vissibl.documentcommenting.dto.CreateDocumentRequest
import com.vissibl.documentcommenting.dto.DocumentResponse
import com.vissibl.documentcommenting.model.Document
import java.time.Instant

object DocumentMapper {
    fun toEntity(req: CreateDocumentRequest): Document{
        return Document(
            title = req.title,
            content = req.content
        )
    }

    fun toResponse(doc: Document): DocumentResponse {
        return DocumentResponse(
            id = doc.id!!,
            title = doc.title,
            content = doc.content
        )
    }
}