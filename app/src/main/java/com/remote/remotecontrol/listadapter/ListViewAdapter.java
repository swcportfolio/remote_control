package com.remote.remotecontrol.listadapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.remote.remotecontrol.R;

import java.util.ArrayList;

/**
 *  ListView Adapter 클래스
 * @author 박상원
 * @since @since 2020. 11. 28
 * @version 1.1
 * @see

 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *      수정일		   수정자             수정내용
 *  =============   ==========      ==============================
 *  2020. 11. 28     박상원              최초 생성
 *
 * ===============================================================
 * </pre>
 */
public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();
    private ViewHolder viewHolder;

    public ListViewAdapter() {
    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        final int position = i;

        if (view == null) {
            LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mLayoutInflater.inflate(R.layout.list_brand_item, viewGroup, false);
            viewHolder =new ViewHolder();
            viewHolder.textView = view.findViewById(R.id.txt_brand);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        ListViewItem listViewItem = listViewItemList.get(position);

        viewHolder.textView.setText(listViewItem.getBrandName());

        return view;
    }

    /**
     * listViewItemList ArrayList Item 추가
     * @param name
     */
    public void addItem(String name) {
        ListViewItem item = new ListViewItem();
        item.setBrandName(name);

        listViewItemList.add(item);
    }

    /**
     * listViewItemList ArrayList 삭제
     */
    public void remove() {

        listViewItemList.clear();
    }

    /**
     * ViewHolder 클래스
     */
    class ViewHolder{
        public TextView textView;
    }
}
