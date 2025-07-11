package com.vissibl.documentcommenting.repository

import com.vissibl.documentcommenting.model.Comment
import com.vissibl.documentcommenting.model.Document
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CommentsRepository: JpaRepository<Comment, UUID> {
    fun findAllByDocument(document: Document,pageable: Pageable): Page<Comment>
}