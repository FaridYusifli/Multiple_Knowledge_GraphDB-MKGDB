package mkgdb.graph.extractors;

import java.nio.charset.Charset;

import mkgdb.graph.NodeRelation;

public class WebIsAGraphExtractor extends LineExtractor {

	private String knowledgeBase;

	public WebIsAGraphExtractor(String filePath, Charset charset, String knowledgeBase) {
		super(filePath, charset);
		this.knowledgeBase = knowledgeBase;
	}

	@Override
	public NodeRelation parse(String line) {
		String[] parts = line.split("~#@~");
		if (parts.length != 2)
			return null;

		return new NodeRelation(parts[0], parts[1], knowledgeBase);
	}

}