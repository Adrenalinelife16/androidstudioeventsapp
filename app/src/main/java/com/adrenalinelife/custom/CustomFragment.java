package com.adrenalinelife.custom;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.adrenalinelife.utils.ImageLoader;

/**
 * This is a common Fragment that all other Fragments of the app can extend to
 * inherit the common behaviors like implementing a common interface that can be
 * used in all child Fragments.
 */
public class CustomFragment extends Fragment implements OnClickListener
{
	
	/** The parent activity of this fragment. */
	public CustomActivity parent;
	
	/** The default no img bitmap. */
	protected static Bitmap bmNoImg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		parent = (CustomActivity) getActivity();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/** The iamge loader. */
	protected static ImageLoader loader;
	
	/** The Bundle arg. */
	private Bundle arg;

	/**
	 * Sets the arg.
	 *
	 * @param arg the new arg
	 */
	public void setArg(Bundle arg)
	{

		this.arg = arg;
	}

	/**
	 * Gets the arg.
	 *
	 * @return the arg
	 */
	public Bundle getArg()
	{

		return arg;
	}

	/**
	 * Sets the touch n click.
	 *
	 * @param v the v
	 * @return the view
	 */
	public View setTouchNClick(View v)
	{

		v.setOnClickListener(this);
		v.setOnTouchListener(CustomActivity.TOUCH);
		return v;
	}

	@Override
	public void onClick(View v)
	{

	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		if (loader != null)
			loader.clear();
	}

}
