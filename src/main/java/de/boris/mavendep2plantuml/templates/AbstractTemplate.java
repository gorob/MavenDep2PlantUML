package de.boris.mavendep2plantuml.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTemplate {
	private String template;
	private List<String> variableNames;
	
	protected static final String VARIABLE_IDENTIFIER_CHAR = "@";
	
	public AbstractTemplate(String templateName) {
		this.template = readTemplate(templateName);
		this.variableNames = extractVariableNames();
	}

	protected String getTemplate() {
		return template;
	}

	protected List<String> getVariableNames() {
		return variableNames;
	}

	protected final String fillTemplate(TemplateVariable... templateVariablen) {
		String template = getTemplate();
		
		for (TemplateVariable templateVariable : templateVariablen) {
			if (!getVariableNames().contains(templateVariable.getName())) {
				throw new RuntimeException("Variablename " + templateVariable.getName() + " existiert nicht in Template!");
			}
			template = template.replace(VARIABLE_IDENTIFIER_CHAR + templateVariable.getName() + VARIABLE_IDENTIFIER_CHAR, templateVariable.getValue());
		}
		
		return template;
	}
	
	private List<String> extractVariableNames() {
		List<String> variableNames = new ArrayList<>();

		String template = getTemplate();
		
		while (true) {
			int beginIndex = template.indexOf(VARIABLE_IDENTIFIER_CHAR);
			
			if (beginIndex==-1) {
				break;
			}
			
			int endIndex = template.indexOf(VARIABLE_IDENTIFIER_CHAR, beginIndex+1);
			String variableName = template.substring(beginIndex+1, endIndex);
			
			if (!variableNames.contains(variableName)) {
				variableNames.add(variableName);
			}
		
			template = template.substring(beginIndex + variableName.length() + 2);
		}
		
		return variableNames;
	}
	
	private String readTemplate(String templateName) {
		InputStream in = getClass().getResourceAsStream("/" + templateName); 
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(in))) {
            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException ex) {
			throw new RuntimeException("Fehler beim Lesen von Template-Datei " + templateName);
		}
	}
}
