package backend;

import java.util.ArrayList;

public class App {

	/**
	 * Demo.
	 */
	public static void main(String[] args) {
		Ontology o = new Ontology("wine.rdf", 
				"http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#");
		ArrayList<Wine> fin;
		fin = o.winesFromRegion("Germany");
		System.out.println("----------\nGermany\n" + fin);
		fin = o.winesFromRegion("central coast");
		System.out.println("----------\nCenral Coast\n" + fin);
		fin = o.winesFromRegion("CentralCoast");
		System.out.println("----------\nCentral Coast\n" + fin);
		fin = o.winesFromRegion("California");
		System.out.println("----------\nCalifornia\n" + fin);
		System.out.println("----------");
		System.out.println(fin.get(0).prettyPrint());
		System.out.println("----------");
		System.out.println(fin.get(1).prettyPrint());
		System.out.println("----------");
	}
	
}
