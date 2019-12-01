package mkgdb.graph.extractors;

import java.util.function.Function;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import mkgdb.graph.IExtractor;
import mkgdb.graph.IRelationCollector;
import mkgdb.graph.NodeRelation;

public class WordNetExtractor implements IExtractor {

	protected Function<String, String> nameFixer;
	private String wordNetPath;
	private String knowledgeBase;

	public WordNetExtractor(String wordNetPath, String knowledgeBase) {
		this.wordNetPath = wordNetPath;
		this.knowledgeBase = knowledgeBase;
	}

	@Override
	public void extractTo(IRelationCollector collector) throws IOException {
		if (nameFixer == null)
			nameFixer = (input) -> input.toLowerCase();

		String hypernym, hyponym;
		URL url = new URL("file", null, wordNetPath);
		IDictionary dict = new edu.mit.jwi.Dictionary(url);
		dict.open();
		Iterator<IIndexWord> wordIterator = dict.getIndexWordIterator(POS.NOUN);
		IIndexWord fullWordID = null;
		List<IWord> words;
		List<ISynsetID> synset_hyponyms;
		while (wordIterator.hasNext()) {
			fullWordID = wordIterator.next();
			for (IWordID wordId : fullWordID.getWordIDs()) {
				hypernym = nameFixer.apply(wordId.getLemma());
				synset_hyponyms = dict.getWord(wordId).getSynset().getRelatedSynsets(Pointer.HYPONYM);
				for (ISynsetID sid : synset_hyponyms) {
					words = dict.getSynset(sid).getWords();
					for (IWord hyponymWord : words) {
						hyponym = nameFixer.apply(hyponymWord.getLemma());
						collector.add(new NodeRelation(hyponym, hypernym, knowledgeBase));
					}
				}
			}
		}
		dict.close();
	}

	@Override
	public IExtractor setNameFixer(Function<String, String> nameFixer) {
		this.nameFixer = nameFixer;
		return this;
	}
}