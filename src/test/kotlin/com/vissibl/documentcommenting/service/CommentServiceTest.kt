package com.vissibl.documentcommenting.service

import com.vissibl.documentcommenting.dto.CommentResponse
import com.vissibl.documentcommenting.dto.CreateCommentRequest
import com.vissibl.documentcommenting.enums.ContextType
import com.vissibl.documentcommenting.mappers.CommentsMapper
import com.vissibl.documentcommenting.mappers.DocumentMapper
import com.vissibl.documentcommenting.model.Comment
import com.vissibl.documentcommenting.model.CommentContext
import com.vissibl.documentcommenting.model.Document
import com.vissibl.documentcommenting.repository.CommentsRepository
import com.vissibl.documentcommenting.repository.DocumentRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import java.time.Instant
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class CommentServiceTest {
  private lateinit var commentsRepository: CommentsRepository
  private lateinit var documentRepository: DocumentRepository
  private lateinit var commentService: CommentService

  @BeforeEach
  fun setUp() {
    commentsRepository = mockk()
    documentRepository = mockk()
    commentService = CommentService(commentsRepository, documentRepository)
    mockkObject(DocumentMapper)
    mockkObject(CommentsMapper)
  }

  @Test
  fun `given valid document id when fetching comments, return paginated response with comments`() {
    // Given
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")
    val document =
      Document(
        id = documentId,
        title = "Sample Title",
        content = "Sample content",
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
      )

    val comment =
      Comment(
        id = UUID.randomUUID(),
        document = document,
        text = "Test comment",
        context = CommentContext(type = ContextType.PARAGRAPH, reference = "30"),
      )

    val pageable = PageRequest.of(0, 10)
    val commentPage = PageImpl(listOf(comment), pageable, 1)

    every { documentRepository.findById(documentId) } returns Optional.of(document)
    every { commentsRepository.findAllByDocument(document, pageable) } returns commentPage
    every { CommentsMapper.toResponse(comment) } returns
      CommentResponse(
        id = comment.id!!,
        text = comment.text,
        contextType = comment.context.type,
        contextReference = comment.context.reference,
      )

    // When
    val result = commentService.getCommentsForDocument(documentId, 0, 10)

    // Then
    assertEquals(1, result.totalNumberOfElements)
    assertEquals("Test comment", result.content.first().text)
    assertEquals("30", result.content.first().contextReference)

    verify { documentRepository.findById(documentId) }
    verify { commentsRepository.findAllByDocument(document, pageable) }
    verify { CommentsMapper.toResponse(comment) }
  }

  @Test
  fun `given valid request body, when addComment called, then save and return comment response`() {
    // Given
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")
    val document =
      Document(
        id = documentId,
        title = "Sample Title",
        content = "Sample content",
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
      )

    val request =
      CreateCommentRequest(
        documentId = documentId,
        text = "This is a comment",
        contextType = ContextType.PARAGRAPH,
        contextReference = "30",
      )

    val comment =
      Comment(
        id = UUID.randomUUID(),
        document = document,
        text = request.text,
        context = CommentContext(request.contextType, request.contextReference),
      )

    val savedComment = comment.copy(id = UUID.randomUUID())

    val expectedResponse =
      CommentResponse(
        id = savedComment.id!!,
        text = savedComment.text,
        contextType = savedComment.context.type,
        contextReference = savedComment.context.reference,
      )

    every { documentRepository.findById(documentId) } returns Optional.of(document)
    every { CommentsMapper.toEntity(request, document) } returns comment
    every { commentsRepository.save(comment) } returns savedComment
    every { CommentsMapper.toResponse(savedComment) } returns expectedResponse

    // When
    val result = commentService.addComment(request)

    // Then
    assertEquals(expectedResponse, result)

    verify { documentRepository.findById(documentId) }
    verify { CommentsMapper.toEntity(request, document) }
    verify { commentsRepository.save(comment) }
    verify { CommentsMapper.toResponse(savedComment) }
  }
}
