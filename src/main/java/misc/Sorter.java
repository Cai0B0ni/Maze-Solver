package misc;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

import java.util.Iterator;
/**
 * This method takes the input list and returns the top k elements
 * in sorted order.
 *
 * So, the first element in the output list should be the "smallest"
 * element; the last element should be the "largest".
 *
 * If the input list contains fewer than 'k' elements, return
 * a list containing all input.length elements in sorted order.
 *
 * This method must not modify the input list.
 *
 * @throws IllegalArgumentException  if k < 0
 * @throws IllegalArgumentException  if input is null
 */

public class Sorter {

    public static <T extends Comparable<T>> IList<T> topKSort(int k, IList<T> input) {
        IList<T> topKItems = new DoubleLinkedList<>();
        IPriorityQueue<T> heap = new ArrayHeap<>();

        if (k < 0 || input == null){
            throw new IllegalArgumentException();
        }
        if (k == 0){
            return topKItems;
        }
        Iterator<T> iter =  input.iterator();

        for (int i = 0; i < input.size(); i++){
            T item = iter.next();
            if (i < k){
                heap.add(item);
            } else {
                if (item.compareTo(heap.peekMin()) > 0){
                    heap.add(item);
                    heap.removeMin();
                }
            }
        }
        while (!heap.isEmpty()){
            topKItems.add(heap.removeMin());
        }
        //test if this line was in there to make sure that the list is the same
        //input.delete(0);
        return topKItems;
    }

}
