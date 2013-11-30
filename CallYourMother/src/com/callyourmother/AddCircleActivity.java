package com.callyourmother;

import java.util.List;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


public class AddCircleActivity extends Activity {
	ListView listView2;
	CircleAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_circle);
		
		listView2 = (ListView)findViewById(R.id.listView2);		      
		View header = (View)getLayoutInflater().inflate(R.layout.add_header, null);
		listView2.addHeaderView(header);
		View footerView = (View)getLayoutInflater().inflate(R.layout.add_footer, null);
		listView2.addFooterView(footerView);
		listView2.setAdapter(mAdapter);
		
		// set up occurance spinner
		Spinner spinner = (Spinner) findViewById(R.id.reoccuranceSpinner);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
		        R.array.occurance_array, android.R.layout.simple_spinner_item);
	
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
	
	}
	

}