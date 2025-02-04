package com.gmail.at.ankyhe.wdl.parser.util;

import java.util.Collection;
import java.util.List;

/**
 * It's called as StreamUtils not CollectionUtils due to CollectionUtils is widely used name in apache, spring, guava.
 */
public final class StreamUtils {

    private StreamUtils() {}

    /**
     * It retrieves elements of type from collection.
     *
     * @param collection The collection to retrieve elements from.
     * @param type The type of retrieved elements.
     * @return {@link List} of elements with type.
     */
    public static <E, T extends E> List<T> getElementsOfTypeFromCollections(final Collection<E> collection, Class<T> type) {
        if (collection == null) {
            return null;
        }
        if (collection.isEmpty()) {
            return List.of();
        }

        return collection.stream().filter(type::isInstance).map(type::cast).toList();
    }
}
