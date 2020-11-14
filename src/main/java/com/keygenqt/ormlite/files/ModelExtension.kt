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
import com.keygenqt.ormlite.function.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class ModelExtension(
    private val el: Element,
    private val processingEnv: ProcessingEnvironment,
    private val elements: List<Element>
) : GenFileSpec {

    override fun getElementOrName(): Element {
        return el
    }

    override fun getProcessingEnvironment(): ProcessingEnvironment {
        return processingEnv
    }

    override fun addImport() {
        getFile()
            .addImport("com.j256.ormlite.dao", "Dao")
            .addImport("com.j256.ormlite.dao", "DaoManager")
            .addImport("java.sql", "SQLException")
            .addImport("com.j256.ormlite.table", "TableUtils")
            .addImport("org.json", "JSONArray")
            .addImport("org.json", "JSONObject")
    }

    override fun addExtension() {
        getFile()
            .addFunction(ModelExtensionDelete(getElementOrName(), elements).getFunSpec())
            .addFunction(ModelExtensionCreateOrUpdate(getElementOrName(), elements).getFunSpec())
            .addFunction(ModelExtensionGetJSONObject(getElementOrName(), getProcessingEnvironment(), elements).getFunSpec())

            .addFunction(ModelExtensionClearTableWithForeign(getElementOrName(), getProcessingEnvironment()).getFunSpec())
            .addFunction(ModelExtensionClearTable(getElementOrName(), getProcessingEnvironment()).getFunSpec())
            .addFunction(ModelExtensionDao(getElementOrName(), getProcessingEnvironment()).getFunSpec())
            .addFunction(ModelExtensionFind(getElementOrName(), getProcessingEnvironment()).getFunSpec())
            .addFunction(ModelExtensionFindAll(getElementOrName(), getProcessingEnvironment()).getFunSpec())
            .addFunction(ModelExtensionFindOneAND(getElementOrName(), getProcessingEnvironment()).getFunSpec())
            .addFunction(ModelExtensionFindOneOR(getElementOrName(), getProcessingEnvironment()).getFunSpec())
            .addFunction(ModelExtensionFindAND(getElementOrName(), getProcessingEnvironment()).getFunSpec())
            .addFunction(ModelExtensionFindOR(getElementOrName(), getProcessingEnvironment()).getFunSpec())
    }

    override fun addType() {
        val dao = ClassName("com.j256.ormlite.dao", "Dao")
        getFile()
            .addProperty(
                PropertySpec.builder(
                    "connect",
                    dao.parameterizedBy(getElementOrName().asType().asTypeName(), String::class.asTypeName())
                        .copy(nullable = true)
                )
                    .initializer("%S", null)
                    .mutable()
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
    }
}