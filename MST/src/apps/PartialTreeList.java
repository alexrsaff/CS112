package apps;

import java.util.Iterator;
import java.util.NoSuchElementException;

import structures.Vertex;


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

    /**
     * Removes the tree that is at the front of the list.
     *
     * @return The tree that is removed from the front
     * @throws NoSuchElementException If the list is empty
     */
    public PartialTree remove()
    throws NoSuchElementException {
      if (size<2)
      {
        Node temp = rear;
        rear = null;
        size=size-1;
        return temp.tree;
      }
      Node temp = rear.next;
      rear.next = rear.next.next;
      size=size-1;
      return temp.tree;
    }

    /**
     * Removes the tree in this list that contains a given vertex.
     *
     * @param vertex Vertex whose tree is to be removed
     * @return The tree that is removed
     * @throws NoSuchElementException If there is no matching tree
     */
    public PartialTree removeTreeContaining(Vertex vertex)
    throws NoSuchElementException {
      //loop through all parents in rear
      //if top parent = vertex, delete tree
      //loop through each node
      //within each node loop through all the parents
      //if the top parent = the vertex, delete tree
      Node prevPos = rear;
      Node currPos = prevPos.next;
      //System.out.println("CURRPOS: " + currPos);
      Vertex TopVertex = vertex.parent;
      while(TopVertex.parent!=TopVertex)
        TopVertex=TopVertex.parent;
      //System.out.println("TOP VERTEX: " + TopVertex);
      if(prevPos==currPos)
      {
        if(currPos.tree.getRoot()==TopVertex)
        {
          currPos = rear;
          rear = null;
          size = size -1;
          return currPos.tree;
        }
      }
      if(size==2)
      {
        if(currPos.tree.getRoot()==TopVertex)
        {
          //System.out.println("IN HERE");
          prevPos.next=currPos.next;
          size = size -1;
          return currPos.tree;
        }
      }
    //  currPos = currPos.next;
    //  prevPos = prevPos.next;
      do
      {
        if(currPos.tree.getRoot()==TopVertex)
          {
            //System.out.println("IN HERE");
            prevPos.next = currPos.next;
            if(currPos==rear)
              rear = prevPos;
            size = size -1;
            return currPos.tree;
          }
        prevPos = currPos;
        currPos = currPos.next;
      } while(currPos!=rear.next);
      return null;
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
