package backend;

import java.util.ArrayList;
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
	
	/**
	 * Returns a collection of wines from a given region. 
	 * Region name is not case or whitespace sensitive.
	 */
	public ArrayList<Wine> winesFromRegion(String reg) {
		return winesFromRegion(reg, "");
	}

	/**
	 * Returns a collection of wines from a given region. 
	 * Region name is not case or whitespace sensitive.
	 * @param reg Region to get wines from
	 * @param fullLoc Current iterative region depth
	 */
	private ArrayList<Wine> winesFromRegion(String reg, String fullLoc) {
		//Collects full region localization
		if (!fullLoc.equals("")) {
			fullLoc += "/";
		}
		fullLoc += regexMagick(reg);
		
		ArrayList<Wine> retVal = new ArrayList<Wine>();
		//Iterates through all of ontology's individuals
		for (Iterator<? extends OntResource> i = base.listIndividuals(); i.hasNext(); ) {
			Individual curr = (Individual) i.next();
			Property p = base.getProperty(ns + "locatedIn");
			//Checks if the individual has a location
			if (curr.hasProperty(p)) {
				String region = curr.getProperty(p).getObject().toString().
						split("#")[1].split("Region")[0];
				//Ignores whitespace and case in region name
				if (region.equalsIgnoreCase(reg) || regexMagick(region).equalsIgnoreCase(reg)) {
					String currClass = curr.getOntClass().getLocalName();
					
					//Recursive method call for subregion
					if (currClass.equals("Region")) {
						ArrayList<Wine> newWines = new ArrayList<Wine>();
						newWines = winesFromRegion(curr.getLocalName().split("Region")[0], 
								fullLoc);
						for (Wine wine : newWines) {
							retVal.add(wine);
						}
					} else {
						Region r = RegionFromProperty(curr.getProperty(p), fullLoc);
						retVal.add(packWine(curr, fullLoc, r));
					}
				}
			}
		}		
		return retVal;
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
	private Wine packWine(Individual wine, String fullLoc, Region r) {
		Wine packedWine = new Wine();

		packedWine.setName(regexMagick(wine.getLocalName()));

		packedWine.setRegion(r);
		
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

		packedWine.setLocation(fullLoc);
		
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
	public Region RegionFromProperty(Statement p, String fullName) {
		Property lat = base.getProperty(ns + "hasLat");
		Property lng = base.getProperty(ns + "hasLng");
		Individual r = base.getIndividual(p.getObject().toString());
		double dLat = Double.parseDouble(r.getProperty(lat).getObject().
				toString().split("#")[1].split("C_")[1]);
		double dLng = Double.parseDouble(r.getProperty(lng).getObject().
				toString().split("#")[1].split("C_")[1]);
		return new Region(new String(fullName).replace("/", ", "), dLat, dLng);
	}

}
