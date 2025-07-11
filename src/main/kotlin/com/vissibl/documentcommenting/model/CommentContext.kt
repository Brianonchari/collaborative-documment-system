package com.vissibl.documentcommenting.model

import com.vissibl.documentcommenting.enums.ContextType
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class CommentContext(
    @Enumerated(EnumType.STRING)
    @Column(name = "context_type")
    val type: ContextType,

    @Column(name = "context_reference")
    val reference: String
) {
}