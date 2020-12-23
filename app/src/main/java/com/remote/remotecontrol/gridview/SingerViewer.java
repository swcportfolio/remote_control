package com.remote.remotecontrol.gridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.remote.remotecontrol.R;

public class SingerViewer extends LinearLayout { // gird_item 과 인플레이션

    TextView txt_brandName;
    ImageView imageView;
    public SingerViewer(Context context) {
        super(context);
        init(context);
    }

    public SingerViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_main_item,this,true);
        txt_brandName = (TextView)findViewById(R.id.txt_nickName);
        imageView = (ImageView) findViewById(R.id.image_device);
    }

    public void setItem(SingerItem singerItem){
        txt_brandName.setText(singerItem.getName());
        imageView.setImageResource(singerItem.getImage());
    }

}
