package com.seregamorph.restapi.utils;

/**
 * A mapper from a source object to a target object that will be used as the pruning result.
 * There are cases where we need this mapper, e.g. the case of composite id. Let's say there's a map
 * from <code>BookAuthor</code>(<code>id</code> = <code>BookAuthorId</code> containing <code>bookId</code>
 * and <code>authorId</code>) to <code>BookAuthorResource</code>(<code>id</code> = <code>BookAuthorIdResource</code>
 * containing <code>bookId</code> and <code>authorId</code>). If we do need to prune <code>BookAuthorResource</code>,
 * then copying from <code>BookAuthor.id</code> to <code>BookAuthorResource.id</code> is not possible because of
 * type incompatibility.
 */
@FunctionalInterface
public interface RecursionPruner {

    <T> T map(Object source, Class<T> targetType);

}
