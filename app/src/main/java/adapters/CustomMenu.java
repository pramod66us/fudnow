package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.foodzone.R;
import com.test.foodzone.Items;

/**
 * Created by User on 11/4/2014.
 */
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
            view= inflater.inflate(R.layout.custom_menu, null, true);
        }


        TextView txtTitle = (TextView) view.findViewById(R.id.itemName);
        txtTitle.setText(names[i]);
        return view;

    }
}
