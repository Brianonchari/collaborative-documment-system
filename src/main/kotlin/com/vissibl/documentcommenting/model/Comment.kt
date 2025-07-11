package com.vissibl.documentcommenting.model

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "comments")
data class Comment(
  @Id @GeneratedValue var id: UUID? = null,
  @ManyToOne @JoinColumn(name = "document_id", nullable = false) val document: Document,
  @Column(name = "text") val text: String,
  @Embedded val context: CommentContext,
  @Column(name = "created_at") val createdAt: Instant = Instant.now(),
)
