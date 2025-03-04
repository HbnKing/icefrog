package com.whaleal.icefrog.extra.tokenizer.engine.hanlp;

import com.hankcs.hanlp.seg.common.Term;

import com.whaleal.icefrog.extra.tokenizer.Word;

/**
 * HanLP分词中的一个单词包装
 *
 * @author Looly
 * @author wh
 *
 */
public class HanLPWord implements Word {
	private static final long serialVersionUID = 1L;

	private final Term term;

	/**
	 * 构造
	 *
	 * @param term {@link Term}
	 */
	public HanLPWord(Term term) {
		this.term = term;
	}

	@Override
	public String getText() {
		return term.word;
	}

	@Override
	public int getStartOffset() {
		return this.term.offset;
	}

	@Override
	public int getEndOffset() {
		return getStartOffset() + this.term.length();
	}

	@Override
	public String toString() {
		return getText();
	}
}
