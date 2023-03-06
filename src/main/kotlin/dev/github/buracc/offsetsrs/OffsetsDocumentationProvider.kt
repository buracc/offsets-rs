package dev.github.buracc.offsetsrs

import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.text.HtmlBuilder
import com.intellij.openapi.util.text.HtmlChunk
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parents
import org.rust.lang.core.psi.RsNamedFieldDecl
import org.rust.lang.core.psi.RsStructItem

class OffsetsDocumentationProvider : DocumentationProvider {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val fieldElement = element.parent as RsNamedFieldDecl? ?: return null
        val structElement = getStructOfField(fieldElement) ?: return null
        val struct = StructRepository.get(structElement.name ?: return null)
        val field = struct.fields[fieldElement.name] ?: return null
        return HtmlBuilder()
            .append(HtmlChunk.text("Struct: ${struct.name}").bold())
            .br()
            .append(HtmlChunk.text("Field: ${field.name}").bold())
            .br()
            .append("Offset: 0x${field.offset.toString(16).uppercase()}")
            .br()
            .append("Size: 0x${field.size.toString(16).uppercase()} (${field.size} bytes)")
            .toString()
    }

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        val field = contextElement?.parent
        val struct = getStructOfField(field)
        if (struct != null) {
            return contextElement
        }

        return null
    }

    private fun getStructOfField(field: PsiElement?): RsStructItem? {
        if (field !is RsNamedFieldDecl) {
            return null
        }

        return field.parents(true).firstOrNull { it is RsStructItem } as RsStructItem?
    }
}