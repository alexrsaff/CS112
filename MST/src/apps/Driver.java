package apps;
import java.util.*;
import structures.*;
import java.io.IOException;


public class Driver
{
  public static void main(String[] args)
  {
    try
    {
      Graph tester = new Graph("graph3.txt");
      System.out.println("========== ORIGINAL GRAPH ==========");
      tester.print();
      System.out.println("========== ORIGINAL TREES ==========");
      PartialTreeList PTL = MST.initialize(tester);
      for(Iterator itme = PTL.iterator();itme.hasNext();)
  		{
  			System.out.println(itme.next());
  		}
      System.out.println("========== FINISHED TREES ==========");
      for(Iterator itme = PTL.iterator();itme.hasNext();)
  		{
  			System.out.println(itme.next());
  		}
      System.out.println("========== MIN SPAN TREE ==========");
      System.out.println(MST.execute(PTL));
    }
    catch(IOException e)
    {
      System.out.println("Move test files to src directory");
    }
  }
}
