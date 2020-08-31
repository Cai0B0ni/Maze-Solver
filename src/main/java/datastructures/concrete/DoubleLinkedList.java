package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        Node<T> newNode = new Node<T>(item);
        if (front == null){ //list is empty
            front = newNode;
            back = newNode;
        } else { //list is not empty
            Node<T> current = back;
            current.next = newNode;
            newNode.prev = back;
            back = newNode;
        }
        size++;
    }

    @Override
    public T remove() {
        if (size == 0){// list is empty
            throw new EmptyContainerException();
        }
        if (front.next == null){//list.size == 1) {// if there is only 1 object in the linkedlist
            front = null;
        }
        Node<T> n = back;

        if (size ==1){

            front = null;

            size--;
            back = null;
            return n.data;
        }
        back = back.prev;
        back.next= null;

        size--;
        return n.data;
    }

    @Override
    public T get(int index) {
        if (front == null){// list is empty
            throw new EmptyContainerException();
        }
        else if (index < 0 || index >= this.size()){// index is less than 0 or greater than the size
            throw new IndexOutOfBoundsException();
        }

        Node<T> current = front;
        for (int i = 0; i < index; i++){
            current = current.next;
        }
        return current.data;
    }

    @Override
    public void set(int index, T item) {
        if (front == null){// list is empty
            throw new EmptyContainerException();
        }
        if (index < 0 || index >= this.size()){// index is less than 0 or greater than the size
            throw new IndexOutOfBoundsException();
        }
        Node<T> newNode = new Node<T>(item);
        Node<T> current = front;
        if (size == 1){
            front = newNode;
            back = newNode;
            return;
        }
        if (index == 0){
            Node<T> after = front.next;
            front = newNode;
            front.next = after;
            after.prev = newNode;
            current.next = null;
            return;

        }

        Node<T> previous = front;
        for (int i = 0; i < index; i++){
            previous = current;
            current = current.next;
        }
        if (index == size - 1){
            previous.next = newNode;
            newNode.prev = previous;
            back = newNode;
        }else {
            Node<T> after = current.next;
            newNode.prev = current.prev;
            newNode.next = after;
            current.next = newNode;
            previous.next = newNode;
            after.prev = newNode;
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || size() < index) {// index is less than 0 or greater than the size
            throw new IndexOutOfBoundsException();
        }
        Node<T> x = new Node<>(item);

        if (size() == 0) {
            front = x;
            back = x;
            size++;
            return;
        }
        if (index == 0) {
            x.next = front;
            front.prev = x;
            front = x;

        } else if (index == size) {
            Node<T> b = back;
            x.prev = b;
            b.next = x;
            back = x;
        } else {

            if (index > size/2){

                Node<T> current = back;


                for (int i = 0; i < size - index; i++) {
                    current = current.prev;
                }
                Node<T> after = current.next;
                x.prev = current;
                x.next = after;
                after.prev = x;
                current.next = x;
            }else {
                Node<T> current = front;

                for (int i = 0; i < index; i++) {
                    current = current.next;
                }
                Node<T> previous = current.prev;
                x.next = current;
                x.prev = previous;
                previous.next = x;
                current.prev = x;
            }

        }
        size++;
    }

    @Override
    public T delete(int index) {

        if (index < 0 || index >= this.size()){// index is less than 0 or greater than the size
            throw new IndexOutOfBoundsException();
        }
        Node<T> deleted = front;
        if (index == 0){
            //set deleted if this happens
            deleted = front;
            if (this.size == 1){
                front = null;
                back = null;
            }else {
                Node<T> a = front.next;
                a.prev = null;
                front = a;
            }
        } else if (index == size-1){  //index given is greater than 0 and 1 less than size
            deleted = back;
            Node<T> p = back.prev;
            p.next = null;
            back = p;
        } else {
            Node<T> current = front;
            for (int i = 0; i < index - 1; i++){
                current = current.next;
            }
            deleted = current.next;
            if (index < size) {
                current.next = current.next.next;
                Node<T> after = deleted.next;
                after.prev = current;
            } else { //index is size - 1
                current.next = null;
            }
        }
        size--;
        return deleted.data;
    }

    @Override
    public int indexOf(T item) {

        Node<T> current = front;
        int count = 0;

        for (int i = 0; i < size; i++){
            if (item == null || current.data == null){
                if (current.data == item) {
                    return count;
                }
            }else {
                if (current.data.equals(item)) {
                    return count;
                }
            }
                current = current.next;
                count++;

        }
        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
        if (front == null){// list is empty
            throw new EmptyContainerException();
        }
        return indexOf(other) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node<T> data = current;
            current = current.next;
            return data.data;
        }
    }
}
