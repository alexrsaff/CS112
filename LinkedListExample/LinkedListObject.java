public class LinkedListObject
{
  private Node front;
  private int size;
  public LinkedListObject()
  {
    front = null;
    size = 0;
  }
  public void addFront(int item)
  {
    front = newNode(item,front);
    size++;
  }
  /*
    Insert modified outher methods.
  */
}
