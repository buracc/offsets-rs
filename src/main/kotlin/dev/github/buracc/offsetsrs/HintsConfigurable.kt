package dev.github.buracc.offsetsrs

import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.ImmediateConfigurable
import javax.swing.JPanel

@Suppress("UnstableApiUsage")
class HintsConfigurable : ImmediateConfigurable {
    override fun createComponent(listener: ChangeListener) = JPanel()
}