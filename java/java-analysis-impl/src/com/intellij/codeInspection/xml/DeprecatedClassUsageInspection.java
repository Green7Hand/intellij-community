// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.codeInspection.xml;

import com.intellij.codeInspection.*;
import com.intellij.codeInspection.deprecation.DeprecationInspectionBase;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dmitry Avdeev
 */
public class DeprecatedClassUsageInspection extends XmlSuppressableInspectionTool {

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder,
                                        boolean isOnTheFly,
                                        @NotNull LocalInspectionToolSession session) {
    return new XmlElementVisitor() {
      @Override
      public void visitXmlTag(XmlTag tag) {
        if (tag.getValue().getTextElements().length > 0) {
          checkReferences(tag, holder);
        }
      }

      @Override
      public void visitXmlAttributeValue(XmlAttributeValue value) {
        checkReferences(value, holder);
      }
    };
  }

  private static void checkReferences(PsiElement psiElement, ProblemsHolder holder) {
    PsiReference[] references = psiElement.getReferences();
    PsiReference last = ArrayUtil.getLastElement(references);
    if (last != null && (!(last instanceof ResolvingHint) || ((ResolvingHint)last).canResolveTo(PsiDocCommentOwner.class))) {
      PsiElement resolved = last.resolve();
      if (resolved instanceof PsiModifierListOwner) {
        DeprecationInspectionBase.checkDeprecated((PsiModifierListOwner)resolved, psiElement, last.getRangeInElement(), false, false, true, false,
                                                  holder, false, ProblemHighlightType.LIKE_DEPRECATED);
      }
    }
  }

  @Override
  public boolean isEnabledByDefault() {
    return true;
  }

  @Nls
  @NotNull
  @Override
  public String getGroupDisplayName() {
    return InspectionsBundle.message("deprecated.class.usage.group.xml");
  }

  @NotNull
  @Override
  public String getShortName() {
    return "DeprecatedClassUsageInspection";
  }
}
