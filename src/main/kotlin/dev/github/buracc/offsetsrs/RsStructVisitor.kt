package dev.github.buracc.offsetsrs

import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import org.rust.lang.core.psi.RsStructItem
import org.rust.lang.core.psi.RsVisitor
import org.rust.lang.core.psi.ext.fields
import org.rust.lang.core.types.rawType
import org.rust.lang.core.types.ty.*

@Suppress("UnstableApiUsage")
class RsStructVisitor(
    private val factory: PresentationFactory,
    private val sink: InlayHintsSink
) : RsVisitor() {
    companion object {
        private val structSizes = mutableMapOf<String, Long>()
    }

    override fun visitStructItem(structItem: RsStructItem) {
        val structSize = structItem.fields
            .mapNotNull { it.typeReference?.rawType }
            .sumOf { calculateSize(it) }

        val structName = structItem.name ?: return
        structSizes[structName] = structSize

        val struct = StructRepository.get(structName)

        var currOffset = 0L
        structItem.fields.forEach { field ->
            val rawType = field.typeReference?.rawType ?: return@forEach
            val fieldSize = calculateSize(rawType)
            val fieldName = field.name ?: return@forEach

            struct.updateField(
                fieldName,
                rawType,
                fieldSize,
                currOffset
            )

            var text = "0x${currOffset.toString(16).uppercase()}"

            if (fieldSize == -1L) {
                text += " - unknown type $rawType, open file containing this type, and reopen this file."
            }

            currOffset += fieldSize

            sink.addInlineElement(
                field.textOffset,
                false,
                factory.roundWithBackground(factory.smallText(text)),
                true
            )
        }
    }

    private fun calculateSize(rawType: Ty): Long {
        return when (rawType) {
            is TyBool -> 1

            is TyInteger.I8 -> 1
            is TyInteger.I16 -> 2
            is TyInteger.I32 -> 4
            is TyInteger.I64 -> 8

            is TyInteger.U8 -> 1
            is TyInteger.U16 -> 2
            is TyInteger.U32 -> 4
            is TyInteger.U64 -> 8

            is TyFloat.F32 -> 4
            is TyFloat.F64 -> 8

            is TyPointer -> 8

            is TyArray -> {
                rawType.size?.times(calculateSize(rawType.base)) ?: -1
            }

            else -> structSizes[rawType.toString()] ?: -1
        }
    }
}