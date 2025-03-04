package com.whaleal.icefrog.extra.tokenizer.engine.jieba;

import com.huaban.analysis.jieba.SegToken;

import com.whaleal.icefrog.extra.tokenizer.Word;

/**
 * Jieba分词中的一个单词包装
 *
 * @author Looly
 * @author wh
 *
 */
public class JiebaWord implements Word {
	private static final long serialVersionUID = 1L;

	private final SegToken segToken;

	/**
	 * 构造
	 *
	 * @param segToken {@link SegToken}
	 */
	public JiebaWord(SegToken segToken) {
		this.segToken = segToken;
	}

	@Override
	public String getText() {
		return segToken.word.toString();
	}

	@Override
	public int getStartOffset() {
		return segToken.startOffset;
	}

	@Override
	public int getEndOffset() {
		return segToken.endOffset;
	}

	@Override
	public String toString() {
		return getText();
	}
}
