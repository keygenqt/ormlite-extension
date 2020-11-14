/*
 * Copyright 2020 Vitaliy Zarubin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.keygenqt.ormlite.base

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

interface GenFileSpec {
    companion object {
        private var FILE: FileSpec.Builder? = null
    }

    fun getFile(): FileSpec.Builder {
        FILE?.let {
            return it
        } ?: run {
            when (getElementOrName()) {
                is Element -> {
                    FILE = FileSpec.builder(
                        this.javaClass.canonicalName.substring(0, this.javaClass.canonicalName.indexOf(".files")),
                        "${(getElementOrName() as Element).simpleName}"
                    )
                }
                is String -> {
                    FILE = FileSpec.builder(
                        this.javaClass.canonicalName.substring(0, this.javaClass.canonicalName.indexOf(".files")),
                        (getElementOrName() as String)
                    )
                }
            }
            return FILE!!
        }
    }

    fun save() {
        addImport()
        addType()
        addExtension()
        getFile().build().writeTo(File(getProcessingEnvironment().options["kapt.kotlin.generated"], ""))
        FILE = null
    }

    fun getElementOrName(): Any
    fun getProcessingEnvironment(): ProcessingEnvironment
    fun addImport()
    fun addExtension()
    fun addType()
}