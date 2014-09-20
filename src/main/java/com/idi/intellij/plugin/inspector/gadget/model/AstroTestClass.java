/*
 * User: eldad.Dor
 * Date: 19/08/2014 17:15
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.inspector.gadget.model;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;

import java.io.Serializable;

/**
 * @author eldad
 * @date 19/08/2014
 */
@Tag("AstroTestClass")
public class AstroTestClass implements Serializable, Cloneable {

	public String name = "";

	@Attribute("TestAnnotation")
	public String annotationFQN = "";

	@Override
	protected Object clone() throws CloneNotSupportedException {
		final AstroTestClass astroTestClass = new AstroTestClass();
		astroTestClass.annotationFQN = annotationFQN;
		astroTestClass.name = name;
		return astroTestClass;
	}
}