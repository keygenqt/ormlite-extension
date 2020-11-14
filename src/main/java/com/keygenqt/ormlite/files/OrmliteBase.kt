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
import com.squareup.kotlinpoet.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class OrmliteBase(
    private val processingEnv: ProcessingEnvironment,
    private val elements: List<Element>
) : GenFileSpec {
    override fun getElementOrName(): String {
        return this.javaClass.simpleName
    }

    override fun getProcessingEnvironment(): ProcessingEnvironment {
        return processingEnv
    }

    override fun addImport() {
        getFile()
            .addImport("com.j256.ormlite.dao", "Dao")
            .addImport("com.j256.ormlite.dao", "DaoManager")
            .addImport("com.j256.ormlite.table", "TableUtils")
        for (el in elements) {
            getFile().addImport(
                getProcessingEnvironment().elementUtils.getPackageOf(el).toString(),
                el.simpleName.toString()
            )
        }
    }

    override fun addExtension() {

    }

    override fun addType() {
        getFile()
            .addType(
                TypeSpec.classBuilder(getElementOrName())
                    .addInitializerBlock(
                        CodeBlock.builder()
                            .addStatement(StringBuffer("connection = getConnectionSource()").toString())
                            .build()
                    )
                    .addModifiers(KModifier.OPEN)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("context", ClassName("android.content", "Context"))
                            .addParameter("dbName", String::class)
                            .addParameter("dbVersion", Int::class)
                            .addParameter(
                                ParameterSpec.builder(
                                    "cursorFactory",
                                    ClassName(
                                        "android.database.sqlite.SQLiteDatabase",
                                        "CursorFactory"
                                    ).copy(nullable = true)
                                )
                                    .defaultValue("null")
                                    .build()
                            )
                            .build()
                    )
                    .superclass(
                        ClassName("com.j256.ormlite.android.apptools", "OrmLiteSqliteOpenHelper")
                    )
                    .addSuperclassConstructorParameter("context")
                    .addSuperclassConstructorParameter("dbName")
                    .addSuperclassConstructorParameter("cursorFactory")
                    .addSuperclassConstructorParameter("dbVersion")
                    .addFunction(OrmliteBaseOnUpgrade().getFunSpec())
                    .addFunction(OrmliteBaseOnCreate().getFunSpec())
                    .addFunction(OrmliteBaseClose().getFunSpec())
                    .addType(
                        TypeSpec.companionObjectBuilder()
                            .addProperty(
                                PropertySpec.builder(
                                    "connection",
                                    ClassName("com.j256.ormlite.support", "ConnectionSource")
                                )
                                    .mutable()
                                    .addModifiers(KModifier.LATEINIT, KModifier.PRIVATE)
                                    .build()
                            )
                            .addFunction(OrmliteBaseClearDb(elements).getFunSpec())
                            .addFunction(OrmliteBaseGetConnection().getFunSpec())
                            .build()
                    ).build()
            )
    }

}