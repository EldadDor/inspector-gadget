/*
 * User: eldad.Dor
 * Date: 17/08/2014 11:28
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.inspector.gadget.model;

import com.google.common.collect.Maps;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author eldad
 * @date 17/08/2014
 */
public class InspectorSettings implements Cloneable {
	@Property(surroundWithTag = false)
	@MapAnnotation(surroundWithTag = false, entryTagName = "annotation", keyAttributeName = "name", valueAttributeName = "value")
	public Map<String, String> ANNOTATIONS = new HashMap<String, String>();

	{
		ANNOTATIONS.put("RemovableCandidate", "com.idi.framework.common.metadata.annotations.intentions");
		ANNOTATIONS.put("RefactoringCandidate", "com.idi.framework.common.metadata.annotations.refactor");
	}

	@Property(surroundWithTag = false)
	@AbstractCollection(surroundWithTag = false)
	public AstroTestClass TEST_CLASS;

	@AbstractCollection(surroundWithTag = false, elementTypes = {AstroTestClass.class})
	public Object[] actions;

	@Tag(value = "IfsTestClass")
	public String IFS_TEST_CLASS = "com.idi.ifs.server.test.IFSNewTest";
	@Tag(value = "AstroTestClass")
	public String ASTRO_TEST_CLASS = "com.idi.astro.server.server.test.AstroNewTest";


	@Property(surroundWithTag = false)
	@MapAnnotation(surroundWithTag = false, entryTagName = "WebServiceConsumer", keyAttributeName = "name", valueAttributeName = "value")
	public Map<String, String> WS_CONSUMER = new HashMap<String, String>();
	{
		WS_CONSUMER.put("@WSConsumer", "com.idi.astro.train.annotation.WSConsumer");
		WS_CONSUMER.put("@WSParam", "com.idi.astro.train.annotation.WSParam");
		WS_CONSUMER.put("@WSHeader", "com.idi.astro.train.annotation.WSHeader");
		WS_CONSUMER.put("@Property", "com.idi.astro.train.annotation.Property");
	}

	@Tag(value = "WSConsumerClass")
	public String WS_CONSUMER_CLASS = "com.idi.astro.train.model.WebserviceConsumer";


	@Override
	protected Object clone() throws CloneNotSupportedException {
		final InspectorSettings clone = new InspectorSettings();
		final Map<String, String> map = Maps.newHashMap();
		map.putAll(ANNOTATIONS);
		clone.ANNOTATIONS = map;
		clone.ASTRO_TEST_CLASS = ASTRO_TEST_CLASS;
		clone.IFS_TEST_CLASS = IFS_TEST_CLASS;
		clone.TEST_CLASS = (AstroTestClass) TEST_CLASS.clone();
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		InspectorSettings that = (InspectorSettings) o;
		if (ANNOTATIONS != null) {
			return isAnnotationMapEquals(ANNOTATIONS);
		}
		if (ASTRO_TEST_CLASS != null ? !ASTRO_TEST_CLASS.equals(that.ASTRO_TEST_CLASS) : that.ASTRO_TEST_CLASS != null) {
			return false;
		}
		if (IFS_TEST_CLASS != null ? !IFS_TEST_CLASS.equals(that.IFS_TEST_CLASS) : that.IFS_TEST_CLASS != null) {
			return false;
		}
		if (TEST_CLASS != null ? !TEST_CLASS.equals(that.TEST_CLASS) : that.TEST_CLASS != null) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = ANNOTATIONS != null ? ANNOTATIONS.hashCode() : 0;
		result = 31 * result + (TEST_CLASS != null ? TEST_CLASS.hashCode() : 0);
		result = 31 * result + (IFS_TEST_CLASS != null ? IFS_TEST_CLASS.hashCode() : 0);
		result = 31 * result + (ASTRO_TEST_CLASS != null ? ASTRO_TEST_CLASS.hashCode() : 0);
		return result;
	}

	private boolean isAnnotationMapEquals(Map<String, String> other) {
		final Set<String> keys = other.keySet();
		for (final String key : keys) {
			if (!(ANNOTATIONS.containsKey(key) && ANNOTATIONS.get(key).equals(other.get(key)))) {
				return false;
			}
		}
		return true;
	}
}