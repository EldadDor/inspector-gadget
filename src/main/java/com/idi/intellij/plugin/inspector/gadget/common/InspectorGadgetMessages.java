/*
 * User: eldad.Dor
 * Date: 19/08/2014 11:49
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.inspector.gadget.common;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

/**
 * @author eldad
 * @date 19/08/2014
 */
public class InspectorGadgetMessages {

	@NonNls
	public static final String BUNDLE = "messages.inspectorGadgetMessages";

	private InspectorGadgetMessages() {
	}

	public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
		return CommonBundle.message(getBundle(), key, params);
	}

	private static class ResourceBundleHolder {
		private static final ResourceBundle ourBundle = ResourceBundle.getBundle(BUNDLE);
	}

	private static ResourceBundle getBundle() {
		return ResourceBundleHolder.ourBundle;
	}
}