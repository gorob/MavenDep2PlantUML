package de.boris.mavendep2plantuml.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.boris.mavendep2plantuml.templates.AbstractTemplate;

public class AbstractTemplateTest {
	@Test
	public void testTemplate_mavenModuleTemplate() throws Exception {
		AbstractTemplate template = new AbstractTemplate("mavenModuleTemplate") {};
		List<String> variableNames = template.getVariableNames();
		assertEquals(5, variableNames.size());
		assertTrue(variableNames.contains("projectArtifactId"));
		assertTrue(variableNames.contains("mavenModuleArtifactId"));
		assertTrue(variableNames.contains("mavenModuleArtifactName"));
		assertTrue(variableNames.contains("mavenModuleArtifactDescription"));
		assertTrue(variableNames.contains("scope"));
	}

	@Test
	public void testFillTemplate() throws Exception {
		AbstractTemplate template = new AbstractTemplate("mavenModuleTemplate") {};
		String filledTemplate = template.fillTemplate(TemplateVariable.from("projectArtifactId", "projectArtifactId"),
				TemplateVariable.from("mavenModuleArtifactId", "mavenModuleArtifactId"), 
				TemplateVariable.from("mavenModuleArtifactName", "mavenModuleArtifactName"),
				TemplateVariable.from("mavenModuleArtifactDescription", "mavenModuleArtifactDescription"),
				TemplateVariable.from("scope", "scope")
		);
		assertEquals(template.getTemplate().replace(AbstractTemplate.VARIABLE_IDENTIFIER_CHAR, ""), filledTemplate);
	}

}
