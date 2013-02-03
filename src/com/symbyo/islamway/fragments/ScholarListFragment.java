package com.symbyo.islamway.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.symbyo.islamway.adapters.ScholarsAdapter;
import com.symbyo.islamway.service.IWService.Section;

public class ScholarListFragment extends SherlockListFragment {

	public final static String SECTION_KEY = "section";
	
	private Section mSection;
	//private ListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// we set the retain instance to true so as not to populate the list
		// from the database everytime the configuration changes.
		setRetainInstance(true);
		Log.d("Islamway", "onCreate called");
		super.onCreate(savedInstanceState);
		
		// get the section
		int section_index = getArguments().getInt(SECTION_KEY, -1);
		if (section_index < 0) {
			mSection = Section.QURAN;
		} else {
			mSection = Section.values()[section_index];
		}

		// get the quran scholars list from the database.
		new AsyncTask<Void, Void, ListAdapter>() {

			@Override
			protected void onPostExecute(ListAdapter result) {
				//ScholarListFragment.this.mAdapter = result;
				ScholarListFragment.this.setListAdapter(result);
			}

			@Override
			protected ListAdapter doInBackground(Void... params) {
				if (getActivity() == null) {
					return null;
				}
				@SuppressWarnings("null")
				ListAdapter adapter = new ScholarsAdapter(getActivity(),
						mSection);
				return adapter;
			}
		}.execute();
	}
}
