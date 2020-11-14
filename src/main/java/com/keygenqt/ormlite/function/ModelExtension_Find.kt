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
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element


class ModelExtensionFind(private val el: Element, private val evn: ProcessingEnvironment) : GenFunSpec {
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
            return FunSpec.builder(getName())
                .receiver(
                    ClassName(
                        getProcessingEnvironment()!!.elementUtils.getPackageOf(element).toString(),
                        element.simpleName.toString() + ".Companion"
                    )
                )
                .returns(element.asType().asTypeName().copy(nullable = true))
                .addParameter("key", String::class.asTypeName())
                .addParameter("value", String::class.asTypeName())
                .addStatement(
                    StringBuilder(
                        """    return try {
    val q = ${element.simpleName}.dao().queryBuilder().where()
    q.eq(key, value)
    val model = ${element.simpleName}.dao().queryForFirst(q.prepare())
    ${element.enclosedElements.getRefresh()}
    return model
} catch (ex: NoSuchElementException) {
    null
}"""
                    ).toString()
                ).build()
        } ?: run {
            return FunSpec.builder(getName()).build()
        }
    }
}