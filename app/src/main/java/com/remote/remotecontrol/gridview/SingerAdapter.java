package com.remote.remotecontrol.gridview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class SingerAdapter extends BaseAdapter {
    ArrayList<SingerItem> items = new ArrayList<SingerItem>();
    Context context;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SingerItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SingerViewer singerViewer = new SingerViewer(context);
        singerViewer.setItem(items.get(i));
        return singerViewer;
    }

    public void SingerAdapter(Context context){
        this.context = context;
    }
    public void addItem(SingerItem singerItem){
        items.add(singerItem);
    }
    public void remove(){
        items.clear();
    }
    public void removeItem(int position){items.remove(position);
    }

}