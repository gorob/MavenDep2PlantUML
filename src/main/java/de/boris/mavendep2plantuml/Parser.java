package de.boris.mavendep2plantuml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.google.common.io.Closeables;

import de.boris.mavendep2plantuml.templates.MavenModuleTemplate;
import de.boris.mavendep2plantuml.templates.MavenProjectTemplate;
import de.boris.mavendep2plantuml.templates.SkinParamTemplate;

public class Parser {
	public static void main(String[] args) {
		// D:\\sts-4.1.2.RELEASE\\workspace\\myGTD-Service
		new Parser("D:\\apache-maven-3.3.9\\bin\\", "C:\\Users\\bozma\\.m2\\repository", "C:\\Users\\bozma\\git\\homemanager-java-client").execute();
	}

	private SkinParamTemplate skinParamTemplate;
	private MavenProjectTemplate mavenProjectTemplate;
	private MavenModuleTemplate mavenModuleTemplate;
	private Path mavenBinPath;
	private Path rootPathLocalMavenRepo;
	private Path rootPathProjectToScan;
	private StringBuffer pumlContent;
	
	private Map<String, String> filledMavenProjectTemplates;
	private Map<String, String> filledMavenModulesTemplates;

	public Parser(String mavenBinPath, String rootPathLocalMavenRepo, String rootPathProjectToScan) {
		this.skinParamTemplate = new SkinParamTemplate();
		this.mavenProjectTemplate = new MavenProjectTemplate();
		this.mavenModuleTemplate = new MavenModuleTemplate();
		this.mavenBinPath = Paths.get(mavenBinPath);
		this.rootPathLocalMavenRepo = Paths.get(rootPathLocalMavenRepo);
		this.rootPathProjectToScan = Paths.get(rootPathProjectToScan);
		this.pumlContent = null;
		this.filledMavenProjectTemplates = new HashMap<String, String>();
		this.filledMavenModulesTemplates = new HashMap<String, String>();
	}

	private SkinParamTemplate getSkinParamTemplate() {
		return skinParamTemplate;
	}

	private MavenProjectTemplate getMavenProjectTemplate() {
		return mavenProjectTemplate;
	}

	private MavenModuleTemplate getMavenModuleTemplate() {
		return mavenModuleTemplate;
	}

	private Path getMavenBinPath() {
		return mavenBinPath;
	}

	private Path getRootPathLocalMavenRepo() {
		return rootPathLocalMavenRepo;
	}

	private Path getRootPathProjectToScan() {
		return rootPathProjectToScan;
	}

	private StringBuffer getPumlContent() {
		return pumlContent;
	}
	
	private Map<String, String> getFilledMavenProjectTemplates() {
		return filledMavenProjectTemplates;
	}

	private Map<String, String> getFilledMavenModulesTemplates() {
		return filledMavenModulesTemplates;
	}
	
	public void execute() {
		checkLocalMavenRepoExists();
		this.pumlContent = new StringBuffer();
		readPOM(getRootPathProjectToScan().toFile().getAbsolutePath(), null, null);
		writePUML();
	}

	private void writePUML() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(getSkinParamTemplate().fillFrom());
		buffer.append(System.lineSeparator());
		buffer.append(System.lineSeparator());
		
		List<String> projectKeys = new ArrayList<String>(getFilledMavenProjectTemplates().keySet());
		
		for (String projectKey : projectKeys) {
			buffer.append(getFilledMavenProjectTemplates().get(projectKey));
			buffer.append(System.lineSeparator());

//			for (String mavenModuleKey : getMavenModuleKeys(projectKey)) {
//				buffer.append(getFilledMavenModulesTemplates().get(mavenModuleKey));
//				buffer.append(System.lineSeparator());
//			}
		}
		
		System.out.println(buffer.toString());
	}
	
	private List<String> getMavenModuleKeys(String projectKey) {
		return getFilledMavenModulesTemplates().keySet().stream().filter(key -> key.startsWith(projectKey + ".")).collect(Collectors.toList());
	}

	private void checkLocalMavenRepoExists() {
		File localMavenRepoPath = getRootPathLocalMavenRepo().toFile();
		if (!localMavenRepoPath.exists()) {
			throw new RuntimeException("Lokales Maven-Repo existiert nicht unter " + localMavenRepoPath + "!");
		}
	}

	private String addMavenProject(Model pomProject, String scopeColor, String... subModules) {
		String projectArtifactId = pomProject.getArtifactId();

		String templateId = projectArtifactId;
		
		System.out.println("addMavenProject: " + templateId);
		
		if (getFilledMavenProjectTemplates().containsKey(templateId)) {
			System.out.println(templateId + " bereits enthalten");
			return null;
		}
		
		String projectArtifactName = pomProject.getName()==null ? "" : pomProject.getName();
		String projectArtifactDescription = pomProject.getDescription()==null ? "---" : pomProject.getDescription();
		
		StringBuffer buffer = new StringBuffer();
		for (String subModule : subModules) {
			buffer.append(subModule);
		}
		String subModulesStr = buffer.toString();
		if (subModulesStr.endsWith(System.lineSeparator())) {
			subModulesStr = subModulesStr.substring(0, subModulesStr.length()-System.lineSeparator().length());
		}
		
		String filledTemplate = getMavenProjectTemplate().fillFrom(projectArtifactId, projectArtifactName, projectArtifactDescription, scopeColor, subModulesStr);
		getFilledMavenProjectTemplates().put(templateId, filledTemplate);
		
		return filledTemplate;
	}
	
	private String addMavenModule(Model pomProject, Model pomModule, String scopeColor) {
		String projectArtifactId = pomProject.getArtifactId();
		String mavenModuleArtifactId = pomModule.getArtifactId();
		
		String templateId = projectArtifactId + "." + mavenModuleArtifactId;

		System.out.println("addMavenModule: " + templateId);

		if (getFilledMavenModulesTemplates().containsKey(templateId)) {
			System.out.println(templateId + " bereits enthalten");
			return null;
		}

		String mavenModuleArtifactName = pomModule.getName()==null ? "" : pomModule.getName();
		String mavenModuleArtifactDescription = pomModule.getDescription()==null ? "---" : pomModule.getDescription();

		String filledTemplate = getMavenModuleTemplate().fillFrom(projectArtifactId, mavenModuleArtifactId, mavenModuleArtifactName, mavenModuleArtifactDescription, scopeColor);
		getFilledMavenModulesTemplates().put(templateId, filledTemplate);
		
		return filledTemplate;
	}
	
	private String replicate(String text, int anzahl) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<anzahl; i++) {
			buffer.append(text);
		}
		return buffer.toString();
	}
	
	protected String einruecken(String text, int anzahlLeerzeichen) {
		return replicate(" ", anzahlLeerzeichen) + text.replace(System.lineSeparator(), System.lineSeparator() + replicate(" ", anzahlLeerzeichen)).trim();
	}
	
	private void readPOM(String path2POM, String pomFileName, String scopeColor) {
		System.out.println("Read " + path2POM + "." + pomFileName);
		
		List<Model> poms = getEffectivePoms(path2POM, pomFileName);

		if (poms.size()==1) {
			addMavenProject(poms.get(0), scopeColor);
			parseDependentProjects(poms.get(0));
		} else {
			Model pomProject = poms.get(0);
			
			List<String> subModules = new ArrayList<>();
			for (Model pomModule : poms.subList(1, poms.size())) {
				String filledTemplateModule = addMavenModule(pomProject, pomModule, scopeColor);
				if (filledTemplateModule!=null) {
					subModules.add(einruecken(filledTemplateModule, 4) + System.lineSeparator());
				}
				parseDependentProjects(pomModule);
			}
			addMavenProject(pomProject, scopeColor, subModules.toArray(new String[] {}));
			parseDependentProjects(pomProject);
		}
	}
	
	private void parseDependentProjects(Model pom) {
		List<Dependency> dependencies = pom.getDependencies();
		for (Dependency dependency : dependencies) {
			String pathToPomForDependency = getPathToPomForDependency(dependency);
			String artifactPomFilenameForDependency = getArtifactPomFilenameForDependency(dependency);
			String scopeColor = getScopeColor(dependency.getScope());
			System.out.println("Dependency: " + pathToPomForDependency + "   " + artifactPomFilenameForDependency + " (" + scopeColor + ")");
			
			readPOM(pathToPomForDependency, artifactPomFilenameForDependency, scopeColor);
		}		
	}
	
	private String getScopeColor(String scope) {
		if (scope.equalsIgnoreCase("test")) {
			return "(TEST)";
		}
		if (scope.equalsIgnoreCase("compile")) {
			return "(COMPILE)";
		}
		if (scope.equalsIgnoreCase("provided")) {
			return "(PROVIDED)";
		}
		if (scope.equalsIgnoreCase("runtime")) {
			return "(RUNTIME)";
		}
		if (scope.equalsIgnoreCase("system")) {
			return "(SYSTEM)";
		}
		if (scope.equalsIgnoreCase("import")) {
			return "(IMPORT)";
		}
		return "(COMPILE)";
	}

	private List<Model> getEffectivePoms(String path2POM, String pomFileName) {
		List<Model> effectivePoms = new ArrayList<>();
		
		String commandLog = new MavenExecutioner(getMavenBinPath().toFile().getAbsolutePath()).runMavenCommand("help:effective-pom", path2POM, pomFileName);
		
		List<String> projectXMLs = extractXMLs(commandLog);
		
		for (String projectXML : projectXMLs) {
			effectivePoms.add(getPomFromStr(projectXML));
		}
		
		return effectivePoms;
	}

	protected List<String> extractXMLs(String commandLog) {
		List<String> projectXMLs = new ArrayList<String>();
		
		int beginIndex = commandLog.indexOf("<project ");
		if (beginIndex==-1) {
			return null;
		}
		
		while (true) {
			String projectXML = extractProjectTag(commandLog, beginIndex);
			
			if (projectXML==null) {
				break;
			}
			
			projectXMLs.add(projectXML);
			
			beginIndex += projectXML.length();
		}
		
		return projectXMLs;
	}

	protected String extractProjectTag(String text, int beginIndexOffset) {
		String beginTag = "<project ";
		String endTag = "</project>";
		int beginIndex = text.indexOf(beginTag, beginIndexOffset);
		if (beginIndex==-1) {
			return null;
		}

		int endIndex = text.indexOf(endTag, beginIndex);
		if (endIndex==-1) {
			return null;
		}
		
		return text.substring(beginIndex, endIndex) + endTag;
	}
	
	private Model getPomFromStr(String pomAsStr) {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		StringReader stringReader=null;
		try {
			stringReader = new StringReader(pomAsStr);
			return reader.read(stringReader);
		}
		catch (XmlPullParserException | IOException ex) {
			throw new RuntimeException("Fehler beim Erstellen Maven-Model!", ex);
		}
		finally {
			Closeables.closeQuietly(stringReader);
		}
	}






	private String getPathToPomForDependency(Dependency dependency) {
		String relativePathToArtifact = getRelativePathToArtifact(dependency.getArtifactId(), dependency.getGroupId(), dependency.getVersion());
		return getRootPathLocalMavenRepo().resolve(relativePathToArtifact).toFile().getAbsolutePath();
	}
	
	private String getArtifactPomFilenameForDependency(Dependency dependency) {
		return getArtifactNameWithVersion(dependency.getArtifactId(), dependency.getVersion());
	}

	private String getRelativePathToArtifact(String artifactId, String groupId, String version) {
		Objects.requireNonNull(artifactId);
		Objects.requireNonNull(groupId);
		Objects.requireNonNull(version);
		return groupId.replace(".", File.separator) + File.separator + artifactId + File.separator + version;
	}

	private String getArtifactNameWithVersion(String artifactId, String version) {
		Objects.requireNonNull(artifactId);
		Objects.requireNonNull(version);
		return artifactId + "-" + version + ".pom";
	}

}
