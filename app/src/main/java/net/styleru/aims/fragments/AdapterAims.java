package net.styleru.aims.fragments;

import android.app.LauncherActivity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import net.styleru.aims.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import static android.support.v4.content.ContextCompat.getColor;
import static net.styleru.aims.fragments.AimsFragment.DATE;
import static net.styleru.aims.fragments.AimsFragment.DESCRIPTION;
import static net.styleru.aims.fragments.AimsFragment.TITLE;


// Убрать хешмеп и сделать 2 листа. Наследовать от эррей адаптера, как в статье на хабре




/**
 * Created by LeonidL on 16.10.16.
 */
public class AdapterAims extends ArrayAdapter<HashMap<String, String>> {

    private final Context context;
    private List<HashMap<String, String>> targets;
    View rowView;

    int resource;

    public AdapterAims(Context context, int resource, List<HashMap<String, String>> data){
        super(context, resource, data);

        this.context = context;
        targets = data;

        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      //  View rowView1 = inflater.inflate(R.layout.aims_item, parent, false);
        //View rowView2 = inflater.inflate(R.layout.aims_item_2, parent, false);

      //  TextView textView1 = (TextView)rowView1.findViewById(R.id.target_aims);
      //  TextView textView2 = (TextView)rowView2.findViewById(R.id.target_aims2);
      //  if(position != 0) {
        rowView = inflater.inflate(resource, parent, false);
        TextView header = (TextView)rowView.findViewById(R.id.target_aims);
        TextView description = (TextView) rowView.findViewById(R.id.date_description);
        TextView date = (TextView) rowView.findViewById(R.id.date_aims);

        HashMap<String, String> aimMap = targets.get(position);
        header.setText(aimMap.get(TITLE));
        description.setText(aimMap.get(DESCRIPTION));

        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("EEE MMM dd HH:mm:ss z yyyy");
        try {
            Date aimDate = format.parse(aimMap.get(DATE));
            SimpleDateFormat printDate = new SimpleDateFormat("dd.MM.yyyy");
            date.setText("Выполнить до: " + printDate.format(aimDate));
        } catch (ParseException e) {
            date.setText("Выполнить до: " + aimMap.get(DATE));
            e.printStackTrace();
        }

//                textView1.setText((CharSequence) targets.get(position));
//            return rowView1;
//        }
//        else {
//            rowView = inflater.inflate(R.layout.aims_item_2, parent, false);
//            TextView textView = (TextView)rowView.findViewById(R.id.target_aims2);
//            textView.setText(targets.get(position).get("targetname"));
////            textView2.setText((CharSequence) targets.get(position));
////            return rowView2;
//        }

        return rowView;
    }
}


