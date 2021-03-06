package backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class Ontology {

	private String ns;
	private OntModel base;

	public Ontology(String source, String ns) {
		super();
		this.ns = ns;
		base = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		base.read(source, "RDF/XML");
	}
	
	/**
	 * Demo
	 */
	public static void main(String[] args) {
		Ontology o = new Ontology("wine.rdf", 
				"http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#");
		ArrayList<Wine> wines = 
				o.winesByProximity(new GeoLocation(0, 0), "Dry", "Medium", "Moderate");
		for (Wine wine : wines) {
			System.out.println(wine.prettyPrint());
		}
	}

	/**
	 * Returns wines with given properties, sorted by their distance from the given location.
	 */
	public ArrayList<Wine> winesByProximity(GeoLocation loc, String sugar, String body, String flavor) {
		HashSet<Resource> results = winesByProperties(sugar, body, flavor);
		ArrayList<Wine> wines = getWines(loc, results);
		return sortByDistance(wines);
	}

	/**
	 * Returns wines with given properties.
	 */
	private HashSet<Resource> winesByProperties(String sugar, String body, String flavor) {
		HashSet<Resource> result;
		Property hasSugar = base.getProperty(ns + "hasSugar");
		Property hasBody = base.getProperty(ns + "hasBody");
		Property hasFlavor = base.getProperty(ns + "hasFlavor");
		result = getSubjects(hasSugar, null);
		result.addAll(getSubjects(hasBody, null));
		result.addAll(getSubjects(hasFlavor, null));
		if (!sugar.equals("Any")) {
			Resource rSugar = base.getResource(ns + sugar);
			result.retainAll(getSubjects(hasSugar, rSugar));
		}
		if (!body.equals("Any")) {
			Resource rBody = base.getResource(ns + body);
			result.retainAll(getSubjects(hasBody, rBody));
		}
		if (!flavor.equals("Any")) {
			Resource rFlavor = base.getResource(ns + flavor);
			result.retainAll(getSubjects(hasFlavor, rFlavor));
		}
		return result;
	}

	/**
	 * Creates Wine objects from ontology resources.
	 */
	private ArrayList<Wine> getWines(GeoLocation reg, HashSet<Resource> resources) {
		ArrayList<Wine> retVal = new ArrayList<Wine>();
		Property p = base.getProperty(ns + "locatedIn");
		for (Resource wine : resources) {
			if (wine.getProperty(p) == null) {
				continue;
			}
			Region r = RegionFromProperty(wine.getProperty(p));
			double dist = GeoCalculator.distance(reg, r.getLoc());
			retVal.add(packWine(wine, r, dist));
		}
		return retVal;
	}

	/**
	 * Gets subjects from statements that match the inputs.
	 * @param p - Predicate
	 * @param o - Object
	 * @return Subject
	 */
	private HashSet<Resource> getSubjects(Property p, Resource o) {
		HashSet<Resource> subjects = new HashSet<Resource>();
		for (StmtIterator i = base.listStatements(null, p, o); i.hasNext(); ) {
			Statement stmt = i.nextStatement();
			subjects.add(stmt.getSubject());
		}
		return subjects;
	}

	/**
	 * Turns PascalCase to normal case.
	 */
	private static String regexMagick(String input) {
		return input.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])",	"(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"),	" ");
	}

	/**
	 * Turns an ontology individual into a Wine class object.
	 * @param wine Individual to be packed.
	 * @param fullLoc Full wine location.
	 */
	private Wine packWine(Resource wine, Region r, double dist) {
		Wine packedWine = new Wine();

		packedWine.setName(regexMagick(wine.getLocalName()));

		packedWine.setRegion(r);
		packedWine.setDistance(dist);

		Property maker = base.getProperty(ns + "hasMaker");
		if (wine.hasProperty(maker)) {
			packedWine.setMaker(getPropertyValue(wine.getProperty(maker)));
		}

		Property body = base.getProperty(ns + "hasBody");
		if (wine.hasProperty(body)) {
			packedWine.setBody(getPropertyValue(wine.getProperty(body)));
		}

		Property flavor = base.getProperty(ns + "hasFlavor");
		if (wine.hasProperty(flavor)) {
			packedWine.setFlavor(getPropertyValue(wine.getProperty(flavor)));
		}

		packedWine.setType(regexMagick(
				base.getIndividual(wine.toString()).getOntClass().getLocalName()));

		Property sugar = base.getProperty(ns + "hasSugar");
		if (wine.hasProperty(sugar)) {
			packedWine.setSugar(getPropertyValue(wine.getProperty(sugar)));
		}

		return packedWine;
	}

	/**
	 * Gets the local value of the property.
	 */
	private static String getPropertyValue(Statement s) {
		return regexMagick(s.getObject().toString().split("#")[1]);
	}

	/**
	 * 
	 * @param p 'hasLocation' property of an individual.
	 * @param fullName Region name with parent regions.
	 * @return Region with coordinates.
	 */
	public Region RegionFromProperty(Statement p) {
		Property lat = base.getProperty(ns + "hasLat");
		Property lng = base.getProperty(ns + "hasLng");
		Individual r = base.getIndividual(p.getObject().toString());
		double dLat = Double.parseDouble(r.getProperty(lat).getObject().
				toString().split("#")[1].split("C_")[1]);
		double dLng = Double.parseDouble(r.getProperty(lng).getObject().
				toString().split("#")[1].split("C_")[1]);
		String name = p.getObject().toString().split("#")[1];
		name = regexMagick(name.split("Region")[0]);
		return new Region(name, dLat, dLng);
	}

	/**
	 * Sorts wines by their distance attribute.
	 * @param wines
	 * @return
	 */
	private static ArrayList<Wine> sortByDistance(ArrayList<Wine> wines) {
		Collections.sort(wines, new DistanceComparator());
		return wines;
	}

	static class DistanceComparator implements Comparator<Wine> {

		@Override
		public int compare(Wine a, Wine b) {
			return a.getDistance() < b.getDistance() ? -1 : a.getDistance() == b.getDistance() ? 0 : 1;
		}

	}

}
