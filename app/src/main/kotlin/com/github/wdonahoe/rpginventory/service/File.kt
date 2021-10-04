package com.github.wdonahoe.rpginventory.service

import java.io.Reader
import java.io.Writer

interface File {
    val writer: Writer
    val reader: Reader
}