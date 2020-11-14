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

package com.keygenqt.ormlite.files

import com.keygenqt.ormlite.base.GenFileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class OrmliteConf(
    private val processingEnv: ProcessingEnvironment,
    private val elements: List<Element>
) : GenFileSpec {
    override fun getElementOrName(): String {
        return this.javaClass.simpleName
    }

    override fun getProcessingEnvironment(): ProcessingEnvironment {
        return processingEnv
    }

    override fun addImport() {

    }

    override fun addExtension() {

    }

    override fun addType() {
        val clazz = TypeSpec.classBuilder(getElementOrName())
        elements.forEach { el ->
            clazz.addProperty(
                PropertySpec.builder(
                    el.simpleName.toString().decapitalize(),
                    el.asType().asTypeName()
                )
                    .mutable()
                    .addModifiers(KModifier.LATEINIT)
                    .build()
            )
        }
        getFile().addType(clazz.build())
    }

}