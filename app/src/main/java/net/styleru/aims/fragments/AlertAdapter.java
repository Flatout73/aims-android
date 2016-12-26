package net.styleru.aims.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.styleru.aims.FriendActivity;
import net.styleru.aims.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.User;

/**
 * Created by LeonidL on 12.10.16.
 */
public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.Expansesholder> {

    private List<User> friendList;

    public AlertAdapter(List<User> friendList) {
        this.friendList = friendList;
    }

    @Override
    public AlertAdapter.Expansesholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);

        return new Expansesholder(itemView);
    }

    @Override
    public void onBindViewHolder(AlertAdapter.Expansesholder holder, final int position) {

        User friend = friendList.get(position);
        holder.avatar.setImageBitmap(friend.getImage());
        holder.target.setText(friend.getName());
        holder.date.setText(friend.getLogin());

//        final String alert_n = friend.alert_target;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context  = view.getContext();
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setMessage(alert_n)
//                        .setCancelable(false)
//                        .setNegativeButton("Закрыть",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//                                    }
//                                });
//                AlertDialog alert = builder.create();
//                alert.show();

                Intent intent = new Intent(context, FriendActivity.class);
                intent.putExtra("User", friendList.get(position).getLogin());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class Expansesholder extends RecyclerView.ViewHolder {

        public TextView target;
        public TextView date;
        public CircleImageView avatar;
        public Expansesholder(View itemView) {
            super(itemView);

            target = (TextView) itemView.findViewById(R.id.target);
            date = (TextView) itemView.findViewById(R.id.date);
            avatar = (CircleImageView) itemView.findViewById(R.id.avatar_friend);
        }
    }
}
