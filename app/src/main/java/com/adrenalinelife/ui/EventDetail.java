package com.adrenalinelife.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adrenalinelife.BookTkt;
import com.adrenalinelife.Login;
import com.adrenalinelife.R;
import com.adrenalinelife.custom.CustomFragment;
import com.adrenalinelife.model.Event;
import com.adrenalinelife.utils.Commons;
import com.adrenalinelife.utils.Const;
import com.adrenalinelife.utils.ImageLoader;
import com.adrenalinelife.utils.Log;
import com.adrenalinelife.utils.StaticData;
import com.adrenalinelife.utils.Utils;
import com.adrenalinelife.web.WebHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.adrenalinelife.web.WebAccess.GET_FAV_EVENTS;
import static com.adrenalinelife.web.WebAccess.executePostRequest;
import static com.adrenalinelife.web.WebAccess.getUserParams;

/**
 * The Class EventDetail is the Fragment class that shows the details about an
 * Event. This Fragment is used inside the EventDetailActivity class. It also
 * show a Map with a marker on map for showing the location of that event. You
 * need to write your own logic for loading actual contents related to Events
 * and also need to show actual location for Event.
 */
public class EventDetail extends CustomFragment
{

	/** The map view. */
	private MapView mMapView;

	/** The Google map. */
	private GoogleMap mMap;

	/** The e. */
	private Event e;

	private Uri shareImageUri;

	public ImageView imgShare;


	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.event_detail, null);
		e = (Event) getArg().getSerializable(Const.EXTRA_DATA);
		setHasOptionsMenu(true);

		setTouchNClick(v.findViewById(R.id.btnReg));

		showDetails(v);
		setupMap(v, savedInstanceState);

		return v;
	}

	/**
	 * Show details.
	 *
	 * @param v the v
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	private void showDetails(View v)
	{
		TextView lbl = (TextView) v.findViewById(R.id.lblTitle);
		lbl.setText(e.getTitle());

		lbl = (TextView) v.findViewById(R.id.lblAddress);
		lbl.setText(e.getLocation());

		Log.e("Description = ", e.getDesc());
		lbl = (TextView) v.findViewById(R.id.lblDesc);
		lbl.setText(e.getDesc());

		lbl = (TextView) v.findViewById(R.id.lblDate);
		if (e.getStartDate().equalsIgnoreCase(e.getEndDate()))
			lbl.setText(Commons.millsToDate(e.getStartDateTime()) + " "
					+ Commons.millsToTime(e.getStartDateTime()) + " - "
					+ Commons.millsToTime(e.getEndDateTime()));
		else
			lbl.setText(Commons.millsToDateTime(e.getStartDateTime()) + " to "
					+ Commons.millsToDateTime(e.getEndDateTime()));
/*
		lbl = (TextView) v.findViewById(R.id.lblCost);
		lbl.setText(e.getPrice() == 0 ? getString(R.string.free_event) : "$"
				+ e.getPrice());
*/
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause()
	{
		mMapView.onPause();
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		mMapView.onDestroy();
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		super.onResume();
		mMapView.onResume();

		mMap = mMapView.getMap();
		if (mMap != null)
		{
			mMap.setMyLocationEnabled(true);
			mMap.setInfoWindowAdapter(null);
			setupMarker();
		}
	}

	/**
	 * Setup and initialize the Google map view.
	 * 
	 * @param v
	 *            the root view
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	private void setupMap(View v, Bundle savedInstanceState)
	{
		try {
			MapsInitializer.initialize(getActivity());
		}catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		mMapView = (MapView) v.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
	}
	/**
	 * This method simply place a few dummy location markers on Map View. You
	 * can write your own logic for loading the locations and placing the marker
	 * for each location as per your need.
	 */
	private void setupMarker()
	{
		mMap.clear();
		LatLng ll = new LatLng(e.getLatitude(), e.getLongitude());
		MarkerOptions opt = new MarkerOptions();
		opt.position(ll).title(e.getTitle()).snippet(e.getLocation());
		opt.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));

		mMap.addMarker(opt);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
	{
		if (StaticData.pref.contains(Const.USER_ID)) {
			inflater.inflate(R.menu.share_fav, menu);
			final ProgressDialog dia = parent
					.showProgressDia(R.string.alert_loading);
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					final String id = '"' + e.getId() + '"';
					final String s = checkFavoriteEvents(e);
					parent.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dia.dismiss();

							if (s.contains(id)){
								menu.findItem(R.id.menu_fav).setIcon(R.drawable.ic_fav_orange);
								e.setFav(true);
							}
							else if (!s.contains(id)){
								menu.findItem(R.id.menu_fav).setIcon(R.drawable.ic_fav);
								e.setFav(false);
							}
						}
					});
				}
			}).start();
		} else {
            inflater.inflate(R.menu.share, menu);
        }
		super.onCreateOptionsMenu(menu, inflater);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == R.id.menu_share)
		{
			getLocalBitmapUri();
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("image/*");
			i.putExtra(Intent.EXTRA_STREAM, shareImageUri);
			i.putExtra(Intent.EXTRA_TEXT, e.getTitle() + " - " + "Find more local events and activities like this one by downloading the Adrenaline Life app now! #FindYourLife" + " - " + "www.onelink.to/life");
			startActivity(Intent.createChooser(i, getString(R.string.share)));
		}
		else //(item.getItemId() == R.id.menu_fav)
		{
			e.setFav(!e.isFav());
			if (e.isFav())
			{
				item.setIcon(R.drawable.ic_fav_orange);
				Toast.makeText(parent, R.string.msg_add_fav, Toast.LENGTH_SHORT).show();
			}
			else
			{
				item.setIcon(R.drawable.ic_fav);
				item.setTitle(R.string.add_to_fav);
				Toast.makeText(parent, R.string.msg_rem_fav, Toast.LENGTH_SHORT).show();
			}
			final ProgressDialog dia = parent
					.showProgressDia(R.string.alert_loading);
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					String id = e.getId();
					WebHelper.addRemoveFavorite(id);
					parent.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dia.dismiss();
							if (e.getId() == null) {
								Utils.showDialog(parent, StaticData.getErrorMessage());
							}
							else {
								Log.e("Fav is not null");
							}
						}
					});
				}
			}).start();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.btnReg)
		{
			bookTicket();
		}
	}
	/**
	 * Book ticket.
	 */
	private void bookTicket()
	{
		if (e.isBooked())
			Utils.showDialog(parent, R.string.err_event_booked);
		else if (e.getAvailSpace() <= 0)
			Utils.showDialog(parent, R.string.err_no_space);
		else if (e.getEndDateTime() < System.currentTimeMillis())
			Utils.showDialog(parent, R.string.err_past_event);
		else if (!StaticData.pref.contains(Const.USER_ID))
			startActivityForResult(new Intent(parent, Login.class),
					Const.REQ_LOGIN);
		else
		{
			final ProgressDialog dia = parent
					.showProgressDia(R.string.alert_wait);
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					final boolean book = WebHelper.isBooked(e);
					parent.runOnUiThread(new Runnable() {
						@Override
						public void run()
						{
							dia.dismiss();
							if (book)
								Utils.showDialog(parent,
										R.string.err_event_booked);
							else
								startActivity(new Intent(parent, BookTkt.class)
										.putExtra(Const.EXTRA_DATA, e));
						}
					});
				}
			}).start();
		}
	}

	public static String checkFavoriteEvents(Event z)
	{
		try
		{
			Log.e("EventDetail checkFavoriteEvents");
			ArrayList<NameValuePair> param = getUserParams();
			param.add(new BasicNameValuePair("page", "1"));
			param.add(new BasicNameValuePair("page_size", "30"));
			String res = executePostRequest(GET_FAV_EVENTS, param, true);
			return res;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Const.REQ_LOGIN && resultCode == Activity.RESULT_OK)
			bookTicket();
	}

	public Uri getLocalBitmapUri() {
		// Extract Bitmap from ImageView drawable
		Bitmap bmp;
		if (e.getImage() != "") {
			bmp = loader.loadImage(e.getImage(),
					new ImageLoader.ImageLoadedListener() {
						@Override
						public void imageLoaded(Bitmap bm)
						{
							if (bm != null)
								Toast.makeText(parent, "No BitMap", Toast.LENGTH_SHORT).show();
								imgShare.setImageBitmap(bmNoImg);
						}
					});
		} else {
			//Get Bitmap for Drawable File no_image.png
			bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_imagebig);
		}
		// Store image to default external storage directory
		Uri bmpUri = null;
		try {
			File file =  new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
			bmpUri = Uri.fromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}


		shareImageUri = bmpUri;
		return bmpUri;

	}

}
