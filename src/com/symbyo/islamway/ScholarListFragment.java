package com.symbyo.islamway;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockListFragment;
import com.symbyo.islamway.adapters.ScholarsAdapter;

public class ScholarListFragment extends SherlockListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d("Islamway", "onCreate called");
		super.onActivityCreated(savedInstanceState);
		// get the quran scholars list from the database.
		new AsyncTask<Void, Void, ListAdapter>() {

			@Override
			protected void onPostExecute(ListAdapter result) {
				ScholarListFragment.this.setListAdapter(result);
			}

			@Override
			protected ListAdapter doInBackground(Void... params) {
				
				return new ScholarsAdapter(getActivity());
			}
		}.execute();
	}
	
}
