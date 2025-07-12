package com.vissibl.documentcommenting.controller

import com.vissibl.documentcommenting.config.MockBeansConfig
import com.vissibl.documentcommenting.dto.CreateDocumentRequest
import com.vissibl.documentcommenting.dto.DocumentResponse
import com.vissibl.documentcommenting.dto.PagedResponse
import com.vissibl.documentcommenting.service.DocumentService
import io.mockk.every
import java.util.UUID
import kotlin.test.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@Import(MockBeansConfig::class)
class DocumentsControllerTest {
  @Autowired lateinit var mockMvc: MockMvc

  @Autowired lateinit var documentService: DocumentService

  private val objectMapper = ObjectMapper()

  @Test
  fun `given GET documents, when request is made, return 200 OK`() {
    val pagedResponse =
      PagedResponse<DocumentResponse>(
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
  fun `given GET document by id when request is made, return 200 OK with response`() {
    val documentId = UUID.fromString("1d49f4fe-578a-427b-975d-653722c09d0c")

    val response = DocumentResponse(id = documentId, title = "Test Doc", content = "Test content")

    every { documentService.getDocumentById(documentId) } returns response

    mockMvc
      .perform(get("/v1/documents/1d49f4fe-578a-427b-975d-653722c09d0c"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(documentId.toString()))
  }
}
