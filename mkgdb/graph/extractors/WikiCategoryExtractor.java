package mkgdb.graph.extractors;

import java.nio.charset.Charset;

import com.google.common.base.CharMatcher;

import mkgdb.graph.NodeRelation;

public class WikiCategoryExtractor extends LineExtractor {

	private String knowledgeBase;
	private final CharMatcher charMatcher = CharMatcher.is('<');

	public WikiCategoryExtractor(String filePath, Charset charset, String knowledgeBase) {
		super(filePath, charset);
		this.knowledgeBase = knowledgeBase;
	}

	@Override
	public NodeRelation parse(String line) {
		String hyponymPart;
		if (line.startsWith("#") || (charMatcher.countIn(line) != 3) || (hyponymPart = getHyponymPart(line)) == null)
			return null;

		return new NodeRelation(
				nameFixer.apply(hyponymPart.substring(hyponymPart.lastIndexOf(":") + 1, hyponymPart.lastIndexOf(">"))),
				nameFixer.apply(line.substring(line.lastIndexOf(":") + 1, line.lastIndexOf(">"))), knowledgeBase);
	}

	private static String getHyponymPart(String line) {
		String[] parts = line.split(" ");

		if (parts.length < 3 || !(parts[1].substring(parts[1].length() - 8, parts[1].length() - 1)).equals("broader")
				|| parts[0].contains("%") || parts[2].contains("%"))
			return null;
		return parts[0];
	}
}