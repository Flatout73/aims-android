package net.styleru.aims;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.User;

/**
 * Created by LeonidL on 24.12.16.
 */

public class SearchAdapter extends BaseAdapter implements Filterable {


    private List<User> mResult;
    private Context context;

    public User getUser(int i) {
        return mResult.get(i);
    }

    public SearchAdapter(Context context){
        super();
        mResult = new ArrayList<>();
        mResult.add(new User("kek", "lol", null, 1, null, null, 0, 0));
        this.context = context;
    }
    @Override
    public int getCount() {
        return mResult.size();
    }

    @Override
    public User getItem(int position) {
        return mResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.search_item, parent, false);
        }
        User user = getItem(position);
        ((TextView)convertView.findViewById(R.id.search_name)).setText(user.getName());
        CircleImageView avatar = (de.hdodenhof.circleimageview.CircleImageView)convertView.findViewById(R.id.search_avatar);
        avatar.setImageBitmap(user.getImageMin());

        return convertView;

    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    try {
                        List<User> users = RequestMethods.search(constraint.toString());
                        filterResults.values = users;
                        filterResults.count = users.size();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0 ) {
                    mResult = (List<User>) results.values;
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
