package mkgdb.graph.collectors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import mkgdb.graph.IRelationCollector;
import mkgdb.graph.NodeRelation;
import sort.BigFileSorter;

public class FileCollector implements IRelationCollector {

	private String outputNodeFilePath;
	private String outputRelationFilePath;
	private Charset charset;
	private File tempNodeFile;
	private File tempRelationFile;
	private BufferedWriter tempNodeFileWriter;
	private BufferedWriter tempRelationFileWriter;
	private boolean isOpen;
	protected boolean collectNodes;
	protected boolean collectRelations;
	private String previousHyponym;
	private String previousHypernym;
	private Integer numberOfSplittedFile = null;

	public FileCollector(String outputNodeFilePath, String outputRelationFilePath, Charset charset, int fileNumber) {
		this.outputNodeFilePath = outputNodeFilePath;
		this.outputRelationFilePath = outputRelationFilePath;
		this.charset = charset;
		this.collectNodes = outputNodeFilePath != null && !outputNodeFilePath.equals("");
		this.collectRelations = outputRelationFilePath != null && !outputRelationFilePath.equals("");
		this.numberOfSplittedFile = new Integer(fileNumber);
	}

	@Override
	public void add(NodeRelation nodeRelation) {
		if (collectNodes)
			writeNode(nodeRelation);
		if (collectRelations)
			writeRelation(nodeRelation);
	}

	@Override
	public void add(Iterable<NodeRelation> nodeRelations) {
		for (NodeRelation nodeRelation : nodeRelations)
			add(nodeRelation);
	}

	@Override
	public boolean open() {
		if (isOpen)
			return false;
		handleTempFiles(true, true);

		try {
			if (collectNodes) {
				tempNodeFile = File.createTempFile("nodes_", null);
				tempNodeFile.deleteOnExit();
				tempNodeFileWriter = Files.newBufferedWriter(tempNodeFile.toPath(), charset);

				previousHypernym = null;
				previousHyponym = null;
			}

			if (collectRelations) {
				tempRelationFile = File.createTempFile("relations_", null);
				tempRelationFile.deleteOnExit();
				tempRelationFileWriter = Files.newBufferedWriter(tempRelationFile.toPath(), charset);
			}

		} catch (IOException ex) {
			handleTempFiles(true, true);
			return false;
		}

		isOpen = true;
		return true;
	}

	@Override
	public void close() {
		if (!isOpen)
			return;

		handleTempFiles(true, false);

		try {
			BigFileSorter.sort(tempNodeFile.getAbsolutePath(), outputNodeFilePath, numberOfSplittedFile.intValue(),
					charset, true);
			BigFileSorter.sort(tempRelationFile.getAbsolutePath(), outputRelationFilePath,
					numberOfSplittedFile.intValue(), charset, true);
		} catch (IOException e) {
		}

		handleTempFiles(false, true);
		isOpen = false;
	}

	private void handleTempFiles(boolean closeWriters, boolean deleteFiles) {
		try {
			if (closeWriters || deleteFiles) {
				if (tempNodeFileWriter != null) {
					tempNodeFileWriter.flush();
					tempNodeFileWriter.close();
					tempNodeFileWriter = null;

				}

				if (tempRelationFileWriter != null) {
					tempRelationFileWriter.flush();
					tempRelationFileWriter.close();
					tempRelationFileWriter = null;
				}
			}

			if (deleteFiles) {
				if (tempNodeFile.exists())
					tempNodeFile.delete();

				if (tempRelationFile.exists())
					tempRelationFile.delete();
			}

		} catch (Exception e) {

		}
	}

	private void writeRelation(NodeRelation nodeRelation) {
		try {
			tempRelationFileWriter.write(nodeRelation.getHypernym() + "~#@~" + nodeRelation.getHyponym() + "<=#@@]>"
					+ nodeRelation.getKnowledgeBase());
			tempRelationFileWriter.newLine();
		} catch (IOException e) {
		}
	}

	private void writeNode(NodeRelation nodeRelation) {
		try {
			if (!nodeRelation.getHyponym().equals(previousHyponym)) {
				previousHyponym = nodeRelation.getHyponym();
				tempNodeFileWriter.write(nodeRelation.getHyponym() + "~#@~" + nodeRelation.getKnowledgeBase());
				tempNodeFileWriter.newLine();
			}

			if (!nodeRelation.getHypernym().equals(previousHypernym)) {
				previousHypernym = nodeRelation.getHypernym();
				tempNodeFileWriter.write(nodeRelation.getHypernym() + "~#@~" + nodeRelation.getKnowledgeBase());
				tempNodeFileWriter.newLine();
			}
		} catch (IOException ie) {

		}
	}

}