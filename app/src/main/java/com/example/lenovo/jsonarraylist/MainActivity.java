package com.example.lenovo.jsonarraylist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG="ListView";
    private ExpandableListView expListView;
    private ArrayList<JSONData> listOfJson=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "Press download button at top-right corner.", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"onCreate main activity");
        expListView=(ExpandableListView) findViewById(R.id.exp_lv);

    }

    private class JSONAdapter extends BaseExpandableListAdapter {
        class JSONDataHolder {
            TextView tvGroup,tvChild;
        }
        private ArrayList<JSONData> dataArrayList=new ArrayList<>();

        public JSONAdapter(ArrayList<JSONData> dataArrayList) {
            this.dataArrayList = dataArrayList;
        }

        @Override
        public int getGroupCount() {
            return dataArrayList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return 3;
            //to avoid hardcoding this value use different java files for Head and Child data
            //and make their arrayList;
        }

        @Override
        public String getGroup(int i) {
            return dataArrayList.get(i).getTitle();
        }

        @Override
        public JSONData.JSONSubData getChild(int i, int i1) {
            return dataArrayList.get(i).getJsonSubData();
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            LayoutInflater li=getLayoutInflater();
            JSONDataHolder holder;
            Log.d(TAG,"getGroupView called");
            if (view==null) {
                view=li.inflate(R.layout.head_layout,null);
                holder=new JSONDataHolder();
                holder.tvGroup =(TextView) view.findViewById(R.id.title);
                view.setTag(holder);
            } else {
                holder=(JSONDataHolder)view.getTag();
            }
            holder.tvGroup.setText(dataArrayList.get(i).getTitle());
            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            LayoutInflater li=getLayoutInflater();
            JSONDataHolder holder;
            Log.d(TAG,"getChildView called");
            if (view==null) {
                view = li.inflate(R.layout.child_layout, null);
                holder = new JSONDataHolder();
                holder.tvChild = (TextView) view.findViewById(R.id.child_tv);
                view.setTag(holder);
            } else {
                holder= (JSONDataHolder) view.getTag();
            }
            JSONData.JSONSubData jsonSubTemp=dataArrayList.get(i).getJsonSubData();
            switch (i1) {
                case 1:holder.tvChild.setText("Start Time: "+jsonSubTemp.getStartTime());
                    break;
                case 2:holder.tvChild.setText("End Time: "+jsonSubTemp.getEndTime());
                    break;
                case 0:holder.tvChild.setText("State: "+jsonSubTemp.getState());
            }
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater ml=getMenuInflater();
        Log.d(TAG,"Menu created");
        ml.inflate(R.menu.menu_button,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.btn_dnld) {
            Log.d(TAG,"Download button pressed.");
            checkConnection();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    public void checkConnection() {
        ConnectivityManager manager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo=manager.getActiveNetworkInfo();
        if (netInfo!=null && netInfo.isConnected()) {
            JSONDataFetch fetchData=new JSONDataFetch(new JSONDataFetch.PostExecuteListener() {
                @Override
                public void dataFetcher(ArrayList<JSONData> jData) {
                    Log.d(TAG,"dataFetcher"+jData);
                       listOfJson=jData;
                    JSONAdapter listAdapter=new JSONAdapter(listOfJson);
                    expListView.setAdapter(listAdapter);
                }

                @Override
                public Context getContext() {
                    return MainActivity.this;
                }
            });
            fetchData.execute("https://raw.githubusercontent.com/fossasia/open-event-orga-server/master/samples/ots16/sessions.json");

            Log.d(TAG,"setting up list view"+listOfJson);

        } else {
            Toast.makeText(MainActivity.this, "Internet Connection Required", Toast.LENGTH_SHORT).show();
        }


    }

}
