package com.tobiasquinn.fivewaysbustimes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
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

	public static List<Bus> getBusesFromURL(String url) throws XPatherException, ParserConfigurationException,
			SAXException, IOException, XPatherException {
		HtmlCleaner cleaner = new HtmlCleaner();
		URL buses_url = new URL(url);
		URLConnection conn = buses_url.openConnection();

		TagNode node = cleaner.clean(new InputStreamReader(conn.getInputStream()));

		Object[] data_nodes = node.evaluateXPath(BUS_EXPR);
		// take the data in groups of three - if the first of the three is
		// bus number is blank then skip
		List<Bus> busList = new ArrayList<Bus>();
		for (int i = 0; i < data_nodes.length; i += 3) {
			String bus_name = ((TagNode) data_nodes[i]).getText().toString();
			String bus_dest = ((TagNode) data_nodes[i + 1]).getText().toString();
			String bus_time = ((TagNode) data_nodes[i + 2]).getText().toString();
			if (bus_name != "") {
				bus_dest = bus_dest.replace("&nbsp;", " ");
				bus_time = bus_time.replace("&nbsp;", " ");
				// deal with the time object - this is either a time
				// or a minutes offset, convert the minutes offset to a real
				// bus time can have an appended * for timetabled time
				Calendar arrivetime = Calendar.getInstance();
				if (!bus_time.contains(":")) {
					// in the format 'mm mins' or 'm mins'
					int minutes_offset = Integer.parseInt(bus_time.substring(0, 2).trim());
					arrivetime.add(Calendar.MINUTE, minutes_offset);
				} else {
					int cpoint = bus_time.indexOf(':');
					int hour = Integer.parseInt(bus_time.substring(0, cpoint));
					int minutes = Integer.parseInt(bus_time.substring(cpoint+1, cpoint + 3));
					Log.v(LOG_TAG, hour + " == " + minutes);
					Calendar timenow = arrivetime;
					arrivetime.set(Calendar.HOUR_OF_DAY, hour);
					arrivetime.set(Calendar.MINUTE, minutes);
					// deal with midnight crossing
					if (arrivetime.before(timenow)) {
						arrivetime.add(Calendar.HOUR_OF_DAY, 24);
					}
				}
				Bus b = new Bus(bus_name, bus_dest, arrivetime);
				busList.add(b);
				Log.v(LOG_TAG, b.toString());
			}
		}

		return busList;
	}
}
