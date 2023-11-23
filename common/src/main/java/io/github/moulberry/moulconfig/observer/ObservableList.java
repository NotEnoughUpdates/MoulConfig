package io.github.moulberry.moulconfig.observer;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A list which allows for notifying a single observer on state change.
 *
 * @param <T> the type of elements of this list.
 */
@Data
public class ObservableList<T> implements List<T> {
    /**
     * The list which actually holds the contents. Should not be accessed directly and no reference to this list should be retained when creating an {@link ObservableList}
     */
    final List<T> delegate;
    /**
     * The observer which will be called when a mutating method is called on this list.
     */
    Runnable observer;

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean a = delegate.add(t);
        if (a)
            update();
        return a;
    }

    /**
     * Explicitly notify the current observer of an update.
     */
    public void update() {
        if (observer != null)
            observer.run();
    }

    @Override
    public boolean remove(Object o) {
        boolean a = delegate.remove(o);
        if (a)
            update();
        return a;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        boolean a = delegate.addAll(c);
        if (a) update();
        return a;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        boolean b = delegate.addAll(index, c);
        if (b) update();
        return b;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean b = delegate.removeAll(c);
        if (b) update();
        return b;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean b = delegate.retainAll(c);
        if (b) update();
        return b;
    }

    @Override
    public void clear() {
        delegate.clear();
        update();
    }

    @Override
    public T get(int index) {
        return delegate.get(index);
    }

    @Override
    public T set(int index, T element) {
        T set = delegate.set(index, element);
        update();
        return set;
    }

    @Override
    public void add(int index, T element) {
        delegate.add(index, element);
        update();
    }

    @Override
    public T remove(int index) {
        T remove = delegate.remove(index);
        update();
        return remove;
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

}
