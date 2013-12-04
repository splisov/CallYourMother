package com.callyourmother;

import java.util.ArrayList;
import java.util.List;

import com.callyourmother.data.Circle;
import com.callyourmother.data.Contact;
import com.callyourmother.data.DatabaseClient;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<Contact> {

	private final List<Contact> mItems = new ArrayList<Contact>();
	private final Context mContext;
	int layoutResourceId;
	private DatabaseClient db;

	
    public ContactAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        mContext = context;
        this.db = new DatabaseClient(context);
        
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

		Log.i("DEBUG", "Getting LayoutView for Contact");
		
		itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.contact_item, parent, false);
	

		TextView nameView = (TextView) itemLayout.findViewById(R.id.contact_name_view);
		nameView.setText(getItem(position).getDisplayName());
	
		
		TextView numberView = (TextView) itemLayout.findViewById(R.id.contact_number_view);
		numberView.setText(getItem(position).getAllPhoneNumbers()); //ONLY GRAB FIRST CONTACT
		
		
		ImageView photoView = (ImageView) itemLayout.findViewById(R.id.contact_image_view);
		if (getItem(position).getPhoto() == null){
			photoView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.android));
		} else {
			photoView.setImageBitmap(getItem(position).getPhoto());
		}
		
		TextView deleteContact = (TextView) itemLayout.findViewById(R.id.delete_contact_view);
		deleteContact.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						
					}
		});
		
		
		return itemLayout;
	}
}
