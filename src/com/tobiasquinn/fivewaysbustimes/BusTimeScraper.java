package com.tobiasquinn.fivewaysbustimes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.xml.sax.SAXException;

import android.util.Log;

public class BusTimeScraper {
	public static final String LOG_TAG = "FBT";

	private static final String BUS_EXPR = "//span[@class='dfifahrten']";

	public static List<Bus> getBusesFromURL(String url)
			throws XPatherException, ParserConfigurationException,
			SAXException, IOException, XPatherException {
		HtmlCleaner cleaner = new HtmlCleaner();
		URL buses_url = new URL(url);
		URLConnection conn = buses_url.openConnection();

		TagNode node = cleaner.clean(new InputStreamReader(conn
				.getInputStream()));

		Object[] data_nodes = node.evaluateXPath(BUS_EXPR);
		// String data =
		// ((TagNode)data_nodes[0]).getChildren().iterator().next().toString().trim();
		// for (Object d_node : data_nodes) {
		// take the data in groups of three - if the first of the three is
		// bus number is blank then skip
		List<Bus> busList = new ArrayList<Bus>();
		for (int i = 0; i < data_nodes.length; i += 3) {			
			String bus_name = ((TagNode)data_nodes[i]).getText().toString();
			String bus_dest = ((TagNode)data_nodes[i+1]).getText().toString();
			String bus_time = ((TagNode)data_nodes[i+2]).getText().toString();
			if (bus_name != "") {
				bus_dest = bus_dest.replace("&nbsp;", " ");
				Bus b = new Bus(bus_name, bus_dest, bus_time);
				busList.add(b);
				Log.v(LOG_TAG, b.toString());
			}
		}

		// Log.v(LOG_TAG, ""+data_nodes);

		return null;
	}
}
