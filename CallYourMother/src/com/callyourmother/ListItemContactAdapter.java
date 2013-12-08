package com.callyourmother;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class ListItemContactAdapter extends ResourceCursorAdapter {
	private final ContentResolver mContentResolver;
	private String TAG = "ListItemContactAdapter";


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// return super.newView(context, cursor, parent);
		LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inf.inflate(R.layout.call_stats_list_item, parent,false);
	}

	public ListItemContactAdapter(Context context, int layout, Cursor c,
			int flags) {
		super(context, layout, c, flags);
		mContentResolver = context.getContentResolver();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView name = (TextView) view.findViewById(R.id.tvName);
		TextView date = (TextView) view.findViewById(R.id.tvLastDateCalled);
		TextView timesCalled = (TextView) view.findViewById(R.id.tvNumTimesCalled);
		TextView phoneNumber = (TextView) view.findViewById(R.id.tvPhoneNumber);
		
		name.setText(cursor.getString(cursor.getColumnIndex("")));
		date.setText(cursor.getString(cursor.getColumnIndex("")));
		timesCalled.setText(cursor.getString(cursor.getColumnIndex("")));
		phoneNumber.setText(cursor.getString(cursor.getColumnIndex("")));
		
	}

}
