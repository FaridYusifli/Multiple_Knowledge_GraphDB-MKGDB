package app;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import mkgdb.graph.IExtractor;
import mkgdb.graph.IRelationCollector;
import mkgdb.graph.builders.Neo4jBatchGraphBuilder;
import mkgdb.graph.collectors.FileCollector;
import mkgdb.graph.extractors.ConceptNetExtractor;
import mkgdb.graph.extractors.DBPediaInstancesExtractor;
import mkgdb.graph.extractors.DBPediaOntologyExtractor;
import mkgdb.graph.extractors.WebIsAGraphExtractor;
import mkgdb.graph.extractors.WikiCategoryExtractor;
import mkgdb.graph.extractors.WordNetExtractor;

public class App {
	private static Properties properties = new Properties();
	static {
		try {
			FileInputStream in = new FileInputStream("config.properties");
			properties.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static void main(String[] args) throws Exception {
		Stopwatch timer = Stopwatch.createUnstarted();
		timer.start();
		Charset charSetISO = StandardCharsets.ISO_8859_1;
		List<IExtractor> extractors = new ArrayList<>();

		extractors.add(new WikiCategoryExtractor(getProperty("WikiCategory"), charSetISO, "WikiCategory"));
		extractors.add(new ConceptNetExtractor(getProperty("ConceptNet"), charSetISO, "ConceptNet"));
		extractors.add(new DBPediaInstancesExtractor(getProperty("DBPediaInstances"), charSetISO, "DBPediaInstances"));
		extractors.add(new DBPediaOntologyExtractor(getProperty("DBPediaOntology"), charSetISO, "DBPediaOntology"));
		extractors.add(new WebIsAGraphExtractor(getProperty("WebIsAGraph"), charSetISO, "WebIsAGraph"));
		extractors.add(new WordNetExtractor(getProperty("WordNet"), "WordNet"));

		IRelationCollector collector = new FileCollector(getProperty("node"), getProperty("edge"), charSetISO, 40);
		collector.open();
		for (IExtractor extractor : extractors)
			extractor.extractTo(collector);
		collector.close();
		timer.stop();
		System.out.println("Extraction: " + timer.elapsed(TimeUnit.MILLISECONDS));

		timer.reset();
		timer.start();
		Neo4jBatchGraphBuilder builder = new Neo4jBatchGraphBuilder(getProperty("neo4jURL"), "neo4j",
				getProperty("neo4jPassword"));
		builder.run(getProperty("node"), getProperty("edge"), charSetISO, 1000);
		timer.stop();
		System.out.println("Graph Build: " + timer.elapsed(TimeUnit.MILLISECONDS));
		System.in.read();
	}
}