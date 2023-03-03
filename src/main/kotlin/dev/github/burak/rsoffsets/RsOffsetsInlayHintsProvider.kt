package dev.github.burak.rsoffsets

import com.intellij.codeInsight.hints.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

@Suppress("UnstableApiUsage")
open class RsOffsetsInlayHintsProvider : InlayHintsProvider<Any> {
    companion object {
        private const val NAME = "RsOffsets"
        private val KEY = SettingsKey<Any>(NAME)
    }

    override val key: SettingsKey<Any>
        get() = KEY

    override val name: String
        get() = NAME

    override val previewText: String
        get() = ""

    override fun createSettings() = Any()

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: Any,
        sink: InlayHintsSink
    ) = RsOffsetsHintsCollector(editor)

    override fun createConfigurable(settings: Any) = HintConfigurable()
}