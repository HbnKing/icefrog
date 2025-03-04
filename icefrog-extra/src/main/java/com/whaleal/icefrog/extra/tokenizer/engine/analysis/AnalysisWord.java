package com.whaleal.icefrog.extra.tokenizer.engine.analysis;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.Attribute;

import com.whaleal.icefrog.extra.tokenizer.Word;

/**
 * Lucene-analysis分词中的一个单词包装
 *
 * @author Looly
 * @author wh
 *
 */
public class AnalysisWord implements Word {
	private static final long serialVersionUID = 1L;

	private final Attribute word;

	/**
	 * 构造
	 *
	 * @param word {@link CharTermAttribute}
	 */
	public AnalysisWord(CharTermAttribute word) {
		this.word = word;
	}

	@Override
	public String getText() {
		return word.toString();
	}

	@Override
	public int getStartOffset() {
		if(this.word instanceof OffsetAttribute) {
			return ((OffsetAttribute)this.word).startOffset();
		}
		return -1;
	}

	@Override
	public int getEndOffset() {
		if(this.word instanceof OffsetAttribute) {
			return ((OffsetAttribute)this.word).endOffset();
		}
		return -1;
	}

	@Override
	public String toString() {
		return getText();
	}
}
