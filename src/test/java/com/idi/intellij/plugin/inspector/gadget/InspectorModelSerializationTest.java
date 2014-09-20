/*
 * User: eldad.Dor
 * Date: 17/08/2014 11:16
 
 *
 * Copyright (2005) IDI. All rights reserved.
 * This software is a proprietary information of Israeli Direct Insurance.
 * Created by IntelliJ IDEA. 
 */
package com.idi.intellij.plugin.inspector.gadget;

import com.idi.intellij.plugin.inspector.gadget.model.InspectorSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.testFramework.IdeaTestCase;
import com.intellij.util.xmlb.SerializationFilter;
import com.intellij.util.xmlb.XmlSerializationException;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import com.intellij.util.xmlb.annotations.Property;
import org.jdom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author eldad
 * @date 17/08/2014
 */
public class InspectorModelSerializationTest extends IdeaTestCase {
	private static final String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static final Logger logger = Logger.getInstance(InspectorModelSerializationTest.class.getName());


	@Property(surroundWithTag = false)
	@MapAnnotation(surroundWithTag = false, entryTagName = "option", keyAttributeName = "name", valueAttributeName = "value")
	public Map<String, String> VALUES = new HashMap<String, String>();

	{
		VALUES.put("a", "1");
		VALUES.put("b", "2");
		VALUES.put("c", "3");
	}

	public static class BeanWithMapWithAnnotations {
		@Property(surroundWithTag = false)
		@MapAnnotation(surroundWithTag = false, entryTagName = "option", keyAttributeName = "name", valueAttributeName = "value")
		public Map<String, String> VALUES = new HashMap<String, String>();

		{
			VALUES.put("a", "1");
			VALUES.put("b", "2");
			VALUES.put("c", "3");
		}
	}

	public void testInspectorSettingsConfiguration() throws Exception {
		InspectorSettings bean = new InspectorSettings();
		final String xml = "<InspectorSettings>\n" +
				"<annotation name= \"RemovableCandidate\" value=\"com.idi.framework.common.metadata.annotations.intentions\" />\n" +
				"<annotation name= \"RefactoringCandidate\" value=\"com.idi.framework.common.metadata.annotations.refactor\" />\n" +
				"</InspectorSettings>";
		final Element serialize = serialize(bean, null);
		final Object deserializersBean = doSerializerTest(xml, bean);
		logger.info("testInspectorSettingsConfiguration():");
		final FileOutputStream fileOutputStream = new FileOutputStream(new File("c:/InsepctorSettings.xml"));

	}

	public void testMapSerializationWithAnnotations() {
		BeanWithMapWithAnnotations bean = new BeanWithMapWithAnnotations();
		doSerializerTest(
				"<BeanWithMapWithAnnotations>\n" +
						"  <option name=\"a\" value=\"1\" />\n" +
						"  <option name=\"b\" value=\"2\" />\n" +
						"  <option name=\"c\" value=\"3\" />\n" +
						"</BeanWithMapWithAnnotations>",
				bean);
		bean.VALUES.clear();
		bean.VALUES.put("1", "a");
		bean.VALUES.put("2", "b");
		bean.VALUES.put("3", "c");

		doSerializerTest("<BeanWithMapWithAnnotations>\n" +
						"  <option name=\"1\" value=\"a\" />\n" +
						"  <option name=\"2\" value=\"b\" />\n" +
						"  <option name=\"3\" value=\"c\" />\n" +
						"</BeanWithMapWithAnnotations>",
				bean);
	}

	private static Element serialize(Object bean, SerializationFilter filter) {
		return XmlSerializer.serialize(bean, filter);
	}


	private static Object doSerializerTest(String expectedText, Object bean) {
		try {
			Element element = assertSerializer(bean, expectedText, null);

			//test deserializer

			Object o = XmlSerializer.deserialize(element, bean.getClass());
			assertSerializer(o, expectedText, "Deserialization failure", null);
			return o;
		} catch (XmlSerializationException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	//---------------------------------------------------------------------------------------------------
	private static Element assertSerializer(Object bean, String expected, SerializationFilter filter) {
		return assertSerializer(bean, expected, "Serialization failure", filter);
	}

	private static Element assertSerializer(Object bean, String expectedText, String message, SerializationFilter filter) throws XmlSerializationException {
		Element element = serialize(bean, filter);
		String actualString = JDOMUtil.writeElement(element, "\n").trim();
		if (!expectedText.startsWith(XML_PREFIX)) {
			if (actualString.startsWith(XML_PREFIX)) {
				actualString = actualString.substring(XML_PREFIX.length()).trim();
			}
		}
		assertEquals(message, expectedText, actualString);
		return element;
	}


}