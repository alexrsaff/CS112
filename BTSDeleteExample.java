public T delete(T key)
throws NoSuchElementException
{
  //search and locate x
  BSTNode<T> x =root, p =null;
  int c = 0;
  while(x != null)
  {
    c = key.compareTo(x.data);
    if(c==0)
      break;
    p=x;
    x = c < 0 ? x.left ? x.right;
  }
  if(x ==null)//key is not in the tree
    throw new NoSuchElementException();
  //hold on to the data at x to be returned at the end of the method
  T hold = x.data;
  if(x.left != null && x.right !=null)
  {
    //find inorder predecessor of x
    BSTNode<T> 7 = x.left;
    p = x;
    while(y.right !=null)
    {
      p = y;
      y=y.right;
    }
    //copy y's data into x
    x.data = y.data;
    //set up to fall through
    x = y;
  }
  //check case 1
  /*THIS CODE BLOCK FOR CASE 1 NOT NEEDED
  *CASE 2 BLOCK THAT FOLLOWS ALSO WORKS FOR CASE 1
  if(x.left == null && x.right==null)
  {
   if(p==null)
   {
    root = null;
    size = 0;
    return hold;
   }
   if(x==p.left)
    p.left=null;
   else
    p.right=null;
   size--;
   return hold;
  }
  */
  //FOLLOWING CODE WILL WORK FOR BOTH CASE 1 AND CASE 2
  if(p==null)
  {
    root = x.left != null ? x.left : x.right;
    size--;
    return hold;
  }
  if(x==p.right)
    p.right = x.left != null ? x.left : x.right;
  else
    p.left = x.left != null ? x.left : x.right;
  size--;
  return hold;
}
public ArrayList<T> sort()
{
  ArrayList<T> list new = ArrayList<T>(size);
  inorder(root, list);
  return list;
}
private static <T extends Comparable<T>>
void inorder(BSTNode<T> root, ArrayList<T> list)
{
  if(root == null)
    return;
  inorder(root.left, list);//L
  list.add(root.data);//V
  inorder(root.right,list); //R
}
