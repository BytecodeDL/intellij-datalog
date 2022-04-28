package com.lfrobeen.datalog.ide.hints

import com.intellij.lang.parameterInfo.*
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.lfrobeen.datalog.DatalogReference
import com.lfrobeen.datalog.lang.psi.*

class DatalogParameterInfoHandler : ParameterInfoHandler<PsiElement, DatalogRelDecl> {
    override fun showParameterInfo(element: PsiElement, context: CreateParameterInfoContext) {
        context.showHint(element, element.textRange.startOffset, this)
    }

    override fun updateParameterInfo(parameterOwner: PsiElement, context: UpdateParameterInfoContext) {

    }

    override fun updateUI(p: DatalogRelDecl?, context: ParameterInfoUIContext) {
        if (p == null)
            return

        val sb = StringBuilder()
        var hlStart = -1
        var hlEnd = -1
        val names = p.typedIdentifierList.map { it.variable.identifier.text }
        val types = p.typedIdentifierList.map { it.type.text }

        for (indexed in names.zip(types).withIndex()) {
            val i = indexed.index
            val (name, type) = indexed.value

            if (sb.isNotEmpty()) {
                sb.append(", ")
            }

            if (i == context.currentParameterIndex)
                hlStart = sb.length

            sb.append(name)
                .append(": ")
                .append(type)

            if (i == context.currentParameterIndex)
                hlEnd = sb.length
        }
        val isDisabled = context.currentParameterIndex >= names.size

        context.setupUIComponentPresentation(
            sb.toString(), hlStart, hlEnd, isDisabled, false, false, context.defaultParameterColor
        )
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): PsiElement? {
        var offsetToLeft = context.offset
        var element:PsiElement? = null
        var index = 0
        var rightParenthesis:PsiElement? = findRightParenthesis(context)

        while (offsetToLeft > 0){
            element = context.file.findElementAt(offsetToLeft)
            if (element?.text == ","){
                index++
            }
            if (element?.text == "("){
                break
            }
            offsetToLeft--
        }

        context.setCurrentParameter(index)

        return rightParenthesis
    }

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): PsiElement? {
        var offsetToLeft = context.offset
        var element:PsiElement? = null
        val rightParenthesis = findRightParenthesis(context)

        while (offsetToLeft > 0){
            element = context.file.findElementAt(offsetToLeft)
            if (element?.parent?.reference is DatalogReference){
                break
            }
            offsetToLeft--
        }
        val decl = element?.parent?.reference?.resolve()

        context.itemsToShow = arrayOf(decl)
        return rightParenthesis
    }

    private fun findRightParenthesis(context: ParameterInfoContext): PsiElement? {
        var offsetToRight = context.offset
        var rightParenthesis:PsiElement? = null

        while (offsetToRight < context.file.textLength){
            var element = context.file.findElementAt(offsetToRight)
            if (element?.text == ")"){
                rightParenthesis = element
                break
            }
            offsetToRight++
        }
        return rightParenthesis
    }
}
