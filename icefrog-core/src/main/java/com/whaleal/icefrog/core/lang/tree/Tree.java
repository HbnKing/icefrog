package com.whaleal.icefrog.core.lang.tree;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.lang.Preconditions;
import com.whaleal.icefrog.core.util.ArrayUtil;
import com.whaleal.icefrog.core.util.CharUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.util.StrUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 通过转换器将你的实体转化为TreeNodeMap节点实体 属性都存在此处,属性有序，可支持排序
 *
 * @param <T> ID类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class Tree<T> extends LinkedHashMap<String, Object> implements Node<T> {
	private static final long serialVersionUID = 1L;

	private final TreeNodeConfig treeNodeConfig;
	private Tree<T> parent;

	public Tree() {
		this(null);
	}

	/**
	 * 构造
	 *
	 * @param treeNodeConfig TreeNode配置
	 */
	public Tree(TreeNodeConfig treeNodeConfig) {
		this.treeNodeConfig = ObjectUtil.defaultIfNull(
				treeNodeConfig, TreeNodeConfig.DEFAULT_CONFIG);
	}

	/**
	 * 打印
	 *
	 * @param tree   树
	 * @param writer Writer
	 * @param intent 缩进量
	 */
	private static void printTree(Tree<?> tree, PrintWriter writer, int intent) {
		writer.println(StrUtil.format("{}{}[{}]", StrUtil.repeat(CharUtil.SPACE, intent), tree.getName(), tree.getId()));
		writer.flush();

		final List<? extends Tree<?>> children = tree.getChildren();
		if (CollUtil.isNotEmpty(children)) {
			for (Tree<?> child : children) {
				printTree(child, writer, intent + 2);
			}
		}
	}

	/**
	 * 获取节点配置
	 *
	 * @return 节点配置
	 * @since 1.0.0
	 */
	public TreeNodeConfig getConfig() {
		return this.treeNodeConfig;
	}

	/**
	 * 获取父节点
	 *
	 * @return 父节点
	 * @since 1.0.0
	 */
	public Tree<T> getParent() {
		return parent;
	}

	/**
	 * 设置父节点
	 *
	 * @param parent 父节点
	 * @return this
	 * @since 1.0.0
	 */
	public Tree<T> setParent(Tree<T> parent) {
		this.parent = parent;
		if (null != parent) {
			this.setParentId(parent.getId());
		}
		return this;
	}

	/**
	 * 获取ID对应的节点，如果有多个ID相同的节点，只返回第一个。<br>
	 * 此方法只查找此节点及子节点，采用广度优先遍历。
	 *
	 * @param id ID
	 * @return 节点
	 * @since 1.0.0
	 */
	public Tree<T> getNode(T id) {
		return TreeUtil.getNode(this, id);
	}

	/**
	 * 获取所有父节点名称列表
	 *
	 * <p>
	 * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
	 * 返回结果就是：[研发一部, 研发中心, 技术中心]
	 *
	 * @param id                 节点ID
	 * @param includeCurrentNode 是否包含当前节点的名称
	 * @return 所有父节点名称列表
	 * @since 1.0.0
	 */
	public List<CharSequence> getParentsName(T id, boolean includeCurrentNode) {
		return TreeUtil.getParentsName(getNode(id), includeCurrentNode);
	}

	/**
	 * 获取所有父节点名称列表
	 *
	 * <p>
	 * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
	 * 返回结果就是：[研发一部, 研发中心, 技术中心]
	 *
	 * @param includeCurrentNode 是否包含当前节点的名称
	 * @return 所有父节点名称列表
	 * @since 1.0.0
	 */
	public List<CharSequence> getParentsName(boolean includeCurrentNode) {
		return TreeUtil.getParentsName(this, includeCurrentNode);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getId() {
		return (T) this.get(treeNodeConfig.getIdKey());
	}

	@Override
	public Tree<T> setId(T id) {
		this.put(treeNodeConfig.getIdKey(), id);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getParentId() {
		return (T) this.get(treeNodeConfig.getParentIdKey());
	}

	@Override
	public Tree<T> setParentId(T parentId) {
		this.put(treeNodeConfig.getParentIdKey(), parentId);
		return this;
	}

	@Override
	public CharSequence getName() {
		return (CharSequence) this.get(treeNodeConfig.getNameKey());
	}

	@Override
	public Tree<T> setName(CharSequence name) {
		this.put(treeNodeConfig.getNameKey(), name);
		return this;
	}

	@Override
	public Comparable<?> getWeight() {
		return (Comparable<?>) this.get(treeNodeConfig.getWeightKey());
	}

	@Override
	public Tree<T> setWeight(Comparable<?> weight) {
		this.put(treeNodeConfig.getWeightKey(), weight);
		return this;
	}

	/**
	 * 获取所有子节点
	 *
	 * @return 所有子节点
	 */
	@SuppressWarnings("unchecked")
	public List<Tree<T>> getChildren() {
		return (List<Tree<T>>) this.get(treeNodeConfig.getChildrenKey());
	}

	/**
	 * 设置子节点，设置后会覆盖所有原有子节点
	 *
	 * @param children 子节点列表
	 * @return this
	 */
	public Tree<T> setChildren(List<Tree<T>> children) {
		this.put(treeNodeConfig.getChildrenKey(), children);
		return this;
	}

	/**
	 * 增加子节点，同时关联子节点的父节点为当前节点
	 *
	 * @param children 子节点列表
	 * @return this
	 * @since 1.0.0
	 */
	@SafeVarargs
	public final Tree<T> addChildren(Tree<T>... children) {
		if (ArrayUtil.isNotEmpty(children)) {
			List<Tree<T>> childrenList = this.getChildren();
			if (null == childrenList) {
				childrenList = new ArrayList<>();
				setChildren(childrenList);
			}
			for (Tree<T> child : children) {
				child.setParent(this);
				childrenList.add(child);
			}
		}
		return this;
	}

	/**
	 * 扩展属性
	 *
	 * @param key   键
	 * @param value 扩展值
	 */
	public void putExtra(String key, Object value) {
		Preconditions.notEmpty(key, "Key must be not empty !");
		this.put(key, value);
	}

	@Override
	public String toString() {
		final StringWriter stringWriter = new StringWriter();
		printTree(this, new PrintWriter(stringWriter), 0);
		return stringWriter.toString();
	}
}
