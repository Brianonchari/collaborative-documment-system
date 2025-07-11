package com.vissibl.documentcommenting.repository

import com.vissibl.documentcommenting.model.Document
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentRepository : JpaRepository<Document, UUID> {}
