package com.fun.hunt;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.util.Version;

// RC-26942
public class AnalyserImpl extends Analyzer {
	private Version version;

	public AnalyserImpl(Version version) {
		this.version = version;
	}

	/*
	 * @Override Upgrade to 5.3.1 Lucene protected TokenStreamComponents
	 * createComponents(String fieldName, Reader reader) { Tokenizer source =
	 * new KeywordTokenizer(reader); TokenStream filter = new
	 * LowerCaseFilter(version, source); return new
	 * TokenStreamComponents(source, filter); }
	 */

	@Override
	protected TokenStreamComponents createComponents(String paramString) {
		Tokenizer source = new KeywordTokenizer();
		TokenStream filter = new LowerCaseFilter(source);
		return new TokenStreamComponents(source, filter);
	}
}
