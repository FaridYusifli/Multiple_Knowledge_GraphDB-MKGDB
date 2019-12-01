package mkgdb.graph.builders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Values;

public class Neo4jBatchGraphBuilder {
	public final Driver driver;
	private final SessionConfig writeConfig = SessionConfig.builder().withDefaultAccessMode(AccessMode.WRITE).build();

	public Neo4jBatchGraphBuilder(String uri, String username, String password) {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
	}

	public void run(String nodeFilePath, String relationFilePath, Charset charset, int batchSize) {
		buildNodes(nodeFilePath, charset, batchSize);
		buildRelations(relationFilePath, charset, batchSize);
	}

	public void finalize() {
		driver.close();
	}

	private void buildRelations(String relationFilePath, Charset charset, int batchSize) {
		File relationFile = new File(relationFilePath);
		if (!relationFile.exists())
			return;
		try {
			Multimap<String, String> termsMap = HashMultimap.create();

			BufferedReader bufferedReader = Files.newBufferedReader(relationFile.toPath(), charset);
			String line, terms, knowledgeBase;
			String previousTerm = null;
			String[] parts;

			while ((line = bufferedReader.readLine()) != null) {
				parts = line.split("<=#@@]>");
				terms = parts[0];
				knowledgeBase = parts[1];
				if (!terms.equals(previousTerm) && termsMap.keySet().size() > batchSize) {
					createRelations(termsMap);
					termsMap.clear();
				}
				termsMap.put(terms, knowledgeBase);
				previousTerm = terms;
			}
			bufferedReader.close();

			if (termsMap.keySet().size() > 0) {
				createRelations(termsMap);
				termsMap.clear();
			}
		} catch (IOException ex) {
		}
	}

	private void createRelations(Multimap<String, String> map) {
		try (Session edgeSession = driver.session(writeConfig)) {
			edgeSession.writeTransaction(new TransactionWork<Integer>() {
				@Override
				public Integer execute(Transaction tx) {
					String[] parts;
					String hypernym, hyponym, script;
					for (String name : map.keySet()) {
						parts = name.split("~#@~");
						if (parts.length >= 2) {
							hypernym = name.split("~#@~")[0];
							hyponym = name.split("~#@~")[1];
							script = "MATCH (hyponym:Term {name: $hypo_name}) MATCH (hypernym:Term {name: $hype_name}) CREATE (hyponym)<-[edge:IsA]-(hypernym) SET "
									+ map.get(name).stream().map((s) -> String.format("edge.%s='1' ", s))
											.collect(Collectors.joining(","));
							tx.run(script, Values.parameters("hypo_name", hyponym, "hype_name", hypernym));
						}
					}
					return 0;
				}
			});
		}
	}

	private void buildNodes(String nodeFilePath, Charset charset, int batchSize) {
		File nodeFile = new File(nodeFilePath);
		if (!nodeFile.exists())
			return;

		try {
			Multimap<String, String> termMap = HashMultimap.create();

			BufferedReader bufferedReader = Files.newBufferedReader(nodeFile.toPath(), charset);
			String line, term, knowledgeBase;
			String previousTerm = null;
			String[] parts;

			while ((line = bufferedReader.readLine()) != null) {
				parts = line.split("~#@~");
				term = parts[0];
				knowledgeBase = parts[1];
				if (!term.equals(previousTerm) && termMap.keySet().size() > batchSize) {
					createNodes(termMap);
					termMap.clear();
				}
				termMap.put(term, knowledgeBase);
				previousTerm = term;
			}
			bufferedReader.close();

			if (termMap.keySet().size() > 0) {
				createNodes(termMap);
				termMap.clear();
			}
			addIndex();
		} catch (IOException ex) {
		}
	}

	private void addIndex() {
		try (Session indexSession = driver.session(writeConfig)) {
			indexSession.writeTransaction(new TransactionWork<Integer>() {
				@Override
				public Integer execute(Transaction arg0) {
					arg0.run("CREATE INDEX ON :Term(name)");
					return 0;
				}
			});
		}
	}

	private void createNodes(Multimap<String, String> map) {
		try (Session nodeSession = driver.session(writeConfig)) {
			nodeSession.writeTransaction(new TransactionWork<Integer>() {
				@Override
				public Integer execute(Transaction tx) {
					for (String name : map.keySet()) {
						String script = "CREATE (n:Term {name:$name}) SET " + map.get(name).stream()
								.map((s) -> String.format("n.%s='1' ", s)).collect(Collectors.joining(","));
						tx.run(script, Values.parameters("name", name));
					}
					return 0;
				}
			});
		}
	}
}