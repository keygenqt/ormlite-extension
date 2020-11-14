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

import com.keygenqt.ormlite.base.*
import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*

class OrmliteBaseClearDb(private val elements: List<Element>) : GenFunSpec {
    override fun getName(): String {
        return this.javaClass.simpleName.replace("OrmliteBase", "").decapitalize()
    }

    override fun getElement(): Element? {
        return null
    }

    override fun getProcessingEnvironment(): ProcessingEnvironment? {
        return null
    }

    override fun getFunSpec(): FunSpec {
        val f = FunSpec.builder(getName())
        for (el in elements) {
            f.addStatement("${el.simpleName}.clearTable()")
        }
        return f.build()
    }
}