package com.whaleal.icefrog.extra.tokenizer.engine.hanlp;

import java.util.Iterator;
import java.util.List;

import com.hankcs.hanlp.seg.common.Term;

import com.whaleal.icefrog.extra.tokenizer.Result;
import com.whaleal.icefrog.extra.tokenizer.Word;

/**
 * HanLP分词结果实现<br>
 * 项目地址：https://github.com/hankcs/HanLP
 *
 * @author Looly
 * @author wh
 *
 */
public class HanLPResult implements Result {

	Iterator<Term> result;

	public HanLPResult(List<Term> termList) {
		this.result = termList.iterator();
	}

	@Override
	public boolean hasNext() {
		return result.hasNext();
	}

	@Override
	public Word next() {
		return new HanLPWord(result.next());
	}

	@Override
	public void remove() {
		result.remove();
	}

	@Override
	public Iterator<Word> iterator() {
		return this;
	}


}
