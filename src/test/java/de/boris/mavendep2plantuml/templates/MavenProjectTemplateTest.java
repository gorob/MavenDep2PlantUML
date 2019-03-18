package de.boris.mavendep2plantuml.templates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.boris.mavendep2plantuml.templates.MavenProjectTemplate;

public class MavenProjectTemplateTest {
	@Test
	public void testMavenProjectTemplate() throws Exception {
		MavenProjectTemplate template = new MavenProjectTemplate();
		assertEquals(template.getTemplate().replace("@", ""), template.fillFrom("projectArtifactId", "projectArtifactName", "projectArtifactDescription", "scope", "submodules"));
	}
}
