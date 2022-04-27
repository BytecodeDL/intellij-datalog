package com.lfrobeen.datalog.lang.completion

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.lfrobeen.datalog.lang.psi.DatalogDeclarationComment
import com.lfrobeen.datalog.lang.psi.DatalogDeclarationMixin
import com.lfrobeen.datalog.lang.psi.DatalogTypes
import com.lfrobeen.datalog.lang.psi.elementType
import com.lfrobeen.datalog.lang.psi.impl.DatalogRelDeclImpl

class DatalogCompletionScopeProcessor(private val result: CompletionResultSet) : PsiScopeProcessor {

    override fun execute(decl: PsiElement, state: ResolveState): Boolean {
        if (decl is DatalogDeclarationMixin) {
            result.addElement(
                LookupElementBuilder.create(decl.name)
                    .withPsiElement(decl)
                    .withBoldness(decl.elementType == DatalogTypes.REL_DECL)
                    .withIcon(decl.presentation?.getIcon(false))
                    .withTypeText(decl.elementType.toString())
            )

            if (decl is DatalogRelDeclImpl){
                val variables = decl.typedIdentifierList.joinToString(separator = ", ") { it.variable.text }
                val keyword = decl.name + "(" + variables + ")"
                result.addElement(
                    LookupElementBuilder.create(keyword)
                        .withPsiElement(decl)
                        .withBoldness(decl.elementType == DatalogTypes.REL_DECL)
                        .withIcon(decl.presentation?.getIcon(false))
                        .withTypeText(decl.elementType.toString())
                )
            }

        }

        if (decl is DatalogDeclarationComment && !decl.name.isNullOrEmpty()) {
            result.addElement(
                LookupElementBuilder.create(decl.name!!)
                    .withPsiElement(decl)
                    .withBoldness(decl.elementType == DatalogTypes.REL_DECL)
                    .withIcon(decl.presentation?.getIcon(false))
                    .withTypeText(decl.elementType.toString())
            )
        }

        return true
    }

    override fun <T : Any?> getHint(hintKey: Key<T>): T? {
        return null
    }

    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {
    }
}
