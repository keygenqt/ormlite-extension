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

package com.keygenqt.ormlite.function

import com.j256.ormlite.field.*
import com.keygenqt.ormlite.base.GenFunSpec
import com.keygenqt.ormlite.files.OrmliteBase
import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*

class ModelExtensionClearTableWithForeign(private val el: Element,
    private val evn: ProcessingEnvironment) : GenFunSpec {
    override fun getName(): String {
        return this.javaClass.simpleName.replace("ModelExtension", "").decapitalize()
    }

    override fun getElement(): Element? {
        return el
    }

    override fun getProcessingEnvironment(): ProcessingEnvironment? {
        return evn
    }

    override fun getFunSpec(): FunSpec {
        getElement()?.let { element ->

            val foreignOneOne = StringBuilder("")
            for (enclosedElement in element.enclosedElements) {
                if (enclosedElement.kind == ElementKind.FIELD) {
                    enclosedElement.getAnnotation(DatabaseField::class.java)?.let { annotation ->
                        if (annotation.foreign) {
                            foreignOneOne.append("\n")
                            foreignOneOne.append(
                                """TableUtils.clearTable(${OrmliteBase::class.java.simpleName}.getConnection(), ${enclosedElement.asType()}::class.java)""")
                        }
                    }
                }
            }

            val foreignOneMany = StringBuilder("")
            for (enclosedElement in element.enclosedElements) {
                if (enclosedElement.kind == ElementKind.FIELD) {
                    enclosedElement.getAnnotation(ForeignCollectionField::class.java)?.let {
                        val type = enclosedElement.asType().toString()
                            .replace("java.util.Collection<", "")
                            .replace(">", "")
                        foreignOneMany.append("\n")
                        foreignOneMany.append(
                            """TableUtils.clearTable(${OrmliteBase::class.java.simpleName}.getConnection(), $type::class.java)""")
                    }
                }
            }

            return FunSpec.builder(getName())
                .receiver(
                    ClassName(
                        getProcessingEnvironment()!!.elementUtils.getPackageOf(element).toString(),
                        element.simpleName.toString() + ".Companion"
                    )
                )
                .addStatement(
                    StringBuilder(
                        """    try {
    $foreignOneOne
    $foreignOneMany
    TableUtils.clearTable(${OrmliteBase::class.java.simpleName}.getConnection(), ${element.simpleName}::class.java)
} catch (ex: SQLException) {
    println(ex.message)
}"""
                    ).toString()
                ).build()
        } ?: run {
            return FunSpec.builder(getName()).build()
        }
    }
}