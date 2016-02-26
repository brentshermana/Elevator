import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.lang.IllegalStateException;

public class Queue<E> {
    public LinkedList<E> list;

    public Queue () {
        list = new LinkedList<E>();
    }


    public boolean isEmpty () {
        return list.size() == 0;
    }

    public int size () {
        return list.size();
    }

    public boolean add (E e) throws IllegalStateException {
        try {
            list.addFirst(e);
        } catch (final Exception ex) {
            throw new IllegalStateException ();
        }
        return true;
    }

    public E remove () throws NoSuchElementException {
        if (this.isEmpty()) {
            throw new NoSuchElementException ();
        }
        E element = list.removeLast();
        return element;
    }

    public E peek () {
        if (isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    public E element () throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException ();
        } else {
            return list.get(list.size() - 1);
        }
    }
}
