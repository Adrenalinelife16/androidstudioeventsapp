package com.adrenalinelife.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adrenalinelife.EventDetailActivity;
import com.adrenalinelife.MainActivity;
import com.adrenalinelife.R;
import com.adrenalinelife.custom.CustomActivity;
import com.adrenalinelife.custom.CustomFragment;
import com.adrenalinelife.custom.PicassoTransform;
import com.adrenalinelife.model.Event;
import com.adrenalinelife.ui.FavEvents;
import com.adrenalinelife.utils.Commons;
import com.adrenalinelife.utils.Const;
import com.adrenalinelife.utils.ImageLoader;
import com.adrenalinelife.utils.ImageLoader.ImageLoadedListener;
import com.adrenalinelife.utils.ImageUtils;
import com.adrenalinelife.utils.Log;
import com.adrenalinelife.utils.StaticData;
import com.adrenalinelife.utils.Utils;
import com.adrenalinelife.web.WebHelper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

/**
 * The Class CalendarView is Fragment class to hold the Calendar view.
 */
public class CalendarView extends CustomFragment implements DateChangeListener
{

	/** The item month. */
	public GregorianCalendar month, itemmonth;// calendar instances.

	/** The adapter. */
	public CalendarAdapter adapter;// adapter instance

	/** The handler. */
	public Handler handler;// for grabbing some event values for showing the dot
							// marker.
	/** The items. */
	public ArrayList<String> items; // container to store calendar items which
	// needs showing the event marker
	/** The events. */
	private final ArrayList<Event> events = new ArrayList<Event>();

	/** The list. */
	private ListView list;

	/** The event for selected date. */
	private ArrayList<Event> eventSel;

	public Button mFavButton;
	public Button mAttendButton;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.calendar, null);
		setHasOptionsMenu(true);
        //v.findViewById(R.id.vTabs).setVisibility(View.VISIBLE);

		/** Fabric Initializing **/
		Fabric.with(getActivity(), new Answers());
		Fabric.with(getActivity(), new Crashlytics());
		final Fabric fabric = new Fabric.Builder(getActivity())
				.kits(new Crashlytics())
				.debuggable(true)
				.build();
		Fabric.with(fabric);

		mFavButton = (Button) v.findViewById(R.id.favButton);
		mFavButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FavEvents fE = new FavEvents();
				android.support.v4.app.FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.add(R.id.content_frame, fE).addToBackStack("My Favorites").commit();
			}
		});

		mAttendButton = (Button) v.findViewById(R.id.attendButton);
		mAttendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new MaterialDialog.Builder(getContext())
						.title("Attending Events")
						.content("This feature is coming soon")
						.negativeText("Dismiss")
						.show();
			}
		});




		setupEventList(v);
		initCalendarView(v);

		return v;
	}

	/**
	 * Initialize the calendar view.
	 * 
	 * @param v
	 *            the v
	 */
	private void initCalendarView(View v)
	{
		month = (GregorianCalendar) Calendar.getInstance();
		itemmonth = (GregorianCalendar) month.clone();

		items = new ArrayList<String>();

		adapter = new CalendarAdapter(getActivity(), month);
		adapter.setDateChangeListener(this);

		GridView gridview = (GridView) v.findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();

		TextView title = (TextView) v.findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

		RelativeLayout previous = (RelativeLayout) v
				.findViewById(R.id.previous);
		previous.setOnTouchListener(CustomActivity.TOUCH);
		previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				setPreviousMonth();
				refreshCalendar(getView());
			}
		});

		RelativeLayout next = (RelativeLayout) v.findViewById(R.id.next);
		next.setOnTouchListener(CustomActivity.TOUCH);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				setNextMonth();
				refreshCalendar(getView());

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id)
			{

				((CalendarAdapter) parent.getAdapter())
						.setSelected(position, v);
			}

		});

		refreshCalendar(v);
	}

	/**
	 * Set the up event list.
	 * 
	 * @param v
	 *            the root view
	 */
	private void setupEventList(View v)
	{
		int w = StaticData.getDIP(60);
		int h = StaticData.getDIP(60);
		bmNoImg = ImageUtils.getPlaceHolderImage(R.drawable.no_image, w, h);

		loader = new ImageLoader(w, h, ImageUtils.SCALE_FIT_CENTER);

		eventSel = new ArrayList<Event>();
		list = (ListView) v.findViewById(R.id.list);
		list.setAdapter(new EventAdapter());
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				startActivity(new Intent(getActivity(),
						EventDetailActivity.class).putExtra(Const.EXTRA_DATA,
						eventSel.get(position)));
			}
		});
	}

	/**
	 * Sets the next month.
	 */
	protected void setNextMonth()
	{
		if (month.get(Calendar.MONTH) == month.getActualMaximum(Calendar.MONTH))
		{
			month.set(month.get(Calendar.YEAR) + 1,
					month.getActualMinimum(Calendar.MONTH), 1);
		}
		else
		{
			month.set(Calendar.MONTH, month.get(Calendar.MONTH) + 1);
		}

	}

	/**
	 * Sets the previous month.
	 */
	protected void setPreviousMonth()
	{
		if (month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH))
		{
			month.set(month.get(Calendar.YEAR) - 1,
					month.getActualMaximum(Calendar.MONTH), 1);
		}
		else
		{
			month.set(Calendar.MONTH, month.get(Calendar.MONTH) - 1);
		}

	}

	/**
	 * Show toast.
	 * 
	 * @param string
	 *            the string message
	 */
	protected void showToast(String string)
	{
		Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Refresh calendar.
	 *
	 * @param v the v
	 */
	public void refreshCalendar(View v)
	{
		TextView title = (TextView) v.findViewById(R.id.title);

		onDateChange(0, 0);
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		final ProgressDialog dia = parent
				.showProgressDia(R.string.alert_loading);
		new Thread(new Runnable() {
			@Override
			public void run()
			{
				final ArrayList<Event> al = WebHelper.getEventsByMonth(
						month.get(Calendar.MONTH) + 1 + "",
						month.get(Calendar.YEAR) + "");
				parent.runOnUiThread(new Runnable() {

					@Override
					public void run()
					{
						dia.dismiss();
						events.clear();
						if (al == null)
							Utils.showDialog(parent,
									StaticData.getErrorMessage());
						else
							events.addAll(al);
						handler.post(calendarUpdater);
					}
				});
			}
		}).start();

		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	/** The calendar updater to update the Calendar grids and data. */
	public Runnable calendarUpdater = new Runnable() {

		@Override
		public void run()
		{
			items.clear();

			DateFormat df = new SimpleDateFormat("MMM-dd-yyyy", Locale.US);

			for (int i = 0; i < events.size(); i++)
			{
				df.format(itemmonth.getTime());
				itemmonth.add(Calendar.DATE, 1);
				// items.add(adrenalinelife.get(i).getStartDate().toString());
			}
			adapter.setItems(events);
			adapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * The Class EventAdapter is the adapter class that is used show list of
	 * Events for a selected date in the ListView.
	 */
	private class EventAdapter extends BaseAdapter
	{

		@Override
		public int getCount()
		{
			return eventSel.size();
		}

		@Override
		public Event getItem(int position)
		{
			return eventSel.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@SuppressLint("RestrictedApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
				convertView = getLayoutInflater(null).inflate(
						R.layout.event_item2, null);

			Event d = getItem(position);

			TextView lbl = (TextView) convertView.findViewById(R.id.txtSmall);
			lbl.setText(d.getTitle());

			lbl = (TextView) convertView.findViewById(R.id.dateSmall);

			// 12:00AM to All Day
			if (Commons.millsToDateTime(d.getStartDateTime()).contains("Today - 12:00 AM")){
				lbl.setText("Today - " + Commons.mToDate(d.getStartDateTime()));
			} else if (Commons.millsToDateTime(d.getStartDateTime()).contains("Monday 12:00 AM")){
				lbl.setText("Monday - " + Commons.mToDate(d.getStartDateTime()));
			}
			else if (Commons.millsToDateTime(d.getStartDateTime()).contains("Tuesday 12:00 AM")){
				lbl.setText("Tuesday - " + Commons.mToDate(d.getStartDateTime()));
			}
			else if (Commons.millsToDateTime(d.getStartDateTime()).contains("Wednesday 12:00 AM")){
				lbl.setText("Wednesday - " + Commons.mToDate(d.getStartDateTime()));
			}
			else if (Commons.millsToDateTime(d.getStartDateTime()).contains("Thursday 12:00 AM")){
				lbl.setText("Thursday - " + Commons.mToDate(d.getStartDateTime()));
			}
			else if (Commons.millsToDateTime(d.getStartDateTime()).contains("Friday 12:00 AM")){
				lbl.setText("Friday - " + Commons.mToDate(d.getStartDateTime()));
			}
			else if (Commons.millsToDateTime(d.getStartDateTime()).contains("Saturday 12:00 AM")){
				lbl.setText("Saturday - " + Commons.mToDate(d.getStartDateTime()));
			}
			else if (Commons.millsToDateTime(d.getStartDateTime()).contains("Sunday 12:00 AM")){
				lbl.setText("Sunday - " + Commons.mToDate(d.getStartDateTime()));
			}
			else {
				lbl.setText(Commons.millsToDateTime(d.getStartDateTime()));
			}


			ImageView img = convertView.findViewById(R.id.imgSmall);
			ImageView img2 = convertView.findViewById(R.id.shadowSmall);
			Picasso.with(getContext()).load(R.drawable.shadow_medium).transform(new PicassoTransform(30,0)).into(img2);
			Picasso.with(getContext()).load(d.getImage()).transform(new PicassoTransform(30,0)).placeholder(R.drawable.no_imagebig).into(img);


			return convertView;
		}

	}

	@Override
	public void onDateChange(int position, long d)
	{
		eventSel.clear();
		if (d > 0)
		{
			String selectedGridDate = CalendarAdapter.dayString.get(position);
			String[] separatedTime = selectedGridDate.split("-");
			String gridvalueString = separatedTime[2].replaceFirst("^0*", "");// taking
																				// last
																				// part
																				// of
																				// date.
																				// ie;
																				// 2
																				// from
																				// 2012-12-02.
			int gridvalue = Integer.parseInt(gridvalueString);
			// navigate to next or previous month on clicking offdays.
			if (gridvalue > 10 && position < 8)
			{
				setPreviousMonth();
				refreshCalendar(getView());
			}
			else if (gridvalue < 7 && position > 28)
			{
				setNextMonth();
				refreshCalendar(getView());
			}
			// Setup Event List according to the Calendar Day picked above.
			for (Event e : events)
			{
				Calendar c1 = Calendar.getInstance();
				c1.setTimeInMillis(d);

				Calendar c2 = Calendar.getInstance();
				c2.setTimeInMillis(e.getStartDateTime());
				// if (e.getStartDateTime() <= d && e.getEndDateTime() >= d)
				if (c1.get(Calendar.DAY_OF_MONTH) == c2
						.get(Calendar.DAY_OF_MONTH)
						&& c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
						&& c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
				{
					eventSel.add(e);
				}
			}
		}
		Log.e("Ev Count=" + eventSel.size());
		((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
	}

}
