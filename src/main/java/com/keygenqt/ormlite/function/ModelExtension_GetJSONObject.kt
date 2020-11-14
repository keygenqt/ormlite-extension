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

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.keygenqt.ormlite.base.GenFunSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

class ModelExtensionGetJSONObject(
    private val el: Element,
    private val evn: ProcessingEnvironment,
    private val elements: List<Element>
) : GenFunSpec {
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

            // add refresh one to one
            val foreignJSONObject = arrayListOf<String>()
            for (enclosedElement in element.enclosedElements) {
                if (enclosedElement.kind == ElementKind.FIELD) {
                    enclosedElement.getAnnotation(DatabaseField::class.java)?.let { annotation ->
                        if (annotation.foreign) {

                            var foreignCollection = false
                            for (el in elements) {
                                if (enclosedElement.asType() == el.asType()) {
                                    for (enclosedEl in el.enclosedElements) {
                                        if (enclosedElement.kind == ElementKind.FIELD) {
                                            if (enclosedEl.asType()
                                                    .toString() == "java.util.Collection<${element.asType()}>"
                                            ) {
                                                foreignCollection = true
                                                break
                                            }
                                        }
                                    }
                                }
                            }

                            if (!foreignCollection) {
                                val value = """
            if (value is ${enclosedElement.asType()}) {
                json.put(field.name, value.getJSONObject())
            }"""
                                if (!foreignJSONObject.contains(value)) {
                                    foreignJSONObject.add(value)
                                }
                            }
                        }
                    }
                }
            }

            // add refresh one to one
            val foreignJSONArray = arrayListOf<String>()
            for (enclosedElement in element.enclosedElements) {
                if (enclosedElement.kind == ElementKind.FIELD) {
                    enclosedElement.getAnnotation(ForeignCollectionField::class.java)?.let {
                        val type = enclosedElement.asType().toString()
                            .replace("java.util.Collection<", "")
                            .replace(">", "")

                        val value = """
            if (value is Collection<*>) {
                val array = JSONArray()
                value.forEach { item ->
                    if (item is $type) {
                        array.put(item.getJSONObject())
                    }
                }
                json.put(field.name, array)
            }"""
                        if (!foreignJSONArray.contains(value)) {
                            foreignJSONArray.add(value)
                        }
                    }
                }
            }

            return FunSpec.builder(getName())
                .receiver(element.asType().asTypeName())
                .returns(ClassName("org.json", "JSONObject"))
                .addStatement(
                    StringBuilder(
                        """    val json = JSONObject()
this::class.java.declaredFields.forEach { field ->
    field.isAccessible = true
    when (field.get(this)) {
        is String -> json.put(field.name, field.get(this) as String)
        is Float -> json.put(field.name, field.get(this) as Float)
        is Int -> json.put(field.name, field.get(this) as Int)
        else -> {
            ${if (foreignJSONObject.isNotEmpty() || foreignJSONArray.isNotEmpty()) "val value = field.get(this)" else ""}
            ${foreignJSONObject.joinToString("\n")}
            ${foreignJSONArray.joinToString("\n")}
        }
    }
}
return json"""
                    ).toString()
                ).build()
        } ?: run {
            return FunSpec.builder(getName()).build()
        }
    }
}