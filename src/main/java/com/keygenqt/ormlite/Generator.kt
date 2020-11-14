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

package com.keygenqt.ormlite

import com.google.auto.service.AutoService
import com.j256.ormlite.table.DatabaseTable
import com.keygenqt.ormlite.files.ModelExtension
import com.keygenqt.ormlite.files.OrmliteBase
import com.keygenqt.ormlite.files.OrmliteConf
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class Generator : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            DatabaseTable::class.java.name
        )
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        roundEnv?.let {
            val list = arrayListOf<Element>()
            roundEnv.getElementsAnnotatedWith(DatabaseTable::class.java).forEach {
                list.add(it)
            }
            roundEnv.getElementsAnnotatedWith(DatabaseTable::class.java).forEach {
                ModelExtension(it, processingEnv, list).save()
            }
            if (list.isNotEmpty()) {
                OrmliteConf(processingEnv, list).save()
                OrmliteBase(processingEnv, list).save()
            }
        }
        return true
    }
}