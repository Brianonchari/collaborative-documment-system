package com.vissibl.documentcommenting

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
  fromApplication<DocumentcommentingApplication>()
    .with(TestcontainersConfiguration::class)
    .run(*args)
}
