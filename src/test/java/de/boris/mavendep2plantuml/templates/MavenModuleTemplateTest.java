package de.boris.mavendep2plantuml.templates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.boris.mavendep2plantuml.templates.MavenModuleTemplate;

public class MavenModuleTemplateTest {
	@Test
	public void testMavenModuleTemplate() throws Exception {
		MavenModuleTemplate template = new MavenModuleTemplate();
		assertEquals(template.getTemplate().replace("@", ""), template.fillFrom("projectArtifactId", "mavenModuleArtifactId", "mavenModuleArtifactName", "mavenModuleArtifactDescription", "scope"));
	}
}
