package backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;

public class Ontology {

	private String ns;
	private OntModel base;

	public Ontology(String source, String ns) {
		super();
		this.ns = ns;
		base = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		base.read(source, "RDF/XML");
	}

	public ArrayList<Wine> winesByProximity(GeoLocation reg) {
		ArrayList<Wine> retVal = new ArrayList<Wine>();
		//Iterates through all of ontology's individuals
		for (Iterator<? extends OntResource> i = base.listIndividuals(); i.hasNext(); ) {
			Individual curr = (Individual) i.next();
			Property p = base.getProperty(ns + "locatedIn");
			//Checks if the individual has a location
			if (curr.hasProperty(p)) {
				String currClass = curr.getOntClass().getLocalName();
				if (!currClass.equals("Region")) {
					Region r = RegionFromProperty(curr.getProperty(p));
					double dist = GeoCalculator.distance(reg, r.getLoc());
					retVal.add(packWine(curr, r, dist));
				}
			}
		}
		return sortByDistance(retVal);
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
	private Wine packWine(Individual wine, Region r, double dist) {
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

		packedWine.setType(regexMagick(wine.getOntClass().getLocalName()));

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
	
	public ArrayList<Wine> sortByDistance(ArrayList<Wine> wines) {
		Collections.sort(wines, new AgeComparator());
		return wines;
	}
	
	class AgeComparator implements Comparator<Wine> {
		
	    @Override
	    public int compare(Wine a, Wine b) {
	        return a.getDistance() < b.getDistance() ? -1 : a.getDistance() == b.getDistance() ? 0 : 1;
	    }
	
	}

}
