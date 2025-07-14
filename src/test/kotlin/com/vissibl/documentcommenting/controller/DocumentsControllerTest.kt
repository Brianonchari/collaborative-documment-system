package com.vissibl.documentcommenting.controller

import com.vissibl.documentcommenting.config.MockBeansConfig
import com.vissibl.documentcommenting.dto.CommentResponse
import com.vissibl.documentcommenting.dto.CreateDocumentRequest
import com.vissibl.documentcommenting.dto.DocumentResponse
import com.vissibl.documentcommenting.dto.PagedResponse
import com.vissibl.documentcommenting.dto.UpdateDocumentRequest
import com.vissibl.documentcommenting.enums.ContextType
import com.vissibl.documentcommenting.exceptions.ResourceNotFoundException
import com.vissibl.documentcommenting.model.Document
import com.vissibl.documentcommenting.repository.DocumentRepository
import com.vissibl.documentcommenting.service.CommentService
import com.vissibl.documentcommenting.service.DocumentService
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import java.time.Instant
import java.util.Optional
import java.util.UUID
import kotlin.test.Test
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
@WebMvcTest(DocumentsController::class)
@Import(MockBeansConfig::class)
class DocumentsControllerTest {
   @Autowired
   lateinit var mockMvc: MockMvc
    @Autowired lateinit var documentService: DocumentService
   @Autowired lateinit var commentsService: CommentService

  private val objectMapper = ObjectMapper()


  @Test
  fun `given GET documents, when request is made, return 200 OK`() {
    val pagedResponse =
      PagedResponse(
        0,
        10,
        10,
        1,
        listOf(
          DocumentResponse(
            id = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c"),
            title = "Test Doc",
            content = "Test content",
          )
        ),
      )
    every { documentService.getAllDocuments(0, 10) } returns pagedResponse

    mockMvc
      .perform(get("/v1/documents?page=0&size=10"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.pageNumber").value(0))
      .andExpect(jsonPath("$.size").value(10))
      .andExpect(jsonPath("$.totalNumberOfElements").value(10))
      .andExpect(jsonPath("$.totalPages").value(1))
      .andExpect(jsonPath("$.content[0].title").value("Test Doc"))
  }

  @Test
  fun `given POST document when request is made, return 201 CREATED with respons`() {
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")

    val response = DocumentResponse(id = documentId, title = "Test Doc", content = "Test content")

    val request = CreateDocumentRequest(title = "Test Doc", content = "Test content")

    every { documentService.createDocument(request) } returns response

    mockMvc
      .perform(
        post("/v1/documents")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(documentId.toString()))
      .andExpect(jsonPath("$.title").value("Test Doc"))
      .andExpect(jsonPath("$.content").value("Test content"))
  }

  @Test
  fun `given GET document by id when request is made, return 200 OK with response if successful`() {
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")

    val response = DocumentResponse(id = documentId, title = "Test Doc", content = "Test content")

    every { documentService.getDocumentById(documentId) } returns response

    mockMvc
      .perform(get("/v1/documents/1d49f4fe-578a-427b-975d-653722c09d0c"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(documentId.toString()))
  }

  @Test
  fun `given GET document by id when request is made, return 404  document is not found `() {
    val documentId = UUID.randomUUID()

    every { documentService.getDocumentById(documentId) } throws
      ResourceNotFoundException("document with $documentId not found")
    mockMvc
      .perform(get("/v1/documents/$documentId"))
      .andExpect(status().isNotFound)
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.detail").value("document with $documentId not found"))
  }

  @Test
  fun `given PUT document by id when request is made, return 200 if successful `() {
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")

    val request = UpdateDocumentRequest(title = "Test Doc", content = "Test content")
    val response = DocumentResponse(id = documentId, title = "Test Doc", content = "Test content")

    every { documentService.updateDocument(documentId, request) } returns response

    mockMvc
      .perform(
        put("/v1/documents/$documentId")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.id").value(response.id.toString()))
      .andExpect(jsonPath("$.title").value(response.title))
      .andExpect(jsonPath("$.content").value(response.content))
  }

  @Test
  fun `given DELETE by document id , return 204 if successful`() {
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")

    every { documentService.deleteDocument(documentId) } just Runs

    mockMvc.perform(delete("/v1/documents/$documentId")).andExpect(status().isNoContent)
  }

  @Test
  fun `given GET comments by document id , return 200 when successful `() {
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")
    val document =
      Document(
        id = documentId,
        title = "Test Doc",
        content = "Some content",
        createdAt = Instant.now(),
        updatedAt = Instant.now().plusSeconds(5),
      )
    val pagedResponse =
      PagedResponse(
        0,
        10,
        10,
        1,
        listOf(
          CommentResponse(
            id = UUID.randomUUID(),
            text = "this is a comment",
            contextType = ContextType.PARAGRAPH,
            contextReference = "PARAGRAPH",
          )
        ),
      )
  //  every { documentRepository.findById(documentId) } returns Optional.of(document)
    every { documentService.getById(documentId) } returns document
    every { commentsService.getCommentsForDocument(documentId, 0, 10) } returns pagedResponse

    mockMvc
      .perform(get("/v1/documents/$documentId/comments?page=0&size=10"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.pageNumber").value(0))
      .andExpect(jsonPath("$.size").value(10))
      .andExpect(jsonPath("$.totalNumberOfElements").value(10))
      .andExpect(jsonPath("$.totalPages").value(1))
      .andExpect(jsonPath("$.content[0].text").value("this is a comment"))
      .andExpect(jsonPath("$.content[0].contextType").value("PARAGRAPH"))
  }
}
