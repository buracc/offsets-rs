package dev.github.buracc.offsetsrs

import dev.github.buracc.offsetsrs.model.Field
import dev.github.buracc.offsetsrs.model.Struct
import java.util.concurrent.ConcurrentHashMap

object StructRepository {
    private val structs = ConcurrentHashMap<String, Struct>()

    fun get(name: String): Struct = structs.getOrPut(name) { Struct(name) }

    fun getField(fieldName: String): Field? = structs.values
        .map { it.fields.values }
        .flatten()
        .firstOrNull { it.name == fieldName }
}