package dev.github.buracc.offsetsrs.model

import org.rust.lang.core.types.ty.Ty

class Struct(
    val name: String,
    val fields: MutableMap<String, Field> = mutableMapOf()
) {
    fun updateField(name: String, type: Ty, size: Long, offset: Long): Boolean {
        var updated = false
        val field = fields.getOrPut(name) {
            updated = true
            Field(name, type, size, offset)
        }

        if (field.type != type) {
            updated = true
            field.type = type
        }

        if (field.size != size) {
            updated = true
            field.size = size
        }

        if (field.offset != offset) {
            updated = true
            field.offset = offset
        }

        return updated
    }
}