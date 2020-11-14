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
import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*

class ModelExtensionClearTable(private val el: Element, private val evn: ProcessingEnvironment) : GenFunSpec {
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
        getElement()?.let {
            return FunSpec.builder(getName())
                .receiver(
                    ClassName(
                        getProcessingEnvironment()!!.elementUtils.getPackageOf(it).toString(),
                        it.simpleName.toString() + ".Companion"
                    )
                )
                .addStatement(
                    StringBuilder(
                        """    try {
    TableUtils.clearTable(${OrmliteBase::class.java.simpleName}.getConnection(), ${it.simpleName}::class.java)
} catch (ex: SQLException) {
    Log.e("TAG", ex.message!!)
}"""
                    ).toString()
                ).build()
        } ?: run {
            return FunSpec.builder(getName()).build()
        }
    }
}