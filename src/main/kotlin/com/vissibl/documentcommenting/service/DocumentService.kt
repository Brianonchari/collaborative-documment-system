package com.vissibl.documentcommenting.service

import com.vissibl.documentcommenting.dto.CreateDocumentRequest
import com.vissibl.documentcommenting.dto.DocumentResponse
import com.vissibl.documentcommenting.dto.PagedResponse
import com.vissibl.documentcommenting.exceptions.ResourceNotFoundException
import com.vissibl.documentcommenting.mappers.DocumentMapper
import com.vissibl.documentcommenting.model.Document
import com.vissibl.documentcommenting.repository.DocumentRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class DocumentService(private val documentRepository: DocumentRepository) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getAllDocuments(page: Int, size: Int): PagedResponse<DocumentResponse> {
        logger.info("fetching documents")
        val pageable: Pageable = PageRequest.of(page, size)
        val result = documentRepository.findAll(pageable)
        return PagedResponse(
            pageNumber = result.number,
            size = result.size,
            totalNumberOfElements = result.totalElements,
            totalPages = result.totalPages,
            content = result.content.map { DocumentMapper.toResponse(it) }
        )
    }

    fun createDocument(request: CreateDocumentRequest): DocumentResponse {
        val doc = DocumentMapper.toEntity(request)
        val saved = documentRepository.save(doc)
        logger.info("Created document with  id  ${saved.id}")
        return DocumentMapper.toResponse(saved)
    }

    fun getById(id: UUID): Document {
        return documentRepository.findByIdOrNull(id)
            ?: throw ResourceNotFoundException("Document not found: $id")
    }

    fun getDocumentById(id: UUID): DocumentResponse {
        var document = documentRepository.findById(id).orElseThrow { ->
            ResourceNotFoundException("Document with id $id not found")
        }
        return DocumentMapper.toResponse(document)
    }

    fun updateDocument(id: UUID, request: CreateDocumentRequest): DocumentResponse {
        val existing = documentRepository.findById(id).orElseThrow { ->
            ResourceNotFoundException("Document with id $id not found")
        }

        val updated = existing.copy(
            title = request.title,
            content = request.content,
            updatedAt = Instant.now()
        )
        val saved = documentRepository.save(updated)
        logger.info("Document updated: $id")
        return DocumentMapper.toResponse(saved)
    }

    fun deleteDocument(id: UUID) {
        val document = documentRepository.findById(id).orElseThrow { ->
            ResourceNotFoundException("Document with id $id not found")
        }
        documentRepository.delete(document)
        logger.info("Document deleted: $id")
    }
}