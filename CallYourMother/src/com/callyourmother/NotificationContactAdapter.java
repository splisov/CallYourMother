package com.callyourmother;

import java.util.ArrayList;
import java.util.List;

import com.callyourmother.data.Circle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationContactAdapter extends ArrayAdapter<Contact> {

	private final List<Contact> mItems = new ArrayList<Contact>();
	private final Context mContext;
	int layoutResourceId;
	private DatabaseClient db;

	
    public NotificationContactAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        mContext = context;
        //this.db = new DatabaseClient(context);
        
    }

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Contact getItem(int pos) {
		return mItems.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public void add(Contact contact) {
		mItems.add(contact);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemLayout;
		//final Circle circle = mItems.get(position);

		itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
	

		TextView nameView = (TextView) itemLayout.findViewById(R.id.contact_name_view);
		nameView.setText(getItem(position).getDisplayName());
		
		TextView numberView = (TextView) itemLayout.findViewById(R.id.contact_number_view);
		numberView.setText(getItem(position).getPhones().get(0).toString()); //just display first number
		
		ImageView photoView = (ImageView) itemLayout.findViewById(R.id.contact_image_view);
		photoView.setImageBitmap(getItem(position).getPhoto());
		
		
		return itemLayout;
	}
}
