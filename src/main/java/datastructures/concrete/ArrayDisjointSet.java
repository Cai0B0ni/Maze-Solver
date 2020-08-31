package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private IDictionary<T, Integer> dict;
    private int setCount;


    // However, feel free to add more fields and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        dict = new ChainedHashDictionary<>();
        pointers = new int[10];

    }

    @Override
    public void makeSet(T item) {
        if (dict.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        if (pointers.length == setCount){
            //resize
            int[] newPointers = new int[pointers.length * 2];
            for (int i = 0; i < pointers.length; i++){
                newPointers[i] = pointers[i];
            }
            pointers = newPointers;
        }
        dict.put(item, setCount);
        this.pointers[setCount] = -1;
        setCount++;



    }

    @Override
    public int findSet(T item) {
        if (!dict.containsKey(item)){
            throw new IllegalArgumentException();
        }
        int parent = dict.get(item);
        while (pointers[parent] >= 0){
            parent = pointers[parent];
        }

        int tempParent = dict.get(item);
        while (pointers[tempParent] >= 0){
            int var = pointers[tempParent];
            pointers[tempParent] = parent;
            tempParent = var;
        }

        return parent;

    }

    @Override
    public void union(T item1, T item2) {

        if (!dict.containsKey(item1) || !dict.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        if (findSet(item1) == findSet(item2)) {
            return;
        }

        //go into pointers and increment rank
        //this is wrong

        int rank1 = -1 * pointers[findSet(item1)];
        int rank2 = -1 * pointers[findSet(item2)];

        if (rank1 == rank2) {
            // pointers[findSet(item1)] -= 1;
            // rank1 = -1 * pointers[findSet(item1)];
            rank1++;
            pointers[findSet(item1)] -= 1;
        }

        T min = item2;
        T max = item1;
        if (rank1 < rank2) {
            min = item1;
            max = item2;
        }
        // use find set here to find the representative, smaller is not
        // always going to be the representativs
        int smaller = findSet(min);

        pointers[smaller] = dict.get(max);



    }

}

