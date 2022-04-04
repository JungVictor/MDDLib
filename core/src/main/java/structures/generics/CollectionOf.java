package structures.generics;

import memory.Freeable;

public interface CollectionOf<T> extends Iterable<T>, Freeable {
    boolean add(T object);
    void add(Iterable<T> objects);
    void clear();
}
