package com.chensoul.core.util.tree.parser;

import com.chensoul.core.util.tree.Tree;

/**
 * 树节点解析器 可以参考{@link DefaultNodeParser}
 *
 * @param <T> 转换的实体 为数据源里的对象类型
 * @author liangbaikai
 */
@FunctionalInterface
public interface NodeParser<T, E> {

    /**
     * @param object   源数据实体
     * @param treeNode 树节点实体
     */
    void parse(T object, Tree<E> treeNode);
}
