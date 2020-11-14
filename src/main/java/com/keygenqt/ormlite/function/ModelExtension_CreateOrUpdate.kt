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
import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*

class ModelExtensionCreateOrUpdate(
    private val el: Element,
    private val elements: List<Element>
) : GenFunSpec {
    override fun getName(): String {
        return this.javaClass.simpleName.replace("ModelExtension", "").decapitalize()
    }

    override fun getElement(): Element? {
        return el
    }

    override fun getProcessingEnvironment(): ProcessingEnvironment? {
        return null
    }

    override fun getFunSpec(): FunSpec {
        getElement()?.let { element ->

            val foreignOneOne = StringBuilder("")
            for (enclosedElement in element.enclosedElements) {
                if (enclosedElement.kind == ElementKind.FIELD) {
                    enclosedElement.getAnnotation(DatabaseField::class.java)?.let { annotation ->
                        if (annotation.foreign) {
                            foreignOneOne.append("\n")
                            foreignOneOne.append("""${enclosedElement.simpleName}?.let {
        ${enclosedElement.asType()}.dao().createOrUpdate(it)
    }""")
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

                        var valueName = ""
                        for (el in elements) {
                            if (type == el.asType().toString()) {
                                for (enclosedEl in el.enclosedElements) {
                                    if (enclosedElement.kind == ElementKind.FIELD) {
                                        if (enclosedEl.asType() == element.asType()) {
                                            valueName = enclosedEl.simpleName.toString()
                                        }
                                    }
                                }
                            }
                        }

                        if (valueName.isEmpty()) {
                            throw RuntimeException(
                                "Error foreign connect. Foreign Collections: https://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_2.html#Foreign-Collection")
                        }

                        foreignOneMany.append("\n")
                        foreignOneMany.append("""${enclosedElement.simpleName}?.forEach {
      it.$valueName = this
      $type.dao().createOrUpdate(it)
    }""")
                    }
                }
            }

            return FunSpec.builder(getName())
                .receiver(element.asType().asTypeName())
                .returns(element.asType().asTypeName())
                .addStatement(
                    StringBuilder(
                        """    try {
    $foreignOneOne
    ${element.simpleName}.dao().createOrUpdate(this)
    $foreignOneMany
} catch (ex: SQLException) {
    println(ex.message)
}
return this"""
                    ).toString()
                )
                .build()
        } ?: run {
            return FunSpec.builder(getName()).build()
        }
    }
}