/*
 * User: eldad.Dor
 * Date: 17/08/2014 11:17
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.inspector.gadget.model;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author eldad
 * @date 17/08/2014
 */
@State(
		name = "InsepctorSettings",
		storages = {
				@Storage(
						id = "other",
						file = StoragePathMacros.APP_CONFIG + "/InsepctorSettings.xml", scheme = StorageScheme.DIRECTORY_BASED
				)}
)
public class InsepctorSettingsConfiguration implements PersistentStateComponent<InspectorSettings>, ApplicationComponent {
	private final Project project;
	private InspectorSettings settings = new InspectorSettings();
//	private InspectorSettings settings;


	public InsepctorSettingsConfiguration(Project project) {
		this.project = project;
	}

	public InsepctorSettingsConfiguration() {
		this(null);
	}

	@Nullable
	@Override
	public InspectorSettings getState() {
		return settings;
	}

	@Override
	public void loadState(InspectorSettings inspectorSettings) {
		settings = inspectorSettings;
	}

	public static InsepctorSettingsConfiguration getInstance(@NotNull Project project) {
		return ServiceManager.getService(project, InsepctorSettingsConfiguration.class);
	}

	@Override
	public void initComponent() {

	}

	@Override
	public void disposeComponent() {

	}

	@NotNull
	@Override
	public String getComponentName() {
		return getClass().getName();
	}
}