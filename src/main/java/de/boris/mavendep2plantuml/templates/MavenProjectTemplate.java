package de.boris.mavendep2plantuml.templates;

public class MavenProjectTemplate extends AbstractTemplate {
	public MavenProjectTemplate() {
		super("mavenProjectTemplate");
	}

	public String fillFrom(String projectArtifactId, String projectArtifactName, String projectArtifactDescription, String scopeColor, String submodules) {
		return fillTemplate(TemplateVariable.from("projectArtifactId", projectArtifactId),
							TemplateVariable.from("projectArtifactName", projectArtifactName),
							TemplateVariable.from("projectArtifactDescription", projectArtifactDescription),
							TemplateVariable.from("scope", scopeColor),
							TemplateVariable.from("submodules", submodules)
		);
	}
}
