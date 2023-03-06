package dev.github.buracc.offsetsrs.model

import org.rust.lang.core.types.ty.Ty

data class Field(
    val name: String,
    var type: Ty,
    var size: Long,
    var offset: Long
)
