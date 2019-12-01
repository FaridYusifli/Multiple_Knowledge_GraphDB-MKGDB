package mkgdb.graph.extractors;

import java.nio.charset.Charset;

import mkgdb.graph.NodeRelation;

public class DBPediaInstancesExtractor extends LineExtractor {

	private String knowledgeBase;

	public DBPediaInstancesExtractor(String filePath, Charset charset, String knowledgeBase) {
		super(filePath, charset);
		this.knowledgeBase = knowledgeBase;
	}

	@Override
	public NodeRelation parse(String line) {
		if (line.startsWith("# <BAD URI") || !IsType(line))
			return null;

		String hypernymPart = line.substring(line.lastIndexOf("/") + 1, line.lastIndexOf(">"));

		return new NodeRelation(line.split("/")[4].split(">")[0].toLowerCase(),
				hypernymPart.substring(hypernymPart.lastIndexOf("#") + 1, hypernymPart.length()).toLowerCase(),
				knowledgeBase);
	}

	private static boolean IsType(String line) {
		String check = line.split(" ")[1];
		return check.substring(check.length() - 5, check.length() - 1).equals("type");
	}

}