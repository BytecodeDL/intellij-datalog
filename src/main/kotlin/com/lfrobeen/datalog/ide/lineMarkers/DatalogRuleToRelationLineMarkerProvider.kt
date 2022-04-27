package com.lfrobeen.datalog.ide.lineMarkers

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.lfrobeen.datalog.lang.psi.DatalogClause
import com.lfrobeen.datalog.lang.psi.impl.DatalogRelDeclImpl

class DatalogRuleToRelationLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element !is DatalogClause)
            return

        val atoms = element.clauseHead.atomList
        atoms.forEach {
            val relationRef = it.anyReference.reference
            val relation = relationRef?.resolve() as? DatalogRelDeclImpl ?: return

            val builder = NavigationGutterIconBuilder
                .create(AllIcons.Gutter.ImplementingMethod)
                .setTargets(listOf(relation))
                .setTooltipText("Rule for relation ${relation.name}")

            result.add(builder.createLineMarkerInfo(element))
        }

    }
}
