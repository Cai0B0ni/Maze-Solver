package datastructures.concrete.dictionaries;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.d
    private Pair<K, V>[] pairs;
    private int size;

    // You may add extra fields or helper methods though!

    public ArrayDictionary() {

        this.pairs = makeArrayOfPairs(100);
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }

    @Override
    public V get(K key) {
        int index = findIndex(key);
        if (index < 0){ // doesn't contain
            throw new NoSuchKeyException();
        }else { // contains
            return this.pairs[index].value;
        }
    }

    public int findIndex(K key) {
        if (this.size == 0){
            return -1;
        }

        for (int i = 0; i < this.size; i++){
            if (this.pairs[i].key != null){
                if (this.pairs[i].key.equals(key)) {
                    return i;
                }
            } else {
                if (this.pairs[i].key == key){
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void put(K key, V value) {
        pairs[this.size] = new Pair<>(key, value);
        int index = findIndex(key);
        if (this.size == 0){
            size++;
            return;
        }
        if (index >= 0){ // key already in use
            pairs[index].value = value;
        }else { //key not in
            if (this.size == pairs.length -1) {

                Pair<K, V>[] newPair = makeArrayOfPairs(size * 2);
                for (int i = 0; i <= size; i++){
                    newPair[i] = pairs[i];
                }
                pairs = newPair;
            }else {
                // Create a key, value pair with initial new values here
                pairs[this.size] = new Pair<K, V>(key, value);
            }
            size++;
        }
        //throw new NotYetImplementedException();
    }

    @Override
    public V remove(K key) {
        int index = findIndex(key);
        V returnValue = getOrDefault(key, get(key));
        if (index >= 0) { // takes care of empty and key doesnt exist
            if (size > 1) {
                returnValue = pairs[index].value;

                pairs[index] = pairs[size -1];

            }else {
                pairs[0] = null;
            }
            size--;
        }else {
            throw new NoSuchKeyException();
        }
        return returnValue;
    }

    @Override
    public boolean containsKey(K key) {
        return findIndex(key) >= 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;

        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new ArrayDictionaryIterator<K, V>(size, pairs);
    }

    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        private Pair<K, V>[] pair;
        private int size;
        private int counter;

        public ArrayDictionaryIterator(int size, Pair<K, V>[] pair) {
            this.size = size;
            this.pair = pair;
            counter = 0;

        }

        public boolean hasNext() {
            return !(counter >= size);
        }

        public KVPair<K, V> next() {

            if (!hasNext()){
                throw new NoSuchElementException();
            } else {
                KVPair<K, V> kvPair = new KVPair<>(this.pair[counter].key, this.pair[counter].value);
                counter++;

                return kvPair;

            }
        }
    }
}