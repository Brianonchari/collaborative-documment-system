package com.vissibl.documentcommenting.repository

import com.vissibl.documentcommenting.model.Document
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DocumentRepository: JpaRepository<Document, UUID> {
}