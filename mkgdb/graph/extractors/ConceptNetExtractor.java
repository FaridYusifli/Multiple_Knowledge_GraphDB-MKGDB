package mkgdb.graph.extractors;

import java.nio.charset.Charset;

import mkgdb.graph.NodeRelation;

public class ConceptNetExtractor extends LineExtractor {
	private String knowledgeBase;

	public ConceptNetExtractor(String filePath, Charset charset, String knowledgeBase) {
		super(filePath, charset);
		this.knowledgeBase = knowledgeBase;
	}

	@Override
	public NodeRelation parse(String line) {
		String hyponym, hypernym;
		if (!(line.split("\\t")[0].split(",")[0].split("/")[4].equals("IsA"))
				|| !(line.split("\\t")[0].split(",")[1].startsWith("/c/en/")))
			return null;
		hyponym = line.split("\\t")[2].split("/")[3];
		hypernym = line.split("\\t")[3].split("/")[3];
		return new NodeRelation(hyponym, hypernym, knowledgeBase);
	}

}
