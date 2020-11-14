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
import com.keygenqt.ormlite.files.*
import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.element.*

class OrmliteBaseOnCreate : GenFunSpec {
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
        return FunSpec.builder(getName())
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("database", ClassName("android.database.sqlite", "SQLiteDatabase").copy(nullable = true))
            .addParameter("connectionSource", ClassName("com.j256.ormlite.support", "ConnectionSource").copy(nullable = true))
            .addStatement(
                StringBuilder(
                    """
try {
    ${OrmliteConf::class.java.simpleName}::class.java.declaredFields.forEach { field ->
        field.isAccessible = true
        TableUtils.createTableIfNotExists(connectionSource, field.type)
    }
} catch (e: Exception) {
    e.printStackTrace()
}"""
                ).toString()
            )
            .build()
    }
}