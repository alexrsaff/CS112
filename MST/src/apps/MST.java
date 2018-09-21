package apps;

import structures.*;
import java.util.ArrayList;

public class MST {

	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 *
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		PartialTreeList PTL = new PartialTreeList();
		for (int i=0; i < graph.vertices.length; i++)
		{
			PartialTree PT = new PartialTree(graph.vertices[i]);
			//System.out.println("This part: " + graph.vertices[i]);
			for (Vertex.Neighbor nbr=graph.vertices[i].neighbors; nbr != null; nbr=nbr.next)
			{
				PartialTree.Arc PTARC = new PartialTree.Arc(graph.vertices[i], nbr.vertex, nbr.weight);
				PT.getArcs().insert(PTARC);
				//System.out.println(graph.vertices[i].name + " " + nbr.vertex.name + " " + nbr.weight);
			}
			PTL.append(PT);
		}
		//System.out.println("-----------------------");
		//System.out.println(PTL.size());
		return PTL;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 *
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		ArrayList<PartialTree.Arc> returnMe = new ArrayList<PartialTree.Arc>();
		while(ptlist.size()>2)
		{
			// for(Iterator itme = ptlist.iterator();itme.hasNext();)
			// {
			// 	System.out.println(itme.next());
			// }
			//System.out.println("====================================");
			PartialTree PT = ptlist.remove();
			PartialTree.Arc minArc = PT.getArcs().deleteMin();
			Vertex MergeWith = minArc.v2;
			returnMe.add(minArc);
			PartialTree PTMERGE = ptlist.removeTreeContaining(MergeWith);
			//System.out.println("MERGING: " + PT + " WITH: " + MergeWith);
			PT.merge(PTMERGE);

			//System.out.println("COMPARING TO: " + PT.getArcs().getMin().v2);
			//System.out.println(MergeWith);
			//System.out.println("ROOT: " + PT.getRoot());
			boolean caught = false;
			PartialTree removed = null;
			//PT.getArcs().getMin().v2 == MergeWith || PT.getArcs().getMin().v2 ==PT.getRoot()
			do{
				caught = false;
				removed = ptlist.removeTreeContaining(PT.getArcs().getMin().v2);
				if (removed == null)
				{
					caught = true;
					PT.getArcs().deleteMin();
				}
				if(caught ==false)
					ptlist.append(removed);
			} while(caught ==true);
			//PT.getRoot().parent.name+=PTMERGE.getRoot().parent.name;
			ptlist.append(PT);
		}
		//System.out.println("==================FINAL==================");
		PartialTree PT = ptlist.remove();
		PartialTree.Arc minArc = PT.getArcs().deleteMin();
		Vertex MergeWith = minArc.v2;
		returnMe.add(minArc);
		PartialTree PTMERGE = ptlist.removeTreeContaining(MergeWith);
		//System.out.println("MERGING: " + PT + " WITH: " + MergeWith);
		PT.merge(PTMERGE);
		// for(Iterator itme = ptlist.iterator();itme.hasNext();)
		// {
		// 	System.out.println(itme.next());
		// }

		return returnMe;
	}
}
