package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;

	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyeordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine()
	{
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}

	private void printMe()
	{
		System.out.println("Noise Words:\n" + noiseWords);
		System.out.println("KeyWords:\n" + keywordsIndex);
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 *
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile)
	throws FileNotFoundException
	{
		HashMap<String, Occurrence> returnable = new HashMap(1000,2.0f);
		File thisFile = new File(docFile);
		Scanner input = new Scanner(thisFile);
		while(input.hasNext()==true)
		{
			String currword = input.next();
			currword = getKeyword(currword);
			if(currword==null)
				;
			else//if word is keyword
			{
				if(returnable.containsKey(currword)==false)//if there is no entry for the keyword
					returnable.put(currword,new Occurrence(docFile,1));
				else//if there is an entry for the keyword
					returnable.get(currword).frequency = returnable.get(currword).frequency + 1;
			}
		}
		return returnable;
	}


	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table.
	 * This is done by calling the insertLastOccurrence method.
	 *
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws)
	{
		Set<String> keys = kws.keySet();
		Iterator<String> walkThrough	= keys.iterator();
		while(walkThrough.hasNext())
		{
			String currentKey = walkThrough.next();
			if(keywordsIndex.get(currentKey)==null)//if there is no entry for the keyword
			{
				ArrayList <Occurrence> thisDoc = new ArrayList();
				thisDoc.add(kws.get(currentKey));
				keywordsIndex.put(currentKey,thisDoc);
				continue;
			}
			//if there is an entry for the keyword
			keywordsIndex.get(currentKey).add(new Occurrence(kws.get(currentKey).document,kws.get(currentKey).frequency));
			insertLastOccurrence(keywordsIndex.get(currentKey));
		}
	}

	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 *
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 *
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word)
	{
		word = word.toLowerCase();
		word = word.replace("\\s", "");
		int start = 0;
		int end = word.length() - 1;
		for(start = 0; (start <= end) && !(word.charAt(start)>96 && word.charAt(start)<123); start++)
			;
		for(end = end; (end >= start) && !(word.charAt(end)>96 && word.charAt(end)<123); end--)
			;
		end = end + 1;
		word = word.substring(start,end);
		for(int position=0; position<word.length(); position ++)
		{
			if(!(word.charAt(position)>96 && word.charAt(position)<123))
				return null;
		}

		Iterator<String> stepThrough=noiseWords.iterator();
		while(stepThrough.hasNext()==true)
		{
			if(stepThrough.next().equals(word))
				return null;
		}
		if(word.length()==0)
			return null;
		return word;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 *
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs)
	{
		//System.out.println("Before: " + occs);
		int number = occs.get(occs.size()-1).frequency;
		Occurrence temp = occs.remove(occs.size()-1);
		ArrayList<Integer> checked = new ArrayList();
		int end = occs.size()-1;
		int start = 0;
		int middle = (end + start)/2;
		while(start<=end)
		{
			middle = (end + start)/2;
			checked.add(middle);
			if(occs.get(middle).frequency<number)
				end = middle-1;
			else if(occs.get(middle).frequency>number)
				start = middle + 1;
			else if(occs.get(middle).frequency==number)
				break;
		}
		if(occs.get(middle).frequency<=number)
			occs.add(middle,temp);
		else
			occs.add(middle+1,temp);
		//System.out.println("After: " + occs);
		//System.out.println(checked);
		return checked;
	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 *
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile)
	throws FileNotFoundException
	{
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}

		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result.
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 *
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2)
	{
		ArrayList<Occurrence> kw1List = keywordsIndex.get(kw1);
		int kw1Position = 0;
		int kw2Position = 0;
		ArrayList<Occurrence> kw2List = keywordsIndex.get(kw2);
		ArrayList<String> returnMe = new ArrayList();
		ArrayList<Integer> frequencies = new ArrayList();
		//printMe();

		while(returnMe.size()<=5)
		{
			if(kw1List == null && kw2List == null)
				return returnMe;
			else if(kw1List == null)
				{
					for (int position = 0; position<kw2List.size() && position <5; position ++)
						returnMe.add(kw2List.get(position).document);
					return returnMe;
				}
			else if(kw2List==null)
			{
				for (int position = 0; position<kw1List.size() && position <5; position ++)
					returnMe.add(kw1List.get(position).document);
				return returnMe;
			}
			else if(kw1Position>=kw1List.size() && kw2Position>=kw2List.size())
				return returnMe;
			else if(kw1Position>=kw1List.size())
			{
				if(returnMe.contains(kw2List.get(kw2Position).document)==false)
				{
					returnMe.add(kw2List.get(kw2Position).document);
					frequencies.add(kw2List.get(kw2Position).frequency);
				}
				kw2Position++;
			}
			else if(kw2Position>=kw2List.size())
			{
				if(returnMe.contains(kw1List.get(kw1Position).document)==false)
				{
					returnMe.add(kw1List.get(kw1Position).document);
					frequencies.add(kw1List.get(kw1Position).frequency);
				}
				kw1Position++;
			}
			else if(kw1List.get(kw1Position).frequency>kw2List.get(kw2Position).frequency)
			{
				if(returnMe.contains(kw1List.get(kw1Position).document)==false)
				{
					returnMe.add(kw1List.get(kw1Position).document);
					frequencies.add(kw1List.get(kw1Position).frequency);
				}
				kw1Position++;
			}
			else if(kw2List.get(kw2Position).frequency>kw1List.get(kw1Position).frequency)
			{
				if(returnMe.contains(kw2List.get(kw2Position).document)==false)
				{
					returnMe.add(kw2List.get(kw2Position).document);
					frequencies.add(kw2List.get(kw2Position).frequency);
				}
				kw2Position++;
			}
			else if(kw1List.get(kw1Position).frequency==kw2List.get(kw2Position).frequency)
			{
				if(returnMe.contains(kw1List.get(kw1Position).document)==false)
				{
					returnMe.add(kw1List.get(kw1Position).document);
					frequencies.add(kw1List.get(kw1Position).frequency);
				}
				kw1Position++;
			}
		}
		return returnMe;
	}
}
