package mkgdb.graph.extractors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.function.Function;
import mkgdb.graph.IExtractor;
import mkgdb.graph.IRelationCollector;
import mkgdb.graph.NodeRelation;

public abstract class LineExtractor implements IExtractor {
	private String filePath;
	private Charset charset;
	protected Function<String, String> nameFixer;

	public LineExtractor(final String filePath, final Charset charset) {
		this.filePath = filePath;
		this.charset = charset;
	}

	public void extractTo(IRelationCollector collector) throws IOException {
		File file = new File(filePath);
		if (!file.exists())
			return;
		if (nameFixer == null)
			nameFixer = (input) -> input.toLowerCase();//trim().

		BufferedReader bufferedReader = Files.newBufferedReader(file.toPath(), charset);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			NodeRelation nodeRelation = parse(line);
			if (nodeRelation != null)
				collector.add(nodeRelation);
		}
		bufferedReader.close();
	}

	public IExtractor setNameFixer(Function<String, String> nameFixer) {
		this.nameFixer = nameFixer;
		return this;
	}

	public abstract NodeRelation parse(String line);
}
