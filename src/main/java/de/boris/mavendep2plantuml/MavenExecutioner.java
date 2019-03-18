package de.boris.mavendep2plantuml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class MavenExecutioner {
	public static final String DEFAULT_EXTERNAL_MAVEN_DIR = "D:\\apache-maven-3.3.9\\bin\\";
	private String externalMavenDir;
	
	public MavenExecutioner() {
		this(DEFAULT_EXTERNAL_MAVEN_DIR);
	}

	public MavenExecutioner(String externalMavenDir) {
		this.externalMavenDir = externalMavenDir;
	}
	
	private String getExternalMavenDir() {
		return externalMavenDir;
	}

	private String readFromInputStream(BufferedReader reader) {
		StringBuffer buffer = new StringBuffer();
		
		String line = null;
		
		try {
			while ((line = reader.readLine()) != null) {
				buffer.append(line + System.lineSeparator());
			}
			return buffer.toString();
		} catch (IOException ex) {
			throw new RuntimeException("Fehler beim Lesen Input-Stream!", ex);
		}
	}
	
	public String runMavenCommand(String mavenCommand, String executionDir, String pomFileName) {
		Objects.requireNonNull(executionDir);
		File dir = new File(executionDir);
		Runtime runtime = Runtime.getRuntime();
		try {
			if (pomFileName!=null) {
				mavenCommand = "-f " + new File(executionDir, pomFileName).getAbsolutePath() + " " + mavenCommand;
			}
			Process process = runtime.exec(new File(getExternalMavenDir(), "mvn.cmd").getAbsolutePath() + " " + mavenCommand, null, dir);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String stdOutLog = readFromInputStream(stdInput);

			if (process.exitValue()>0) {
				throw new RuntimeException("Fehler bei Ausführung Maven-Command '" + mavenCommand + "'! Exit-Code: " + process.exitValue() + "\n" + stdOutLog);
			}
			
			return stdOutLog;
		} catch (IOException ex) {
			throw new RuntimeException("Fehler bei Ausführung Maven-Command '" + mavenCommand + "'!", ex);
		}
	}
}
