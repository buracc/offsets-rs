package dev.github.burak.rsoffsets

import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.presentation.PresentationFactory
import org.rust.lang.core.psi.RsStructItem
import org.rust.lang.core.psi.RsVisitor
import org.rust.lang.core.psi.ext.fields
import org.rust.lang.core.types.rawType
import org.rust.lang.core.types.ty.*
import java.lang.IllegalStateException

@Suppress("UnstableApiUsage")
class RsStructVisitor(
    private val factory: PresentationFactory,
    private val sink: InlayHintsSink
) : RsVisitor() {
    companion object {
        private val structSizes = mutableMapOf<String, Long>()
    }

    override fun visitStructItem(struct: RsStructItem) {
        var size = 0L
        for (field in struct.fields) {
            val rawType = field.typeReference?.rawType ?: continue
            try {
                size += calculateBaseSize(rawType)
            } catch (e: IllegalStateException) {
                break
            }
        }

        structSizes[struct.name ?: return] = size

        var currOffset = 0L
        for (field in struct.fields) {
            val text = "0x${currOffset.toString(16).uppercase()}"
            val rawType = field.typeReference?.rawType ?: continue

            try {
                currOffset += calculateSize(rawType)
            } catch (e: IllegalStateException) {
                break
            }

            sink.addInlineElement(
                field.textOffset,
                false,
                factory.roundWithBackground(factory.smallText(text)),
                true
            )
        }
    }

    private fun calculateBaseSize(rawType: Ty): Long {
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

            is TyPointer -> 8

            is TyArray -> {
                rawType.size?.times(calculateBaseSize(rawType.base)) ?: error("Invalid array size")
            }

            else -> error("Invalid base type $rawType ${rawType::class}")
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

            is TyPointer -> 8

            is TyArray -> {
                rawType.size?.times(calculateSize(rawType.base)) ?: error("Invalid array size")
            }

            else -> structSizes[rawType.toString()] ?: error("Invalid type $rawType ${rawType::class} ${structSizes}")
        }
    }
}