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
		ArrayList<Wine> wines;
		if (request.getParameter("region") != null) {
			String region = request.getParameter("region");
			wines = o.winesByProximity(GeoCalculator.getLangLat(region));
		} else {
			double lat = Double.parseDouble(request.getParameter("lat"));
			double lng = Double.parseDouble(request.getParameter("lng"));
			wines = o.winesByProximity(new GeoLocation(lat, lng));
		}
		request.setAttribute("wines", wines);
		getServletContext().getRequestDispatcher("/wines.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
