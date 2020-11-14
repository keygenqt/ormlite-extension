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
import com.keygenqt.ormlite.files.OrmliteBase
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class ModelExtensionDao(private val el: Element, private val env: ProcessingEnvironment) : GenFunSpec {
    override fun getName(): String {
        return this.javaClass.simpleName.replace("ModelExtension", "").decapitalize()
    }

    override fun getElement(): Element? {
        return el
    }

    override fun getProcessingEnvironment(): ProcessingEnvironment? {
        return env
    }

    override fun getFunSpec(): FunSpec {
        val dao = ClassName("com.j256.ormlite.dao", "Dao")
        getElement()?.let {
            return FunSpec.builder(getName())
                .receiver(
                    ClassName(
                        getProcessingEnvironment()!!.elementUtils.getPackageOf(it).toString(),
                        it.simpleName.toString() + ".Companion"
                    )
                )
                .returns(dao.parameterizedBy(it.asType().asTypeName(), String::class.asTypeName()))
                .addStatement(
                    StringBuilder(
                        """    connect?.let {
    return it
} ?: run {
    connect = DaoManager.createDao(${OrmliteBase::class.java.simpleName}.getConnection(), ${it.simpleName}::class.java)
    return connect!!
}"""
                    ).toString()
                )
                .build()
        } ?: run {
            return FunSpec.builder(getName()).build()
        }
    }
}