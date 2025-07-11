package com.vissibl.documentcommenting.service

import com.vissibl.documentcommenting.dto.CommentResponse
import com.vissibl.documentcommenting.dto.CreateCommentRequest
import com.vissibl.documentcommenting.dto.PagedResponse
import com.vissibl.documentcommenting.exceptions.ResourceNotFoundException
import com.vissibl.documentcommenting.mappers.CommentsMapper
import com.vissibl.documentcommenting.repository.CommentsRepository
import com.vissibl.documentcommenting.repository.DocumentRepository
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CommentService(
  private val commentRepository: CommentsRepository,
  private val documentRepository: DocumentRepository,
  private val documentService: DocumentService,
) {
  private val logger = LoggerFactory.getLogger(javaClass)

  fun addComment(request: CreateCommentRequest): CommentResponse {
    var document =
      documentRepository.findById(request.documentId).orElseThrow { ->
        ResourceNotFoundException("Document with id ${request.documentId} not found")
      }
    val comment = CommentsMapper.toEntity(request, document)
    val saved = commentRepository.save(comment)
    logger.info("Added comment ${saved.id} to document ${document.id}")
    return CommentsMapper.toResponse(saved)
  }

  fun getCommentsForDocument(
    documentId: UUID,
    page: Int,
    size: Int,
  ): PagedResponse<CommentResponse> {
    val document =
      documentRepository.findById(documentId).orElseThrow { ->
        ResourceNotFoundException("Document with id $documentId not found")
      }
    val pageable = PageRequest.of(page, size)
    val result = commentRepository.findAllByDocument(document, pageable)
    return PagedResponse(
      pageNumber = result.number,
      size = result.size,
      totalNumberOfElements = result.totalElements,
      totalPages = result.totalPages,
      content = result.content.map { CommentsMapper.toResponse(it) },
    )
  }
}
