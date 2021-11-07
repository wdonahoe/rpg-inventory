package com.github.wdonahoe.rpginventory.service

import java.io.Reader
import java.io.Writer

interface File {
    fun getWriter(append: Boolean): Writer
    val reader: Reader
}