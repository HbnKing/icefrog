package com.whaleal.icefrog.core.lang.tree;

import com.whaleal.icefrog.core.comparator.CompareUtil;

import java.io.Serializable;

/**
 * 节点接口，提供节点相关的的方法定义
 *
 * @param <T> ID类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public interface Node<T> extends Comparable<Node<T>>, Serializable {

	/**
	 * 获取ID
	 *
	 * @return ID
	 */
	T getId();

	/**
	 * 设置ID
	 *
	 * @param id ID
	 * @return this
	 */
	Node<T> setId(T id);

	/**
	 * 获取父节点ID
	 *
	 * @return 父节点ID
	 */
	T getParentId();

	/**
	 * 设置父节点ID
	 *
	 * @param parentId 父节点ID
	 * @return this
	 */
	Node<T> setParentId(T parentId);

	/**
	 * 获取节点标签名称
	 *
	 * @return 节点标签名称
	 */
	CharSequence getName();

	/**
	 * 设置节点标签名称
	 *
	 * @param name 节点标签名称
	 * @return this
	 */
	Node<T> setName(CharSequence name);

	/**
	 * 获取权重
	 *
	 * @return 权重
	 */
	Comparable<?> getWeight();

	/**
	 * 设置权重
	 *
	 * @param weight 权重
	 * @return this
	 */
	Node<T> setWeight(Comparable<?> weight);

	@SuppressWarnings({"unchecked", "rawtypes", "NullableProblems"})
	@Override
	default int compareTo(Node node) {
		if(null == node){
			return 1;
		}
		final Comparable weight = this.getWeight();
		final Comparable weightOther = node.getWeight();
		return CompareUtil.compare(weight, weightOther);
	}
}
