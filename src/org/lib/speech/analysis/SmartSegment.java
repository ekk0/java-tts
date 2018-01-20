package org.lib.speech.analysis;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.SmartChineseAnalyzer;

public abstract class SmartSegment implements AnalyzeCenter {

	public SmartSegment() {
		nt = new Token();
		ca = new SmartChineseAnalyzer(true);
	}

	public List<String> doSegment(String str) throws IOException {
		List<String> phrases = new LinkedList<String>();

		Reader sentence = new StringReader(str);
		TokenStream ts = ca.tokenStream("sentence", sentence);

		int beginIndex = -1, endIndex = -1;
		nt = ts.next(nt);
		if (nt != null) {
			endIndex = nt.startOffset(); // 如果句子开头由多个标点符号开始的，忽略这种情况的标记
			while (nt != null) {
				beginIndex = nt.startOffset();
				if (beginIndex > endIndex) {
					phrases.add(str.substring(endIndex, beginIndex));
				}
				phrases.add(nt.term());
				endIndex = nt.endOffset();
				nt = ts.next(nt);
			}
			ts.close();

			if (endIndex < str.length()) { // 处理句子最后标点
				phrases.add(str.substring(endIndex, str.length()));
			}
		}

		return phrases;
	}

	private Token nt;
	private Analyzer ca;

}
