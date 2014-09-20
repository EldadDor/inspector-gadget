/*
 * User: eldad.Dor
 * Date: 19/08/2014 13:17
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.inspector.gadget.inspection;

import com.idi.intellij.plugin.inspector.gadget.model.InsepctorSettingsConfiguration;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiTypeParameter;
import com.siyeh.ig.BaseInspectionVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author eldad
 * @date 19/08/2014
 */
public class IDIAstroTestConventionInspection extends LocalInspectionTool {
	private static final Logger log = Logger.getInstance(IDIAstroTestConventionInspection.class.getName());
	private String m_shortName;
	@NonNls
	private static final String INSPECTION = "Inspection";

	@Pattern("[a-zA-Z_0-9.-]+")
	@NotNull
	@Override
	public String getID() {
		return IDIAstroTestConventionInspection.class.getName();
	}


	@Override
	@NotNull
	public final String getShortName() {
		if (m_shortName == null) {
			final Class<? extends LocalInspectionTool> aClass = getClass();
			final String name = aClass.getName();
			assert name.endsWith(INSPECTION) : "class name must end with 'Inspection' to correctly" + " calculate the short name: " + name;
			m_shortName = name.substring(name.lastIndexOf((int) '.') + 1, name.length() - INSPECTION.length());
		}
		return m_shortName;
	}

	@Nullable
	@Override
	public String getAlternativeID() {
		return super.getAlternativeID();
	}


	@Nls
	@NotNull
	@Override
	public String getDisplayName() {
		return getID();
	}


	private static class AstroTestConventionVisitor extends BaseInspectionVisitor {
		private final ProblemsHolder problemsHolder;
		private final Project project;

		private AstroTestConventionVisitor(final ProblemsHolder holder) {
			problemsHolder = holder;
			project = problemsHolder.getProject();
		}

		@Override
		public void visitClass(PsiClass aClass) {
			if (aClass instanceof PsiTypeParameter) {
				return;
			}
			final InsepctorSettingsConfiguration settings = InsepctorSettingsConfiguration.getInstance(project);
			if (aClass.getQualifiedName().equalsIgnoreCase(settings.getState().ASTRO_TEST_CLASS)) {

			}
			if (aClass.getModifierList().findAnnotation("") != null) {

			}
		}
	}

}