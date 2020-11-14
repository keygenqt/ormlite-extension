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

import com.keygenqt.ormlite.base.GenFunSpec
import com.keygenqt.ormlite.utils.getRefresh
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class ModelExtensionFindAND(private val el: Element, private val evn: ProcessingEnvironment) : GenFunSpec {
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
            val hash = ClassName("kotlin.collections", "HashMap")
            return FunSpec.builder(this.javaClass.simpleName.replace("ModelExtension", "").decapitalize())
                .receiver(
                    ClassName(
                        getProcessingEnvironment()!!.elementUtils.getPackageOf(element).toString(),
                        element.simpleName.toString() + ".Companion"
                    )
                )
                .returns(ClassName("kotlin.collections", "List").parameterizedBy(element.asType().asTypeName()))
                .addParameter("args", hash.parameterizedBy(String::class.asTypeName(), String::class.asTypeName()))
                .addStatement(
                    StringBuilder(
                        """    return try {
    val q = ${element.simpleName}.dao().queryBuilder().where()
    var index = 0
    for ((key, value) in args) {
        if (index > 0) {
            q.and()
        }
        q.eq(key, value)
        index++
    }
    val models = ${element.simpleName}.dao().query(q.prepare())
    for (model in models) {
      ${element.enclosedElements.getRefresh()}
    }
    return models
} catch (ex: NoSuchElementException) {
    arrayListOf()
}"""
                    ).toString()
                ).build()
        } ?: run {
            return FunSpec.builder(getName()).build()
        }
    }
}