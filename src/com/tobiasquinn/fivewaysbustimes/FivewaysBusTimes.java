package com.tobiasquinn.fivewaysbustimes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.htmlcleaner.XPatherException;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class FivewaysBusTimes extends ListActivity {
	public static final String LOG_TAG = "FBT";

	private BusAdapter m_buslistaa;
	// private BusAdapter m_busadapter;

	private int counter = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// ListView lv = (ListView) findViewById(R.id.listView1);
		List<Bus> buslist = new ArrayList<Bus>();
		m_buslistaa = new BusAdapter(this, R.layout.buslist_item, buslist);
		Log.v(LOG_TAG, "" + buslist);
		setListAdapter(m_buslistaa);
		final Button testButton = (Button) findViewById(R.id.buttonTest);
		testButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(LOG_TAG, "Test Clicked");
				// add a bus to the list
				// m_buslistaa.add(new Bus("26", "Churchill Square", "17:43"));
				m_buslistaa.add(new Bus("" + counter++, "Churchill Square",
						"17:43"));
			}
		});
		final Button changeButton = (Button) findViewById(R.id.buttonChange);
		changeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v(LOG_TAG, "Change Clicked");
				m_buslistaa.remove(m_buslistaa.getItem(1));
				m_buslistaa.insert(
						new Bus("45", "The end of the road", "19:00"), 1);
			}
		});
		final Button clearButton = (Button) findViewById(R.id.buttonClear);
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v(LOG_TAG, "Clear Clicked");
				m_buslistaa.clear();
			}
		});
		final Button fetchButton = (Button) findViewById(R.id.buttonFetch);
		fetchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.v(LOG_TAG, "Fetch Clicked");
				// String test_url =
				// "http://buses.citytransport.org.uk/smartinfo/service/jsp/?olifServerId=182&autorefresh=0&default_autorefresh=20&routeId=182%2F7&stopId=North+Road&optDir=-1&nRows=10&showArrivals=n&optTime=now&time=";
				String test_url = "http://buses.citytransport.org.uk/smartinfo/service/jsp/?olifServerId=182&autorefresh=0&default_autorefresh=20&routeId=182%2FN7&stopId=North+Road&optDir=-1&nRows=10&showArrivals=n&optTime=now&time=";
				List<Bus> buslist;
				try {
					buslist = BusTimeScraper.getBusesFromURL(test_url);
					m_buslistaa.update(buslist);
				} catch (XPatherException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		final Button setList1Button = (Button) findViewById(R.id.buttonSetList1);
		final Button setList2Button = (Button) findViewById(R.id.buttonSetList2);
		setList1Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// generate a new list
				ArrayList<Bus> list1 = new ArrayList<Bus>();
				for (int i = 0; i < 5; i++) {
					list1.add(new Bus("" + i, "List1", "12:34"));
				}
				m_buslistaa.update(list1);
			}
		});
		setList2Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// generate a new list
				ArrayList<Bus> list2 = new ArrayList<Bus>();
				for (int i = 0; i < 10; i++) {
					list2.add(new Bus("" + i, "List2", "43:21"));
				}
				m_buslistaa.update(list2);
			}
		});
		final Button clearStateButton = (Button) findViewById(R.id.buttonClearState);
		clearStateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_buslistaa.clearState();
			}
		});
	}

	private class BusAdapter extends ArrayAdapter<Bus> {
		private List<Bus> items;

		public BusAdapter(Context context, int textViewResourceId,
				List<Bus> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		// updates the Bus objects held int the BusAdapter with the given List
		public void update(List<Bus> buses) {
			// take a list of Bus objects
			// compare each one to the held list of objects
			// remove any extra
			int newlen = buses.size();
			int oldlen = items.size();
			if (newlen < oldlen) {
				for (int i = oldlen - 1; i >= newlen; i--) {
					items.remove(i);
				}
			} else if (newlen > oldlen) {
				List<Bus> sl = buses.subList(oldlen, newlen);
				for (Bus b : sl) {
					b.setState('A');
				}
				items.addAll(sl);
				// items.addAll(buses.subList(oldlen, newlen));
			}
			for (int i = 0; i < items.size(); i++) {
				if (!items.get(i).equals(buses.get(i))) {
					items.set(i, buses.get(i));
					items.get(i).setState('C');
				}
			}

			super.notifyDataSetChanged();
		}

		public void clearState() {
			for (Bus b : items) {
				b.setState(' ');
			}
			super.notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.buslist_item, null);
			}
			Bus b = items.get(position);
			if (b != null) {
				TextView tvs = (TextView) v.findViewById(R.id.textViewState);
				TextView tvt = (TextView) v.findViewById(R.id.textViewTime);
				TextView tvn = (TextView) v.findViewById(R.id.textViewNumber);
				TextView tvd = (TextView) v
						.findViewById(R.id.textViewDestination);
				if (tvs != null) {
					tvs.setText("" + b.getState());
				}
				if (tvt != null) {
					tvt.setText("" + b.getArrivetime());
				}
				if (tvn != null) {
					tvn.setText("" + b.getNumber());
				}
				if (tvd != null) {
					tvd.setText("" + b.getDestination());
				}
			}
			return v;
		}
	}
}