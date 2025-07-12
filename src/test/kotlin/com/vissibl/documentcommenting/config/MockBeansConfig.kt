package com.vissibl.documentcommenting.config

import com.vissibl.documentcommenting.service.DocumentService
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MockBeansConfig {
  @Bean fun documentService(): DocumentService = mockk(relaxed = true)
}
