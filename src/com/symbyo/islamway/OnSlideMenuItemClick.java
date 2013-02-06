package com.symbyo.islamway;

import com.symbyo.islamway.fragments.SlideMenuFragment.SlideMenuItem;

/**
 * Activities that incorporate the SlideMenu fragment must implement this
 * interface.
 * 
 * @author kdehairy
 * 
 */
public interface OnSlideMenuItemClick {
	void onSlideMenuItemClick( SlideMenuItem item );
}
