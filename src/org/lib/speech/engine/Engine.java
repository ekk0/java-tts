package org.lib.speech.engine;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.lib.speech.analysis.AnalyzeCenter;
import org.lib.speech.index.IndexManager;

public interface Engine {

	public void getPronounces(String sentences);

	public void getPronounces(File file, boolean isPrintDocument)
			throws IOException;

	public void getPronouncesFile(String sentences, File targetFile)
			throws IOException;

	public void getPronouncesFile(File sourceDocumentFile, File targetFile)
			throws IOException;

	public List<Object[]> getPronounceElements(String sentences);

	public AnalyzeCenter getAnalCen();

	public void setAnalCen(AnalyzeCenter analCen);

	public IndexManager getIndexManager();

	public void setIndexManager(IndexManager indexManager);
}
