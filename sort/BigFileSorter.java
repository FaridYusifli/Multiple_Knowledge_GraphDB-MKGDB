package sort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BigFileSorter {

	public static void sort(final String inputFilePath, final String outputFilePath, final int noOfSplits,
			final Charset charset, boolean removeDuplicates) throws IOException {

		List<File> files = splitAndSort(inputFilePath, noOfSplits, charset, removeDuplicates);
		if (removeDuplicates) {
			File mergeFile = File.createTempFile("merged", null);
			mergeSortedFiles(files, mergeFile.getAbsolutePath(), charset);
			removeDuplicateLines(mergeFile.getAbsolutePath(), outputFilePath, charset);
			mergeFile.delete();
		} else
			mergeSortedFiles(files, outputFilePath, charset);

		for (File file : files)
			file.delete();
	}

	private static List<File> splitAndSort(String inputFilePath, int noOfSplits, Charset charset,
			boolean removeDuplicates) throws IOException {
		final File inputFile = new File(inputFilePath);
		String prefix = inputFile.getName();

		long totalCount = Files.lines(inputFile.toPath(), charset).count();
		final long linesPerSplit = totalCount / noOfSplits;
		final BufferedReader br = Files.newBufferedReader(inputFile.toPath(), charset);

		final List<File> files = new ArrayList<>();

		for (int i = 1; i <= noOfSplits; i++) {
			File file = File.createTempFile(prefix, null);
			final BufferedWriter buffWriter = Files.newBufferedWriter(file.toPath(), charset);

			long j = 0;
			String line;
			while (j < linesPerSplit) {
				j++;
				line = br.readLine();
				if (line == null)
					break;
				buffWriter.write(line);
				buffWriter.newLine();
			}
			buffWriter.flush();
			buffWriter.close();

			file = sortFileContent(file, removeDuplicates);
			files.add(file);
		}
		File file = File.createTempFile(prefix, null);
		final BufferedWriter buffWriter = Files.newBufferedWriter(file.toPath(), charset);
		boolean hasRemaining = false;

		while (true) {
			final String line = br.readLine();
			if (line == null)
				break;
			else {
				hasRemaining = true;
				buffWriter.write(line);
				buffWriter.newLine();
			}
		}
		buffWriter.flush();
		buffWriter.close();

		if (hasRemaining) {
			file = sortFileContent(file, removeDuplicates);
			files.add(file);
		} else
			file.delete();
		return files;
	}

	private static File sortFileContent(final File file, boolean removeDuplicates) throws IOException {
		List<String> lines;

		try (Stream<String> ln = Files.lines(file.toPath())) {
			lines = ln.collect(Collectors.toList());
		}

		Collections.sort(lines);
		try (BufferedWriter bw = Files.newBufferedWriter(file.toPath())) {
			String previousLine = null;
			for (final String line : lines) {
				if (removeDuplicates) {
					if (line.equals(previousLine))
						continue;
					previousLine = line;
				}
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		return file;
	}

	private static void mergeSortedFiles(final List<File> files, final String outputFilePath, Charset charset)
			throws IOException {
		final List<BufferedReader> brReaders = new ArrayList<>();
		final PriorityQueue<FileLine> queue = new PriorityQueue<FileLine>(files.size(), new Comparator<FileLine>() {
			@Override
			public int compare(final FileLine s1, final FileLine s2) {
				return s1.line.compareToIgnoreCase(s2.line);
			}
		});

		final File outputFile = new File(outputFilePath);
		final BufferedWriter bw = Files.newBufferedWriter(outputFile.toPath(), charset);
		try {
			for (final File file : files) {
				final BufferedReader br = Files.newBufferedReader(file.toPath(), charset);
				brReaders.add(br);
				final String line = br.readLine();
				final FileLine fileLine = new FileLine();
				fileLine.setLine(line);
				fileLine.setReader(br);
				queue.add(fileLine);
			}
			while (!queue.isEmpty()) {
				final FileLine fileLine = queue.remove();

				bw.write(fileLine.line);
				bw.newLine();
				final String line = fileLine.getReader().readLine();
				if (line != null) {
					fileLine.setLine(line);
					queue.add(fileLine);
				}
			}
		} finally {
			if (brReaders != null) {
				for (final BufferedReader br : brReaders) {
					br.close();
				}
			}
			if (bw != null) {
				bw.flush();
				bw.close();
			}
		}
	}

	private static void removeDuplicateLines(String inputFileName, String outputFileName, Charset charset)
			throws IOException {
		BufferedReader reader = Files.newBufferedReader(new File(inputFileName).toPath(), charset);
		BufferedWriter writer = Files.newBufferedWriter(new File(outputFileName).toPath(), charset);

		String prevLine = reader.readLine();
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			if (!currentLine.equals(prevLine)) {
				writer.write(prevLine);
				writer.newLine();
				prevLine = currentLine;
			}
		}

		writer.write(prevLine);

		reader.close();
		writer.close();
	}
}