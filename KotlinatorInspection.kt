// (c) Facebook, Inc. and its affiliates. Confidential and proprietary.

package com.facebook.tools.intellij.kotlinator

import com.google.common.collect.Lists
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiPackageStatement
import java.util.HashSet
import org.jetbrains.kotlin.j2k.ConverterSettings.Companion.defaultSettings
import org.jetbrains.kotlin.nj2k.IdeaNewJavaToKotlinServices.oldServices
import org.jetbrains.kotlin.nj2k.NewJavaToKotlinConverter

class KotlinatorInspection : LocalInspectionTool() {

  override fun buildVisitor(
      holder: ProblemsHolder,
      isOnTheFly: Boolean,
      session: LocalInspectionToolSession
  ): PsiElementVisitor {
    return Visitor(holder)
  }

  override fun getStaticDescription(): String {
    return "[Experimental] A dummy inspection which calls the kotlin conversion library (nj2k)"
  }

  class Visitor(private val holder: ProblemsHolder) : PsiElementVisitor() {

    private val visitedFilenames: MutableSet<String> = HashSet()

    override fun visitElement(element: PsiElement) {
      if (element is PsiPackageStatement) {
        val project = element.getProject()
        val psiFile = element.getContainingFile()
        val psiJavaFile = psiFile as PsiJavaFile
        if (!visitedFilenames.contains(psiFile.getName())) {
          visitedFilenames.add(psiFile.getName())
          val module = ModuleUtilCore.findModuleForFile(psiJavaFile)
          if (module == null || module.isDisposed) {
            holder.registerProblem(
                element, "No valid module found for ${psiFile.name}", HIGHLIGHT_TYPE)
            super.visitElement(element)
            return
          }
          val newJavaToKotlinConverter =
              NewJavaToKotlinConverter(project, module, defaultSettings, oldServices)
          val javaFileList: List<PsiJavaFile> = Lists.newArrayList(psiJavaFile)
          val (results) = newJavaToKotlinConverter.elementsToKotlin(javaFileList)
          val (convertedText) =
              results[0]
                  ?: return holder.registerProblem(
                      element,
                      "No results found after converting ${psiJavaFile.name}",
                      HIGHLIGHT_TYPE)
          holder.registerProblem(element, convertedText, HIGHLIGHT_TYPE)
        }
      }
      super.visitElement(element)
    }

    companion object {
      private val HIGHLIGHT_TYPE = ProblemHighlightType.INFORMATION
    }
  }
}
