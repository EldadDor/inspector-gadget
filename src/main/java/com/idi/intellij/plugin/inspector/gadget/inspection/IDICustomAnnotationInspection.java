/*
 * User: eldad.Dor
 * Date: 17/08/2014 15:34
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.inspector.gadget.inspection;

import com.idi.intellij.plugin.inspector.gadget.model.InsepctorSettingsConfiguration;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightMessageUtil;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.siyeh.ig.BaseInspectionVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.idi.intellij.plugin.inspector.gadget.common.InspectorGadgetMessages.message;

/**
 * @author eldad
 * @date 17/08/2014
 */
public class IDICustomAnnotationInspection extends BaseLocalInspectionTool {
	private static final Logger log = Logger.getInstance(IDICustomAnnotationInspection.class);

	@Nullable
	@Override
	public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
		return super.checkMethod(method, manager, isOnTheFly);
	}

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		return new AnnotationCustomVisitor(holder);
	}

	private static class AnnotationCustomVisitor extends BaseInspectionVisitor {
		private final ProblemsHolder problemsHolder;
		private final Project project;

		private AnnotationCustomVisitor(final ProblemsHolder holder) {
			problemsHolder = holder;
			project = problemsHolder.getProject();
		}

		@Override
		public void visitMethod(PsiMethod method) {
			final InsepctorSettingsConfiguration settingsConfiguration = InsepctorSettingsConfiguration.getInstance(project);
			final Map<String, String> annotationsMap = settingsConfiguration.getState().ANNOTATIONS;
			PsiModifierList list = method.getModifierList();
			for (final String key : annotationsMap.keySet()) {
				String annotation = annotationsMap.get(key) + "." + key;
				final PsiAnnotation psiAnnotation = list.findAnnotation(annotation);
				if (psiAnnotation != null) {
					if (log.isDebugEnabled()) {
						log.debug("visitMethod(): psiAnnotation=" + psiAnnotation.getQualifiedName() + " method=" + method.getName());
					}
					MethodSignatureBackedByPsiMethod methodSignature = MethodSignatureBackedByPsiMethod.create(method, PsiSubstitutor.EMPTY);

					PsiElement methodName = method.getNameIdentifier();
					final PsiClass aClass = method.getContainingClass();
					String description = message("annotationMarker.refactoring", HighlightMessageUtil.getSymbolName(aClass, PsiSubstitutor.EMPTY));
					log.info("visitMethod(): registerProblem()=" + methodName);
					problemsHolder.registerProblem(methodName, description, ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
				}
			}
		}
	}

}