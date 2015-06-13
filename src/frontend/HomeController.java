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
		System.out.println("pOCETAK");
		ArrayList<Wine> wines;
		ArrayList<Wine>	returnWines = new ArrayList<Wine>();
		
	/*	Enumeration<String> e = request.getParameterNames();
		while(e.hasMoreElements())
		{
			String s = e.nextElement();
			System.out.println("Parametar: " + s);
		}*/
		
		String body = request.getParameter("selectBody");
		String flavor = request.getParameter("selectFlavor");
		String type = request.getParameter("selectType");
		String sugar = request.getParameter("selectSugar");
		
	//	System.out.println("Body : " +body);
	
		if (request.getParameter("region") != null) {
			String region = request.getParameter("region");
			region = region.replace(' ', '_');
			wines = o.winesByProximity(GeoCalculator.getLangLat(region));
		} else {
			double lat = Double.parseDouble(request.getParameter("lat"));
			double lng = Double.parseDouble(request.getParameter("lng"));
			wines = o.winesByProximity(new GeoLocation(lat, lng));
		}
		

		for(Wine w : wines)
		{
			try{
				if (!body.equals("Any"))
					if (w.getBody() != null)
					{
						if(!w.getBody().equals(body))
						{
							continue;
						}
					}
					else
						continue;
				if (!flavor.equals("Any"))
					if (w.getFlavor() != null)
					{
						if(!w.getFlavor().equals(flavor))
						{
							continue;
						}
					}
					else
						continue;
				if (!type.equals("Any"))
					if (w.getType() != null)
					{
						if(!w.getType().equals(type))
						{
							continue;
						}
					}
					else
						continue;
				if (!sugar.equals("Any"))
					if (w.getSugar() != null)
					{
						if(!w.getSugar().equals(sugar))
						{
							continue;
						}
					}
					else
						continue;
				
			//	System.out.println("Prosao");
				returnWines.add(w);
			}
			catch (NullPointerException e1)
			{
				System.out.println("Greska !");
			}
		}
	
		
		request.setAttribute("wines", returnWines);
		getServletContext().getRequestDispatcher("/wines.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
