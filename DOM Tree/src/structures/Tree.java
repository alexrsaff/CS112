package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 *
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 *
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	private String buildString(Scanner scan)
	{
		String returnString = "";
		while(scan.hasNextLine())
		{
			returnString = returnString + scan.nextLine();
		}
		return returnString;
	}

	private ArrayList buildList(String input)
	{
		int StartLocation = 0;
		int EndLocation = 1;
		ArrayList<String> List = new ArrayList<String>();
		while(StartLocation<input.length())
		{
			if(input.charAt(StartLocation) == '<')
			{
				EndLocation=StartLocation+1;
				while(input.charAt(EndLocation)!= '>')
					EndLocation++;
				//System.out.println(input.substring(StartLocation,EndLocation+1));
				List.add(input.substring(StartLocation,EndLocation+1));
				StartLocation=EndLocation+1;
			}
			else
			{
				EndLocation = StartLocation+1;
				while(EndLocation<input.length() && input.charAt(EndLocation)!='<')
					EndLocation++;
				//System.out.println(input.substring(StartLocation,EndLocation));
				List.add(input.substring(StartLocation,EndLocation));
				StartLocation=EndLocation;
			}
		//	StartLocation++;
		}
		return List;
	}

	private ArrayList<String> TreeToList(TagNode front)
	{
		ArrayList<String> htmlList = new ArrayList<String>();
		String[] Temporary = {"html", "body", "p", "em", "b", "table", "tr", "td", "ol", "ul", "li"};
		ArrayList<String> htmlTerms = new ArrayList<String>(Arrays.asList(Temporary));
		if(front.firstChild == null && front.sibling == null)
		{
			htmlList.add(front.tag);
			return htmlList;
		}
		if(htmlTerms.contains(front.tag))
			htmlList.add("<" + front.tag + ">");
		else
			htmlList.add(front.tag);
		if(front.firstChild!=null)
		{
			htmlList.addAll(TreeToList(front.firstChild));
			htmlList.add("</" + front.tag + ">");
		}
		if(front.sibling != null)
			htmlList.addAll(TreeToList(front.sibling));
		return htmlList;
	}

	private TagNode RecursiveBuild(ArrayList<String> htmlList)
	{
		TagNode front = new TagNode(null,null,null);//tag firstChild sibling
		// if(htmlList.length()==1)
		// {
		// 	front.tag = htmlList.get(0);
		// 	return front;
		// }
		if(htmlList.get(0).contains("<") && htmlList.get(0).contains(">"))
		{
			String IndexString = htmlList.get(0);
			int count = 1;
			int index = 1;
			String tag = IndexString.substring(IndexString.indexOf("<")+1,IndexString.indexOf(">"));
			//System.out.println("Found" + IndexString + ", " + tag);
			while (count !=0)
			{
				//System.out.println("Checked" + htmlList.get(index));
				if(htmlList.get(index).contains("</" + tag + ">"))
					count--;
				else if(htmlList.get(index).contains("<" + tag + ">"))
					count++;
				index++;
			}
			index=index-1;
			//System.out.println("Chose" + htmlList.get(index));
			front.tag=tag;
			root = new TagNode(null,front,null);
			ArrayList Middle = new ArrayList<String>(htmlList.subList(1,index));
			ArrayList End = new ArrayList<String>(htmlList.subList(index+1,htmlList.size()));
			if(Middle.size()>0)
				front.firstChild = RecursiveBuild(Middle);
			if(End.size()>0)
				front.sibling = RecursiveBuild(End);
		}
		else
		{
			front.tag = htmlList.get(0);
			ArrayList End = new ArrayList<String>(htmlList.subList(1,htmlList.size()));
			if(End.size()>0)
				front.sibling = RecursiveBuild(End);
		}
		return front;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object.
	 *
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		String str = buildString(sc);
		ArrayList<String> htmlList = buildList(str);
		//System.out.println(htmlList);
		TagNode front = new TagNode(null,null,null);//tag firstChild sibling
		if(htmlList.get(0).contains("<") && htmlList.get(0).contains(">"))
		{
			String IndexString = htmlList.get(0);
			int count = 1;
			int index = 1;
			String tag = IndexString.substring(IndexString.indexOf("<")+1,IndexString.indexOf(">"));
			//System.out.println(tag);
			while (count !=0)
			{
				if(htmlList.get(index).contains("</" + tag + ">"))
					count--;
				else if(htmlList.get(index).contains("<" + tag + ">"))
					count++;
				index++;
			}
			index=index-1;
			front.tag=tag;
			//System.out.println(front.tag);
			ArrayList Middle = new ArrayList<String>(htmlList.subList(1,index));
			//System.out.println(Middle);
			front.firstChild = RecursiveBuild(Middle);
		}
		root = new TagNode(null,front,null);
		root.tag = front.tag;
		root.firstChild = front.firstChild;
		root.sibling = front.sibling;
		//System.out.println("OG:" + htmlList);
		//System.out.println("REDO:" + TreeToList(root));
		return;
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 *
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		ArrayList<String> htmlList = TreeToList(root);
		//System.out.println(htmlList);
		//print(root,1);
		String oldStart = "<" + oldTag + ">";
		String oldEnd = "</" + oldTag + ">";
		String newStart = "<" + newTag + ">";
		String newEnd = "</" + newTag + ">";
		while(htmlList.contains(oldStart))
		{
			int location = htmlList.indexOf(oldStart);
			htmlList.set(location,newStart);
		}
		while(htmlList.contains(oldEnd))
		{
			int location = htmlList.indexOf(oldEnd);
			htmlList.set(location,newEnd);
		}
		//System.out.println(htmlList);
		TagNode temp = RecursiveBuild(htmlList);
		root.firstChild = temp.firstChild;
		root.sibling = temp.sibling;
		root.tag = temp.tag;
		//print(root,1);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 *
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		ArrayList<String> htmlList = TreeToList(root);
		int position = htmlList.indexOf("<table>");
		int count = 0;
		while(count < row)
		{
			while(!(htmlList.get(position).equals("<tr>")))
				position++;
			count++;
			position++;
		}
		int start = position;
		count = 1;
		while(count!=0)
		{
			position++;
			if(htmlList.get(position).equals("<tr>"))
				count++;
			if(htmlList.get(position).equals("</tr>"))
				count--;
		}
		int end = position;
		position = start;
		while(position<end)
		{
			if(htmlList.get(position).equals("<td>"))
			{
				htmlList.add(position+1,"<b>");
				position++;
				end++;
			}
			if(htmlList.get(position).equals("</td>"))
			{
				htmlList.add(position,"</b>");
				position++;
				end++;
			}
			position++;
		}
		//System.out.println(htmlList);
		TagNode temp = RecursiveBuild(htmlList);
		root.firstChild = temp.firstChild;
		root.sibling = temp.sibling;
		root.tag = temp.tag;
	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and,
	 * in addition, all the li tags immediately under the removed tag are converted to p tags.
	 *
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		ArrayList<String> htmlList = TreeToList(root);
		//System.out.println(htmlList);
		//print(root,1);
		String tagStart = "<" + tag + ">";
		String tagEnd = "</" + tag + ">";
		if(tag.equals("ol"))
		{
			while(htmlList.contains(tagEnd))
			{
				int start = htmlList.indexOf(tagStart);
				//System.out.println(tagStart + ", " + start);
				int count = 1;
				int end = start;
				while(count!=0)
				{
					end++;
					//System.out.println("Checking: " + htmlList.get(end));
					if(htmlList.get(end).equals(tagEnd))
						count--;
					if(htmlList.get(end).equals(tagStart))
						count++;
					if(htmlList.get(end).equals("<ul>"))
					{
						int innercount = 1;
						end++;
						while(innercount !=0)
						{
							if(htmlList.get(end).equals("</ul>"))
								innercount--;
							if(htmlList.get(end).equals("<ul>"))
								innercount++;
							end++;
						}
					}
					if(htmlList.get(end).equals("<li>"))
						htmlList.set(end,"<p>");
					if(htmlList.get(end).equals("</li>"))
						htmlList.set(end,"</p>");
				}
				htmlList.remove(end);
				htmlList.remove(start);
			}
		}
		else if(tag.equals("ul"))
		{
			while(htmlList.contains(tagEnd))
			{
				int start = htmlList.indexOf(tagStart);
				//System.out.println(tagStart + ", " + start);
				int count = 1;
				int end = start;
				while(count!=0)
				{
					end++;
					//System.out.println("Checking: " + htmlList.get(end));
					if(htmlList.get(end).equals(tagEnd))
						count--;
					if(htmlList.get(end).equals(tagStart))
						count++;
					if(htmlList.get(end).equals("<ol>"))
					{
						int innercount = 1;
						end++;
						while(innercount !=0)
						{
							if(htmlList.get(end).equals("</ol>"))
								innercount--;
							if(htmlList.get(end).equals("<ol>"))
								innercount++;
							end++;
						}
					}
					if(htmlList.get(end).equals("<li>"))
						htmlList.set(end,"<p>");
					if(htmlList.get(end).equals("</li>"))
						htmlList.set(end,"</p>");
				}
				htmlList.remove(end);
				htmlList.remove(start);
			}
		}
		else
		{
			while(htmlList.contains(tagStart))
			{
				htmlList.remove(htmlList.indexOf(tagStart));
			}
			while(htmlList.contains(tagEnd))
			{
				htmlList.remove(htmlList.indexOf(tagEnd));
			}
		}
		//System.out.println(htmlList);
		TagNode temp = RecursiveBuild(htmlList);
		root.firstChild = temp.firstChild;
		root.sibling = temp.sibling;
		root.tag = temp.tag;
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 *
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag)
	{
		ArrayList<String> htmlList = TreeToList(root);
		String[] Temporary = {"<html>", "<body>", "<p>", "<em>", "<b>", "<table>", "<tr>", "<td>", "<ol>", "<ul>", "<li>", "</html>", "</body>", "</p>", "</em>", "</b>", "</table>", "</tr>", "</td>", "</ol>", "</ul>", "</li>"};
		String[] Punctuation = {"!", "?", ".", ";", ":", " "};
		String[] Punctuation2 = {"!", "?", ".", ";", ":"};
		ArrayList<String> PuncTerms = new ArrayList<String>(Arrays.asList(Punctuation));
		ArrayList<String> PuncTerms2 = new ArrayList<String>(Arrays.asList(Punctuation2));
		ArrayList<String> htmlTerms = new ArrayList<String>(Arrays.asList(Temporary));
		//System.out.println(htmlList);
		//print(root,1);
		String tagStart = "<" + tag + ">";
		String tagEnd = "</" + tag + ">";
		int position = 0;
		while(position < htmlList.size())
		{
			//System.out.println("index:" +  htmlList.get(position));
			if(htmlTerms.contains(htmlList.get(position)))
			{
				position++;
				continue;
			}
			//has term in string
				String item = htmlList.get(position);
				String itemCaps = item.toUpperCase();
				String wordCaps = word.toUpperCase();
				int wordStart = itemCaps.indexOf(wordCaps);
				int wordEnd = wordStart + word.length()-1;
				int forwardAmount = 0;
				String pre = "";
				//pre = pre + item.substring(0,wordStart);
				//item = item.substring(wordStart,item.length());
				//itemCaps = item.toUpperCase();
				boolean passed = false;
				//wordStart = 0;
				//wordEnd = word.length();
				while(passed==false && item.length()>0 && itemCaps.contains(wordCaps))
				{
				//	System.out.println("pre: " + pre);
				//	System.out.println("item: " + item);
					if(itemCaps.contains(wordCaps))//if item contains word
					{
						//System.out.println("Check 1, " + wordStart + ", " + wordEnd);
						if((wordStart-1<0) || (itemCaps.charAt(wordStart-1)==' ')) //if word at start, or space before
						{
							//System.out.println("Check 2, " + itemCaps.charAt(wordEnd+1));
							if(wordEnd+1==itemCaps.length() || (PuncTerms.contains(""+ itemCaps.charAt(wordEnd+1)))) //if word at end, or space or punctuation after
							{
								if(!(wordEnd+2<=itemCaps.length() && PuncTerms2.contains(""+ itemCaps.charAt(wordEnd+1)) && PuncTerms2.contains(""+ itemCaps.charAt(wordEnd+2))))
								{
									passed = true;
									//System.out.println("Check 3");
									String back = "";
									String middle = "";
									String front = item.substring(0,wordStart);
									if(wordEnd+1< item.length() && itemCaps.charAt(wordEnd+1)==' ')
									{
										back = item.substring(wordEnd+1,item.length());
										middle = item.substring(wordStart,wordEnd+1);
									}
									else
									{
										if(wordEnd+2<item.length())
											back = item.substring(wordEnd+2,item.length());
										middle = item.substring(wordStart,wordEnd+1);
									}
									htmlList.remove(position);
									if(back.length()>0)
									{
										htmlList.add(position,back);
										forwardAmount++;
									}
									htmlList.add(position, tagEnd);
									forwardAmount++;
									htmlList.add(position,middle);
									htmlList.add(position,tagStart);
									//forwardAmount++;
									if(front.length()>0)
									{
										htmlList.add(position,pre + front);
										forwardAmount++;
									}
									else if(pre.length()>0)
									{
										htmlList.add(position, pre);
										forwardAmount++;
									}
									//System.out.println(htmlList);
									passed = true;
								}
							}
						}
					}
					if(passed==false)
					{
						//System.out.println("Adding: " + item.substring(0,wordEnd+1));
						pre = pre + item.substring(0,wordEnd+1);
						item = item.substring(wordEnd+1,item.length());
						itemCaps = item.toUpperCase();
						wordStart = itemCaps.indexOf(wordCaps);
						//System.out.println("Adding: " + item.substring(0,wordStart));
						//pre = pre + item.substring(0,wordStart);
						//item = item.substring(wordStart,item.length());
						//itemCaps=item.toUpperCase();
						//wordStart=0;
						wordEnd = wordStart + word.length()-1;
					}
				}
				position = position + 1 + forwardAmount;
		}
		//System.out.println(htmlList);
		TagNode temp = RecursiveBuild(htmlList);
		root.firstChild = temp.firstChild;
		root.sibling = temp.sibling;
		root.tag = temp.tag;
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 *
	 * @return HTML string, including new lines.
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

	/**
	 * Prints the DOM tree.
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
