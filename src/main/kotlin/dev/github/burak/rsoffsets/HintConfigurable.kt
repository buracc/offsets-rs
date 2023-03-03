package dev.github.burak.rsoffsets

import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.ImmediateConfigurable
import javax.swing.JComponent
import javax.swing.JPanel

@Suppress("UnstableApiUsage")
class HintConfigurable : ImmediateConfigurable {
    override fun createComponent(changeListener: ChangeListener): JComponent {
        return JPanel()
    }
}