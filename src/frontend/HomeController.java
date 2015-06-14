package frontend;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import backend.GeoCalculator;
import backend.GeoLocation;
import backend.Ontology;
import backend.Wine;

/**
 * Servlet implementation class HomeController
 */
@WebServlet("/HomeController")
public class HomeController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Ontology o;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HomeController() {
		super();
		o = new Ontology("wine.rdf", 
				"http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Pocetak");
		ArrayList<Wine> wines = new ArrayList<Wine>();
		
		String body = request.getParameter("selectBody");
		String flavor = request.getParameter("selectFlavor");
		String sugar = request.getParameter("selectSugar");
		
		double lat=0, lng=0;
	
		if (request.getParameter("region") != null && !request.getParameter("region").trim().equals("")) {
			String region = request.getParameter("region");
			region = region.replace(' ', '_');
			wines = o.winesByProximity(GeoCalculator.getLangLat(region), sugar, body, flavor);
			GeoLocation gl = GeoCalculator.getLangLat(region);
			lat = gl.getLatitude();
			lng = gl.getLongitude();
		} else {
			System.out.println("lat "+request.getParameter("lat"));
			System.out.println("long "+request.getParameter("lng"));
			// Zbog onog duplog pozivanja prvo pozove sa praznim stringom
			if (!request.getParameter("lat").equals("") && !request.getParameter("lng").equals(""))
			{
				lat = Double.parseDouble(request.getParameter("lat"));
				lng = Double.parseDouble(request.getParameter("lng"));
				wines = o.winesByProximity(new GeoLocation(lat, lng), sugar, body, flavor);
			}
		}
			
		request.setAttribute("wines", wines);
		request.setAttribute("homeLat", lat);
		request.setAttribute("homeLng", lng);
		getServletContext().getRequestDispatcher("/wines.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
