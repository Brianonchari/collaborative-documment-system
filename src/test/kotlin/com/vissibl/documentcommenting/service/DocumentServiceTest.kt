package com.vissibl.documentcommenting.service

import com.vissibl.documentcommenting.dto.CreateDocumentRequest
import com.vissibl.documentcommenting.dto.DocumentResponse
import com.vissibl.documentcommenting.dto.PagedResponse
import com.vissibl.documentcommenting.mappers.DocumentMapper
import com.vissibl.documentcommenting.model.Document
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

class DocumentServiceTest {
  private lateinit var documentRepository: DocumentRepository
  private lateinit var documentService: DocumentService

  @BeforeEach
  fun setUp() {
    documentRepository = mockk()
    documentService = DocumentService(documentRepository)
    mockkObject(DocumentMapper)
  }

  @Test
  fun `get all documents returns a paginated list`() {
    // Given
    val document =
      Document(
        id = UUID.randomUUID(),
        title = "Title",
        content = "Lorem ispum",
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
      )

    val documentResponse =
      DocumentResponse(id = document.id!!, title = document.title, content = document.content)

    val pageable = PageRequest.of(0, 10)
    val page = PageImpl(listOf(document), pageable, 1)

    every { documentRepository.findAll(pageable) } returns page
    every { DocumentMapper.toResponse(document) } returns documentResponse

    // when
    val response: PagedResponse<DocumentResponse> = documentService.getAllDocuments(0, 10)

    // then
    assertEquals(0, response.pageNumber)
    assertEquals(10, response.size)
    assertEquals(1, response.totalNumberOfElements)
    assertEquals(1, response.totalPages)
    assertEquals(1, response.content.size)
    assertEquals("Title", response.content.first().title)

    verify { documentRepository.findAll(pageable) }
    verify { DocumentMapper.toResponse(document) }
  }

  @Test
  fun `should create document and return response `() {
    // Given
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")
    val request = CreateDocumentRequest("This document is a sample", "lorem ispum lorem ispum")
    val document =
      Document(documentId, request.title, request.content, Instant.now(), Instant.now())

    val saved = document.copy(id = documentId)
    val response = DocumentResponse(id = saved.id!!, title = saved.title, content = saved.content)

    every { DocumentMapper.toEntity(request) } returns document
    every { documentRepository.save(document) } returns document
    every { DocumentMapper.toResponse(saved) } returns response

    // when
    val result = documentService.createDocument(request)

    // then
    assertEquals(response.id, result.id)
    verify { DocumentMapper.toEntity(request) }
    verify { documentRepository.save(document) }
    verify { DocumentMapper.toResponse(saved) }
  }
}
