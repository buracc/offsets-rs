package dev.github.burak.rsoffsets

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import org.rust.lang.core.psi.*
import org.rust.lang.core.types.ty.*

@Suppress("UnstableApiUsage")
class RsOffsetsHintsCollector(editor: Editor) : FactoryInlayHintsCollector(editor) {
    override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
        element.accept(RsStructVisitor(factory, sink))

        return true
    }
}