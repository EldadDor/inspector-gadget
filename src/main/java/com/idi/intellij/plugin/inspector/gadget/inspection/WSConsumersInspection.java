package com.idi.intellij.plugin.inspector.gadget.inspection;

import com.idi.intellij.plugin.inspector.gadget.model.InsepctorSettingsConfiguration;
import com.idi.intellij.plugin.inspector.gadget.model.InspectorSettings;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.BaseLocalInspectionTool;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.siyeh.ig.BaseInspectionVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created by EAD-MASTER on 9/20/2014.
 */
public class WSConsumersInspection extends BaseLocalInspectionTool {
	private static final Logger log = Logger.getInstance(WSConsumersInspection.class);
/*
	@Nullable
	@Override
	public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
		return super.checkMethod(method, manager, isOnTheFly);
	}*/

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		return new WSConsumerVisitor(holder, holder.getProject());
	}


	private static class WSConsumerVisitor extends BaseInspectionVisitor {
		private final ProblemsHolder problemsHolder;
		private final Project project;

		private WSConsumerVisitor(ProblemsHolder problemsHolder, Project project) {
			this.problemsHolder = problemsHolder;
			this.project = project;
		}

		@Override
		public void visitMethod(PsiMethod method) {
			final InsepctorSettingsConfiguration settingsConfiguration = InsepctorSettingsConfiguration.getInstance(project);
			final Map<String, String> annotationsMap = settingsConfiguration.getState().ANNOTATIONS;
		}

		@Override
		public void visitClass(PsiClass aClass) {
			super.visitClass(aClass);
			final InspectorSettings state = InsepctorSettingsConfiguration.getInstance(project).getState();
			final Map<String, String> ws_consumer;
			if (state != null) {
				ws_consumer = state.WS_CONSUMER;
				final PsiAnnotation annotation = aClass.getModifierList().findAnnotation(ws_consumer.get("@WSConsumer"));
				final PsiAnnotationMemberValue url = annotation.findAttributeValue("url");
				if (url != null) {
					if (((PsiLiteralExpressionImpl) url).getInnerText() != null && !((PsiLiteralExpressionImpl) url).getInnerText().isEmpty()) {
						if (((PsiLiteralExpressionImpl) url).getInnerText().split("\\?").length > 0) {

						}
						final String[] split = ((PsiLiteralExpressionImpl) url).getInnerText().split("@");
						if (split.length > 0) {
							for (int i = 1; i < split.length; i++) {
								final String cleanParam = split[i].replace("/", "");
							}
						}
					}
				}
				for (String key : ws_consumer.keySet()) {
					log.info("Key=" + key);
				}
			}
		}
	}


	private boolean isImplementing(PsiClass aClass, String fullyQualifiedName) {
		for (PsiClass psiInterface : aClass.getInterfaces()) {
			if (fullyQualifiedName.equalsIgnoreCase(psiInterface.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}
}
