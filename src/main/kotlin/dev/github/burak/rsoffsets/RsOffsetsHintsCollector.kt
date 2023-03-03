package dev.github.burak.rsoffsets

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import org.rust.lang.core.psi.*
import org.rust.lang.core.psi.ext.fields
import org.rust.lang.core.types.rawType
import org.rust.lang.core.types.ty.*

@Suppress("UnstableApiUsage")
class RsOffsetsHintsCollector(editor: Editor) : FactoryInlayHintsCollector(editor) {
    override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
        element.accept(object : RsVisitor() {
            override fun visitStructItem(struct: RsStructItem) {
                var currOffset = 0L
                struct.fields.forEach { field ->
                    val text = "0x${currOffset.toString(16).uppercase()}"

                    val typeReference = field.typeReference
                    val rawType = typeReference?.rawType ?: return@forEach
                    currOffset += calculateSize(rawType)

                    sink.addInlineElement(
                        field.textOffset,
                        false,
                        factory.roundWithBackground(factory.smallText(text)),
                        true
                    )
                }
            }
        })

        return true
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

            else -> 16 // XXStruct classes have 2 ptrs so size 16 for now, todo: calculate size of custom structs
        }
    }
}