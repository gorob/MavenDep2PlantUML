package de.boris.mavendep2plantuml.templates;

public class MavenModuleTemplate extends AbstractTemplate {
	public MavenModuleTemplate() {
		super("mavenModuleTemplate");
	}
	
	public String fillFrom(String projectArtifactId, String mavenModuleArtifactId, String mavenModuleArtifactName, String mavenModuleArtifactDescription, String scopeColor) {
		return fillTemplate(TemplateVariable.from("projectArtifactId", projectArtifactId),
							TemplateVariable.from("mavenModuleArtifactId", mavenModuleArtifactId),
							TemplateVariable.from("mavenModuleArtifactName", mavenModuleArtifactName),
							TemplateVariable.from("mavenModuleArtifactDescription", mavenModuleArtifactDescription),
							TemplateVariable.from("scope", scopeColor)
		);
	}
}
