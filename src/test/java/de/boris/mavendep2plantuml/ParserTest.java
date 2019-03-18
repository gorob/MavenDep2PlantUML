package de.boris.mavendep2plantuml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

public class ParserTest {
	@Test
	public void testExtractProjectTag() throws Exception {
		Parser parser = new Parser("mavenBinPath", "rootPathLocalMavenRepo", "rootPathProjectToScan");
		String project1 = "<project xml=...>aaa</project>";
		String project2 = "<project xml=...>bbb</project>";
		String project3 = "<project xml=...>ccc</project>";

		assertEquals(project1, parser.extractProjectTag(project1, 0));
		assertNull(parser.extractProjectTag(project1, 1));
		
		String beginTag = "<projects>";
		String endTag = "</projects>";
		assertEquals(project1, parser.extractProjectTag(beginTag + project1 + project2 + project3 + endTag, 0));
		assertEquals(project1, parser.extractProjectTag(beginTag + project1 + project2 + project3 + endTag, beginTag.length()));
		assertEquals(project2, parser.extractProjectTag(beginTag + project1 + project2 + project3 + endTag, beginTag.length()+1));

		assertEquals(project2, parser.extractProjectTag(beginTag + project1 + project2 + project3 + endTag, beginTag.length() + project1.length()));
		assertEquals(project3, parser.extractProjectTag(beginTag + project1 + project2 + project3 + endTag, beginTag.length() + project1.length() + 1));

		assertEquals(project3, parser.extractProjectTag(beginTag + project1 + project2 + project3 + endTag, beginTag.length() + project1.length() + project2.length()));
		assertNull(parser.extractProjectTag(beginTag + project1 + project2 + project3 + endTag, beginTag.length() + project1.length() + project2.length() + 1));
	}

	@Test
	public void testExtractXMLs() throws Exception {
		Parser parser = new Parser("mavenBinPath", "rootPathLocalMavenRepo", "rootPathProjectToScan");
		String project1 = "<project xml=...>aaa</project>";
		String project2 = "<project xml=...>bbb</project>";
		String project3 = "<project xml=...>ccc</project>";

		String beginTag = "<projects";
		String endTag = "</projects>";

		List<String> xmls = parser.extractXMLs(beginTag + project1 + project2 + project3 + endTag);
		assertEquals(3, xmls.size());
		assertEquals(project1, xmls.get(0));
		assertEquals(project2, xmls.get(1));
		assertEquals(project3, xmls.get(2));		
	}

	@Test
	public void testEinruecken() throws Exception {
		Parser parser = new Parser("mavenBinPath", "rootPathLocalMavenRepo", "rootPathProjectToScan");
		String expectedText = "    zeile1" + System.lineSeparator() + "    zeile2" + System.lineSeparator() + "    zeile3";

		String text = "zeile1" + System.lineSeparator() + "zeile2" + System.lineSeparator() + "zeile3";
		String ergText = parser.einruecken(text, 4);
		assertEquals(expectedText, ergText);
		
		text = "zeile1" + System.lineSeparator() + "zeile2" + System.lineSeparator() + "zeile3" + System.lineSeparator();
		ergText = parser.einruecken(text, 4);
		assertEquals(expectedText, ergText);
	}

}
