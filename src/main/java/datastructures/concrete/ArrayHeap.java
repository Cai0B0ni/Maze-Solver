package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;
import misc.exceptions.InvalidElementException;

/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int size;
    private int capacity;
    private IDictionary<T, Integer> dictionary1;

    //T is the actual data
    // integer is the index in the array


    // Feel free to add more fields and constants.

    public ArrayHeap() {
        capacity = 10;
        heap = makeArrayOfT(capacity);
        size = 0;
        dictionary1 = new ChainedHashDictionary<>();

        //throw new NotYetImplementedException();
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    /**
     * A method stub that you may replace with a helper method for percolating
     * upwards from a given index, if necessary.
     */
    private void percolateUp(int index) {
        int parent = (index - 1) / 4;
        while (heap[index].compareTo(heap[parent]) < 0){
            swap(parent, index);
            index = parent;
            parent = (index - 1) / 4;
            //swap parent and index
        }
    }

    /**
     * A method stub that you may replace with a helper method for percolating
     * downwards from a given index, if necessary.
     */
    private void percolateDown(int index) {
        if (index * 4  + 1 > size){
            return;
        }
        //maybe check index
        int childIndex = index * 4 + 1;
        int i = 1;
        if (heap[4 * index + 1] != null) {
            while (heap[4 * index + i] != null && i < 5) {
                if (heap[index * 4 + i].compareTo(heap[childIndex]) < 0) {
                    childIndex = index * 4 + i;
                }
                i++;
            }
            if (heap[index].compareTo(heap[childIndex]) > 0){
                swap(childIndex, index);
                percolateDown(childIndex);
            }
        }
        //throw new NotYetImplementedException();
    }

    /**
     * A method stub that you may replace with a helper method for determining
     * which direction an index needs to percolate and percolating accordingly.
     */
    private void percolate(int index) {
        if (index == 0){
            percolateDown(index);
        } else {
            int parent = (index - 1) / 4;
            if (heap[index].compareTo(heap[parent]) < 0) {
                percolateUp(index);
            } else if (heap[index].compareTo(heap[parent]) > 0) {
                percolateDown(index);
            }
        }
    }

    /**
     * A method stub that you may replace with a helper method for swapping
     * the elements at two indices in the 'heap' array.
     */
    private void swap(int a, int b) {
        T key1 = heap[a];
        T key2 = heap[b];
        //test for the lack of these two lines through methods that use the swap method
        dictionary1.put(key1, b);
        dictionary1.put(key2, a);
        T temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;
    }

    @Override
    public T removeMin() {
        if (size == 0) {
            throw new EmptyContainerException();
        }
        T removed = heap[0];
        //use swap here so changes are reflected in the dictionary
        heap[0] = heap[size - 1];
        dictionary1.remove(removed); // how to remove key from hASG
        heap[size-1] = null;
        //maybe use percolate instead of percolatedown
        if (size > 1) {
            percolateDown(0);
        }
        size--;
        return removed;
    }

    @Override
    public T peekMin() {
        if (size == 0) {
            throw new EmptyContainerException("");
        }
        return heap[0]; // may be heap[1]
    }

    @Override
    public void add(T item) {
        //check for null item
        if (item == null){
            throw new IllegalArgumentException();
        //check if item is already inside
        } else if (contains(item)){
            throw new InvalidElementException();
        }
        if (size == capacity){
            capacity = capacity * 2;
            T[] newHeap = makeArrayOfT(capacity);
            for (int i = 0; i < heap.length; i++){
                newHeap[i] = heap[i];
            }
            heap = newHeap;
        }
        heap[size] = item;
        // add value into dictionary
        dictionary1.put(item, size);
        percolate(size);
        size++;

        // throw new NotYetImplementedException();
    }

    @Override
    public boolean contains(T item) {
        if (item == null){
            throw new IllegalArgumentException();
        }
        return dictionary1.containsKey(item);
    }

    //we are given key here as a parameter to be passed for dictionary
    //calling a dictionary method remove here would return
    // @Override
    // public void remove(T item) { //change size
    //     if (item == null){
    //         throw new IllegalArgumentException();
    //     }
    //     if (!contains(item)){
    //         throw new InvalidElementException();
    //     }
    //     int removedIndex = dictionary1.remove(item);
    //     // if it is the last thing in the heap
    //     if (removedIndex == size - 1){
    //         heap[removedIndex] = null;
    //         // remove last index dictionary1.remove()
    //         //set it to null
    //     } else {
    //         T lastItem = heap[size - 1];
    //         heap[removedIndex] = lastItem;
    //         dictionary1.put(lastItem, removedIndex);
    //         heap[size - 1] = null;
    //         percolate(removedIndex);
    //         //swap the item at the top, swap remove and last index
    //     }
    //     size--;
    // }
    @Override
    public void remove(T item) { //change size
        if (item == null){
            throw new IllegalArgumentException();
        }
        if (!contains(item)){
            throw new InvalidElementException();
        }
        int removedIndex = dictionary1.get(item);
        // if it is the last thing in the heap
        if (removedIndex == size - 1){
            heap[removedIndex] = null;
            dictionary1.remove(item);
            // remove last index dictionary1.remove()
            //set it to null
        } else {
            //move value to be removed to the end of dictionary, put last value in dictionary in that place (swap)
            //remove last value of dictionary, maybe set to null?
            //percolate
            //**swap and then remove here
            T lastItem = heap[size - 1];
            heap[removedIndex] = lastItem;
            dictionary1.put(lastItem, removedIndex);
            heap[size - 1] = null;
            dictionary1.remove(item);
            percolate(removedIndex);
        }
        size--;
    }

    @Override
    public void replace(T oldItem, T newItem) {
        if (dictionary1.containsKey(newItem)){
            throw new InvalidElementException();
        }
        if (!dictionary1.containsKey(oldItem)){
            throw new InvalidElementException();
        }
        int removedIndex = dictionary1.remove(oldItem);
        heap[removedIndex] = newItem;
        dictionary1.put(newItem, removedIndex);
        percolate(removedIndex);

    }

    @Override
    public int size() {
        return size;
    }
}

