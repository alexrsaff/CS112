package lse;

import java.io.*;
import java.util.*;
public class SearchDriver
{
  public static void main(String[] args) throws FileNotFoundException
  {
    LittleSearchEngine Test = new LittleSearchEngine();
    System.out.println("Enter Document List File: ");
    Scanner scanner = new Scanner(System.in);
    String file = scanner.nextLine();
    Test.makeIndex(file,"noisewords.txt");
    System.out.println("Enter First Keyword: ");
    String kw1 = scanner.nextLine();
    System.out.println("Enter Second Keyword: ");
    String kw2 = scanner.nextLine();
    System.out.println(Test.top5search(kw1,kw2));
  }
}
