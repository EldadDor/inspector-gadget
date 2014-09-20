package com.idi.intellij.plugin.inspector.gadget.inspection;

import com.google.common.collect.Maps;
import com.idi.intellij.plugin.inspector.gadget.model.InsepctorSettingsConfiguration;
import com.idi.intellij.plugin.inspector.gadget.model.InspectorSettings;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightMessageUtil;
import com.intellij.codeInspection.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.util.MethodSignatureBackedByPsiMethod;
import com.siyeh.ig.BaseInspectionVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.idi.intellij.plugin.inspector.gadget.common.InspectorGadgetMessages.message;

/**
 * Created by EAD-MASTER on 9/20/2014.
 */
//public class WSConsumersInspection extends BaseLocalInspectionTool {
public class WSConsumersInspection extends LocalInspectionTool {
	private static final Logger log = Logger.getInstance(WSConsumersInspection.class);

	@NotNull
	@Override
	public String getShortName() {
		return "WSConsumers";
	}
/*
	@Nullable
	@Override
	public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
		return super.checkMethod(method, manager, isOnTheFly);
	}*/

	@Override
	public void inspectionFinished(@NotNull LocalInspectionToolSession session, @NotNull ProblemsHolder problemsHolder) {
		super.inspectionFinished(session, problemsHolder);
	}

/*	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
		return new WSConsumerVisitor(holder, holder.getProject());
	}*/

	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
		return super.checkFile(file, manager, isOnTheFly);
	}

	@NotNull
	@Override
	public List<ProblemDescriptor> processFile(@NotNull PsiFile file, @NotNull InspectionManager manager) {
		return super.processFile(file, manager);
	}
	/*

	@Nullable
	@Override
	public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
		return super.checkMethod(method, manager, isOnTheFly);
	}
*/

	@NotNull
	@Override
	public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
		return new WSConsumerVisitor(holder, holder.getProject());
	}

	private static class WSConsumerVisitor extends BaseInspectionVisitor {
		private final ProblemsHolder problemsHolder;
		private final Project project;
		private Map<String, String> urlParams = Maps.newHashMap();

		private WSConsumerVisitor(ProblemsHolder problemsHolder, Project project) {
			this.problemsHolder = problemsHolder;
			this.project = project;
		}

		@Override
		public void visitMethod(PsiMethod method) {
			final InspectorSettings state = InsepctorSettingsConfiguration.getInstance(project).getState();
			if (state != null) {
				final PsiAnnotation annotation = method.getModifierList().findAnnotation(state.WS_CONSUMER.get("@Property"));
				if (annotation == null) {
					MethodSignatureBackedByPsiMethod methodSignature = MethodSignatureBackedByPsiMethod.create(method, PsiSubstitutor.EMPTY);
					PsiElement methodName = method.getNameIdentifier();
					final PsiClass aClass = method.getContainingClass();
					String description = message("wsConsumer.property.missing", HighlightMessageUtil.getSymbolName(aClass, PsiSubstitutor.EMPTY));
					log.info("visitMethod(): registerProblem()=" + methodName);
					if (methodName != null) {
//						registerError(method,ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
						problemsHolder.registerProblem(method, description, ProblemHighlightType.GENERIC_ERROR);
					}
				}
			}
		}

		@Override
		public void visitClass(PsiClass aClass) {
			super.visitClass(aClass);
			final InspectorSettings state = InsepctorSettingsConfiguration.getInstance(project).getState();
			final Map<String, String> ws_consumer;
			if (state != null && isImplementing(aClass, state.WS_CONSUMER_CLASS)) {
				ws_consumer = state.WS_CONSUMER;
				final PsiAnnotation annotation = aClass.getModifierList().findAnnotation(ws_consumer.get("@WSConsumer"));
				final PsiAnnotationMemberValue url = annotation.findAttributeValue("url");
				if (url != null && url instanceof PsiLiteralExpression) {
					if (((PsiLiteralExpressionImpl) url).getInnerText() != null && !((PsiLiteralExpressionImpl) url).getInnerText().isEmpty()) {
						final String[] splitQMark = ((PsiLiteralExpressionImpl) url).getInnerText().split("\\?");
						if (splitQMark.length > 1) {
							for (int i = 1; i < splitQMark.length; i++) {
								String aSplitQMark = splitQMark[i];
								final String[] splitAmpersand = aSplitQMark.split("&");
								for (String aSplitAmpersand : splitAmpersand) {
									cleanAndAddParam(aSplitAmpersand.split("@"));
								}
							}
						} else {
							final String[] splitAt = ((PsiLiteralExpressionImpl) url).getInnerText().split("@");
							if (splitAt.length > 1) {
								cleanAndAddParam(splitAt);
							}
						}
						for (String key : ws_consumer.keySet()) {
							log.info("Key=" + key);
						}
					}
				}
			}
		}

		private void cleanAndAddParam(String[] split) {
			for (int i = 1; i < split.length; i++) {
				final String cleanParam = split[i].replace("/", "");
				urlParams.put(cleanParam, cleanParam);
			}
		}
	}


	private static boolean isImplementing(PsiClass aClass, String fullyQualifiedName) {
		for (PsiClass psiInterface : aClass.getInterfaces()) {
			if (fullyQualifiedName.equalsIgnoreCase(psiInterface.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}
}
