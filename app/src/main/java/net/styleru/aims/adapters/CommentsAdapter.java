package net.styleru.aims.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import net.styleru.aims.R;

import java.util.List;

import ru.aimsproject.models.Comment;

/**
 * Created by LeonidL on 24.12.16.
 */


public class CommentsAdapter extends ArrayAdapter<Comment>{


    private final Context context;
    private List<Comment> comments;
    View view;
    int resource;

    public CommentsAdapter(Context context, int resource, List<Comment> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.comments = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(resource, parent, false);

        TextView name = (TextView) view.findViewById(R.id.comment_name);
        TextView text = (TextView) view.findViewById(R.id.comment_text);

        name.setText(comments.get(position).getAuthorName());
        text.setText(comments.get(position).getText());

        return view;
    }
}
