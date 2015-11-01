package com.test.poptab;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.test.poptab.PopTabView.OnPopSwitchListener;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private final String TAG = PlaceholderFragment.class.getSimpleName();
		
		private PopTabView ptvMain;
		
		private List<PopModel> pops = null;
		
		private PopAdapter mAdapter;
		
		private String startColor = "#FF808080";
		private String endColor = "#FF6992d4";
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			ptvMain = (PopTabView) rootView.findViewById(R.id.ptv_main);
			
			pops = new ArrayList<PopModel>();
			for(int i = 0; i < 4; i ++){
				PopModel pop = new PopModel();
				pop.setId(i);
				pop.setName("Name" + i);
				pop.setScore(i * 10);
				pops.add(pop);
			}
			
			mAdapter = new PopAdapter();
			ptvMain.setmAdapter(mAdapter);
			ptvMain.notifyDataSetChanged();
			
			ptvMain.setOnPopSwitchListener(new OnPopSwitchListener() {
				
				@Override
				public void onPopSwitch(int position) {
					Toast.makeText(getActivity(), "选中" + position, Toast.LENGTH_SHORT).show();
				}
				
				@Override
				public void onPopSelectedAgain(int position) {
					Toast.makeText(getActivity(), "点击" + position, Toast.LENGTH_SHORT).show();
				}
				
				@SuppressLint("NewApi")
				@Override
				public void onAnimationUpdate(View narrowView, View enlargeView,
						float franch) {
					Log.e(TAG, "update-" + franch);
					String narrowColor = ColorUtil.caculateColor(endColor, startColor, franch);
					String enlargeColor = ColorUtil.caculateColor(startColor, endColor, franch);
					
					int strokeWidth = 5; // 3dp 边框宽度
				    int roundRadius = 15; // 8dp 圆角半径
				    int strokeColor = Color.parseColor("#2E3135");//边框颜色

				    GradientDrawable gd = new GradientDrawable();//创建drawable
				    gd.setShape(GradientDrawable.OVAL);
				    gd.setColor(Color.parseColor(narrowColor));
//				    gd.setCornerRadius(roundRadius);
//				    gd.setStroke(strokeWidth, strokeColor);
				    
				    GradientDrawable gd2 = new GradientDrawable();//创建drawable
				    gd2.setShape(GradientDrawable.OVAL);
				    gd2.setColor(Color.parseColor(enlargeColor));
//				    gd2.setCornerRadius(roundRadius);
//				    gd2.setStroke(strokeWidth, strokeColor);
				    
				    if(Build.VERSION.SDK_INT < 16){
				    	narrowView.setBackgroundDrawable(gd);
					    enlargeView.setBackgroundDrawable(gd2);
				    }else{
				    	 narrowView.setBackground(gd);
						 enlargeView.setBackground(gd2);
				    }
				    
				    
//				    Drawable drawable1 = getResources().getDrawable(R.drawable.bg_circle_blue_selector);
//				    // 这一步必须要做,否则不会显示.
//					drawable1.setBounds(0, 0, narrowView.getMinimumWidth(), narrowView.getMinimumHeight());
//					
//					Drawable drawable2 = getResources().getDrawable(R.drawable.bg_circle_blue_selector);
//				    // 这一步必须要做,否则不会显示.
//					drawable2.setBounds(0, 0, enlargeView.getMinimumWidth(), enlargeView.getMinimumHeight());
//					if(Build.VERSION.SDK_INT < 16){
//				    	narrowView.setBackgroundDrawable(drawable1);
//					    enlargeView.setBackgroundDrawable(drawable2);
//				    }else{
//				    	 narrowView.setBackground(drawable1);
//						 enlargeView.setBackground(drawable2);
//				    }
//					narrowView.setSelected(false);
//					enlargeView.setSelected(false);
				}
			});
			
			return rootView;
		}
		
		class PopAdapter extends BaseAdapter{

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return pops.size();
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return pops.get(position);
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_pop_item, null);
				TextView tvId = (TextView) convertView.findViewById(R.id.tv_pop_item_id);
				TextView tvName = (TextView) convertView.findViewById(R.id.tv_pop_item_name);
				TextView tvScore = (TextView) convertView.findViewById(R.id.tv_pop_item_score);
				
				tvId.setText(pops.get(position).getId() + "");
				tvName.setText(pops.get(position).getName());
				tvScore.setText(pops.get(position).getScore() + "分");
				
				return convertView;
			}
			
		}
	}

}
