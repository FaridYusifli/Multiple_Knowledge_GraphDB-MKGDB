package mkgdb.graph.extractors;

import java.nio.charset.Charset;

import mkgdb.graph.NodeRelation;

public class DBPediaOntologyExtractor extends LineExtractor {

	private String knowledgeBase;

	public DBPediaOntologyExtractor(String filePath, Charset charset, String knowledgeBase) {
		super(filePath, charset);
		this.knowledgeBase = knowledgeBase;
	}

	@Override
	public NodeRelation parse(String line) {
		String[] columns = line.split(" ");
		if (columns.length < 2 || !IsSubclass(columns[1]))
			return null;

		String hypernymPart = line.substring(line.lastIndexOf("/") + 1, line.lastIndexOf(">"));
		return new NodeRelation(columns[0].substring(columns[0].lastIndexOf("/") + 1, columns[0].lastIndexOf(">")).toLowerCase(),
				hypernymPart.substring(hypernymPart.lastIndexOf("#") + 1, hypernymPart.length()).toLowerCase(), knowledgeBase);
	}

	private static boolean IsSubclass(String part) {
		return part.substring(part.length() - 11, part.length() - 1).equals("subClassOf");
	}

}