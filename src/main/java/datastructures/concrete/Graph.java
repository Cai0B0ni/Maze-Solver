package datastructures.concrete;


import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.concrete.dictionaries.KVPair;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Sorter;
import misc.exceptions.NoPathExistsException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.
    private IList<E> edges;
    private IDictionary<V, IList<E>> dict;


    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * Note that each edge in 'edges' represents a unique edge. For example, if 'edges'
     * contains an entry for '(A,B)' and for '(B,A)', that means there are two parallel
     * edges between vertex 'A' and vertex 'B'.
     *
     * @throws IllegalArgumentException if any edges have a negative weight
     * @throws IllegalArgumentException if any edges connect to a vertex not present in 'vertices'
     * @throws IllegalArgumentException if 'vertices' or 'edges' are null or contain null
     * @throws IllegalArgumentException if 'vertices' contains duplicates
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        this.dict = new ChainedHashDictionary<>();
        this.edges = edges;
        for (V vertex : vertices) {
            dict.put(vertex, new DoubleLinkedList<>());
        }
        for (E edge : edges) {
            V vertex1 = edge.getVertex1();
            V vertex2 = edge.getVertex2();
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            if (!vertices.contains(vertex1) || !vertices.contains(vertex2)) {
                throw new IllegalArgumentException();
            }
            dict.get(vertex2).add(edge);
            dict.get(vertex1).add(edge);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
       return this.dict.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ArrayDisjointSet<V> tree = new ArrayDisjointSet<>();

        for (KVPair<V, IList<E>> vertex : dict) {
            tree.makeSet(vertex.getKey());
        }


        ISet<E> edgeSet = new ChainedHashSet<>();
        IList<E> sortEdges = Sorter.topKSort(numEdges(), edges);

        for (E sortedEdge : sortEdges) {
            V vertex1 = sortedEdge.getVertex1();
            V vertex2 = sortedEdge.getVertex2();
            if (tree.findSet(vertex1) != tree.findSet(vertex2)) {
                tree.union(vertex1, vertex2);
                edgeSet.add(sortedEdge);
            }
        }
        return edgeSet;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null or not in the graph
     */

    public IList<E> findShortestPathBetween(V start, V end) {
        if (start == end) {
            return new DoubleLinkedList<>();
        }
        if (start == null || end == null || !dict.containsKey(start) || !dict.containsKey(end)){
            throw new IllegalArgumentException();
        }
        IDictionary<V, IList<E>> path = new ChainedHashDictionary<>();
        ISet<V> processed = new ChainedHashSet<>();
        IDictionary<V, Double> distance = new ChainedHashDictionary<>();
        IPriorityQueue<ComparableVertex<V, E>> mpq = new ArrayHeap<>();

        //Add brand new vertex with no weight and empty path list starting at the "start"
        for (KVPair<V, IList<E>> pair : dict){
            distance.put(pair.getKey(), Double.POSITIVE_INFINITY);
        }

        distance.put(start, 0.0);
        //ComparableVertex<V, E> source = new ComparableVertex<>(start, null, 0.0);
        ComparableVertex<V, E> source = new ComparableVertex<>(start, 0.0);
        path.put(start, new DoubleLinkedList<>());
        mpq.add(source);

        while (!mpq.isEmpty()) {
            ComparableVertex<V, E> u = mpq.removeMin();

            if (!u.vertex.equals(end)) {
                if (!processed.contains(u.vertex)) {
                    processed.add(u.vertex);

                    for (E edge : dict.get(u.vertex)) {
                        V nextVertex = edge.getOtherVertex(u.vertex); // getOtherVertex
                        if (!processed.contains(nextVertex)) {
                            double oldDist = distance.get(nextVertex);
                            double newDist = distance.get(u.vertex) + edge.getWeight();
                            if (newDist < oldDist) { // <=
                                ComparableVertex<V, E> temp = new ComparableVertex<>(nextVertex, newDist);
                                mpq.add(temp);
                                distance.put(nextVertex, newDist);


                                IList<E> lowestWeight = new DoubleLinkedList<>();
                                for (E endge : path.get(u.vertex)) {
                                    lowestWeight.add(endge);
                                }
                                lowestWeight.add(edge);
                                path.put(nextVertex, lowestWeight);
                            }
                        }
                    }
                }
            } else {
                 return path.get(u.vertex);
            }
        }

        throw new NoPathExistsException();
    }

    private static class ComparableVertex<V, E> implements Comparable<ComparableVertex<V, E>> {
        public V vertex;
        public double totalWeight;


        public ComparableVertex(V start, double totalWeight) {
            this.vertex = start;
            this.totalWeight = totalWeight;
        }

        @Override
        public int compareTo(ComparableVertex<V, E> other) {
            if (this.totalWeight > other.totalWeight){
                return 1;
            } else if (this.totalWeight < other.totalWeight){
                return -1;
            } else {
                return 0;
            }

        }
    }
}
