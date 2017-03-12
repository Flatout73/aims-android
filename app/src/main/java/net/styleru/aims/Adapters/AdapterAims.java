package net.styleru.aims.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.styleru.aims.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.aimsproject.models.Aim;

import static net.styleru.aims.fragments.MyPageFragment.DATE;
import static net.styleru.aims.fragments.MyPageFragment.DESCRIPTION;
import static net.styleru.aims.fragments.MyPageFragment.TITLE;


// Убрать хешмеп и сделать 2 листа. Наследовать от эррей адаптера, как в статье на хабре




/**
 *  Этот адаптер нужен чтобы отображать цели на Моей странице и в новостной ленте
 */
public class AdapterAims extends ArrayAdapter<HashMap<String, String>> {

    private final Context context;
    private List<HashMap<String, String>> targets;
    View rowView;

    int resource;

    /**
     *  Дефолтный конструктор
     */
    List<Aim> aims;
    public AdapterAims(Context context, int resource, List<HashMap<String, String>> data){
        super(context, resource, data);

        this.context = context;
        targets = data;

        this.resource = resource;
    }

    /**
     * Конструктор для ленты новостей и моей страницы с Листом целлей
     */
    public AdapterAims(Context context, int resource, List<HashMap<String, String>> data, List<Aim> aims) {
        super(context, resource, data);

        this.context = context;
        targets = data;

        this.resource = resource;

        this.aims = aims;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      //  View rowView1 = inflater.inflate(R.layout.aims_item, parent, false);
        //View rowView2 = inflater.inflate(R.layout.aims_item_feed, parent, false);

      //  TextView textView1 = (TextView)rowView1.findViewById(R.id.target_aims);
      //  TextView textView2 = (TextView)rowView2.findViewById(R.id.target_aims2);
      //  if(position != 0) {
        rowView = inflater.inflate(resource, parent, false);
        TextView header = (TextView)rowView.findViewById(R.id.target_aims);
        TextView description = (TextView) rowView.findViewById(R.id.date_description);
        TextView date = (TextView) rowView.findViewById(R.id.date_aims);
        CircleImageView avatar = (CircleImageView) rowView.findViewById(R.id.friend_avatar);
        TextView nameAuthor = (TextView) rowView.findViewById(R.id.author_name);

        HashMap<String, String> aimMap = targets.get(position);
        header.setText(aimMap.get(TITLE));
        description.setText(aimMap.get(DESCRIPTION));

        // Приводим дату в нужный формат
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            Date aimDate = format.parse(aimMap.get(DATE));
            SimpleDateFormat printDate = new SimpleDateFormat("dd.MM.yyyy");
            date.setText("Выполнить до: " + printDate.format(aimDate));
        } catch (ParseException e) {
            date.setText("Выполнить до: " + aimMap.get(DATE));
            e.printStackTrace();
        }

        //если есть цели
        if(aims != null && !aims.isEmpty()) {
            Aim aim = aims.get(position);
            //для отображения аваторк и имени в новостной ленте
            if(resource == R.layout.aims_item_feed) {
                Bitmap avatMin = aim.getAuthor().getImageMin();
                if(avatMin == null) {
                    //avatar.setVisibility(View.GONE);
                }
                else
                    avatar.setImageBitmap(avatMin);
                nameAuthor.setText(aim.getAuthor().getName());
            }

            //В зависимости от прогресса цели меняется цвет заднего фона
            if(aim.getFlag() == 1) {
                rowView.setBackgroundColor(Color.YELLOW);
            } else if (aim.getFlag() == 2) {
                rowView.setBackgroundColor(Color.GREEN);
            } else if (aim.getFlag() == 3) {
                rowView.setBackgroundColor(Color.RED);
            }
        }

//                textView1.setText((CharSequence) targets.get(position));
//            return rowView1;
//        }
//        else {
//            rowView = inflater.inflate(R.layout.aims_item_feed, parent, false);
//            TextView textView = (TextView)rowView.findViewById(R.id.target_aims2);
//            textView.setText(targets.get(position).get("targetname"));
////            textView2.setText((CharSequence) targets.get(position));
////            return rowView2;
//        }

        return rowView;
    }
}


