package com.vissibl.documentcommenting.controller

import com.vissibl.documentcommenting.dto.*
import com.vissibl.documentcommenting.service.CommentService
import com.vissibl.documentcommenting.service.DocumentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("v1/documents")
@Tag(name = "Documents", description = "Endpoints to manage documents and thier comments")
class DocumentsController(
    private val documentService: DocumentService,
    private val commentService: CommentService
) {

    @Operation(summary = "Get paginated list of documents")
    @ApiResponse(responseCode = "200")
    @GetMapping
    fun getDocuments(@RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "10") size: Int)
            : ResponseEntity<PagedResponse<DocumentResponse>> {
        return ResponseEntity.ok(documentService.getAllDocuments(page, size))
    }

    @Operation(summary = "Create a new document")
    @ApiResponse(responseCode = "201")
    @PostMapping
    fun createDocument(@RequestBody request: CreateDocumentRequest): ResponseEntity<DocumentResponse> =
        ResponseEntity.status(201)
            .body(documentService.createDocument(request))

    @Operation(summary = "Get document by id")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{id}")
    fun getDocument(@PathVariable id: UUID): ResponseEntity<DocumentResponse> =
        ResponseEntity.ok(documentService.getDocumentById(id))

    @Operation(summary = "Update document by id")
    @ApiResponse(responseCode = "200")
    @PutMapping("/{id}")
    fun updateDocument(
        @PathVariable id: UUID,
        @RequestBody @Valid request: CreateDocumentRequest
    ): ResponseEntity<DocumentResponse> = ResponseEntity.ok(documentService.updateDocument(id, request))

    @Operation(summary = "delete document")
    @ApiResponse(responseCode = "204")
    @DeleteMapping("/{id}")
    fun deleteDocument(@PathVariable id: UUID): ResponseEntity<Void> {
        documentService.deleteDocument(id)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Add comment to documet")
    @ApiResponse(responseCode = "201")
    @PostMapping("/{id}/comments")
    fun addComment(
        @PathVariable id: UUID,
        @RequestBody @Valid request: CreateCommentRequest
    ): ResponseEntity<CommentResponse> {
        val comment = commentService.addComment(request.copy(documentId = id))
        return ResponseEntity.status(201)
            .body(comment)
    }

    @Operation(summary = "Get paginated list of a document")
    @GetMapping("/{id}/comments")
    fun getComments(
        @PathVariable id: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PagedResponse<CommentResponse>> {
        val comments = commentService.getCommentsForDocument(id, page, size)
        return ResponseEntity.ok(comments)
    }
}