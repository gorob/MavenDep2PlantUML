package de.boris.mavendep2plantuml.templates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.boris.mavendep2plantuml.templates.SkinParamTemplate;

public class SkinParamTemplateTest {
	@Test
	public void testSkinParamTemplate() throws Exception {
		SkinParamTemplate template = new SkinParamTemplate();
		assertEquals(template.getTemplate(), template.fillFrom());
	}
}
