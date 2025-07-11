package com.vissibl.documentcommenting.model

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "documents")
data class Document(
  @Id @GeneratedValue val id: UUID? = null,
  @Column(name = "title") val title: String,
  @Column(name = "content") val content: String,
  @Column(name = "created_at") val createdAt: Instant = Instant.now(),
  @Column(name = "updated_at") val updatedAt: Instant = Instant.now(),
)
