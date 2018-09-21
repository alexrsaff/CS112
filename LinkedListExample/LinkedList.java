public class LinkedList
{
  public static Node addFront(int data, Node front)
  {
    front = new Node(data,front);
    return front;
  }
  public static void main(String[] args)
  {
    Node front = new Node(3,null);
    System.out.println(front);
  }
}
