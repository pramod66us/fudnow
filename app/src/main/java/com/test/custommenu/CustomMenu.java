package com.test.custommenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.foodzone.Items;
import com.test.foodzone.R;

public class CustomMenu extends BaseAdapter {

    private Context context;
    private static LayoutInflater inflater = null;
    private String[] names;

    public CustomMenu(Items indexActivity, String[] names){
        context = indexActivity;
        this.names = names;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int i) {
        return names[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= inflater.inflate(R.layout.item_list, null, true);
        }


        TextView txtTitle = (TextView) view.findViewById(R.id.textView1);
        txtTitle.setText(names[i]);
        return view;

    }
}
