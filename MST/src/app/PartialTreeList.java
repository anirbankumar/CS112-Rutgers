package app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Arc;
import structures.Graph;
import structures.PartialTree;
import structures.Vertex;

/**
 * Stores partial trees in a circular linked list
 * hello
 */
public class PartialTreeList implements Iterable<PartialTree> {
	
	/**
	 * Inner class - to build the partial tree circular linked list 
	 * 
	 */
	public static class Node {
		/**
		 * Partial tree
		 */
		public PartialTree tree;
		
		/**
		 * Next node in linked list
		 */
		public Node next;
		
		/**
		 * Initializes this node by setting the tree part to the given tree,
		 * and setting next part to null
		 * 
		 * @param tree Partial tree
		 */
		public Node(PartialTree tree) {
			this.tree = tree;
			next = null;
		}
	}

	/**
	 * Pointer to last node of the circular linked list
	 */
	private Node rear;
	
	/**
	 * Number of nodes in the CLL
	 */
	private int size;
	
	/**
	 * Initializes this list to empty
	 */
    public PartialTreeList() {
    	rear = null;
    	size = 0;
    }

    /**
     * Adds a new tree to the end of the list
     * 
     * @param tree Tree to be added to the end of the list
     */
    public void append(PartialTree tree) {
    	Node ptr = new Node(tree);
    	if (rear == null) {
    		ptr.next = ptr;
    	} else {
    		ptr.next = rear.next;
    		rear.next = ptr;
    	}
    	rear = ptr;
    	size++;
    }

	public static PartialTreeList initialize(Graph graph) {
	
		PartialTreeList result = new PartialTreeList();
		
		for(int i = 0; i < graph.vertices.length; i++) {
			
			PartialTree pt = new PartialTree(graph.vertices[i]);
			Vertex.Neighbor neigh = graph.vertices[i].neighbors;
			
			while(neigh != null) {
				Arc a = new Arc(graph.vertices[i], neigh.vertex, neigh.weight);
				pt.getArcs().insert(a);
				neigh = neigh.next;
			}
			result.append(pt);
		}
		
		return result;
	}
	
	public static ArrayList<Arc> execute(PartialTreeList ptlist) {
		
		ArrayList<Arc> result = new ArrayList<Arc>();
		
		while(ptlist.size() != 1) {
			PartialTree tree = ptlist.remove();
			Arc a = tree.getArcs().deleteMin();
			while(true) {
				if(a.getv1().getRoot().parent == a.getv2().getRoot().parent) {
					a = tree.getArcs().deleteMin();
				}
				else {
					break;
				}
			}
			result.add(a);
			
			PartialTree treeSecond = ptlist.removeTreeContaining(a.getv2().getRoot().parent);
			tree.merge(treeSecond);
			ptlist.append(tree);
		}
		
		return result;

	}
	
    /**
     * Removes the tree that is at the front of the list.
     * 
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove() 
    throws NoSuchElementException {
    			
    	if (rear == null) {
    		throw new NoSuchElementException("list is empty");
    	}
    	PartialTree ret = rear.next.tree;
    	if (rear.next == rear) {
    		rear = null;
    	} else {
    		rear.next = rear.next.next;
    	}
    	size--;
    	return ret;
    		
    }

    public PartialTree removeTreeContaining(Vertex vertex) 
    throws NoSuchElementException {
    	
    	Node prev = rear;
    	Node ptr = rear.next;
    	while(ptr != rear){
    		if(ptr.tree.getRoot() == vertex){
    			PartialTree out = ptr.tree;
    			prev.next = ptr.next;
    			ptr = ptr.next;
    			size--;
    			return out;
    		}
    		prev = prev.next;
    		ptr = ptr.next;
    	}
    	if(ptr.tree.getRoot() == vertex){
    		PartialTree output = ptr.tree;
    		prev.next = ptr.next;
    		rear = prev;
    		ptr = ptr.next;
    		size--;
    		return output;
    	}
    	throw new NoSuchElementException("Oops");

     }
    
    /**
     * Gives the number of trees in this list
     * 
     * @return Number of trees
     */
    public int size() {
    	return size;
    }
    
    /**
     * Returns an Iterator that can be used to step through the trees in this list.
     * The iterator does NOT support remove.
     * 
     * @return Iterator for this list
     */
    public Iterator<PartialTree> iterator() {
    	return new PartialTreeListIterator(this);
    }
    
    private class PartialTreeListIterator implements Iterator<PartialTree> {
    	
    	private PartialTreeList.Node ptr;
    	private int rest;
    	
    	public PartialTreeListIterator(PartialTreeList target) {
    		rest = target.size;
    		ptr = rest > 0 ? target.rear.next : null;
    	}
    	
    	public PartialTree next() 
    	throws NoSuchElementException {
    		if (rest <= 0) {
    			throw new NoSuchElementException();
    		}
    		PartialTree ret = ptr.tree;
    		ptr = ptr.next;
    		rest--;
    		return ret;
    	}
    	
    	public boolean hasNext() {
    		return rest != 0;
    	}
    	
    	public void remove() 
    	throws UnsupportedOperationException {
    		throw new UnsupportedOperationException();
    	}
    	
    }
}