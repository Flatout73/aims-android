package net.styleru.aims.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.styleru.aims.R;

import java.util.List;

/**
 * Created by LeonidL on 12.10.16.
 */
public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.Expansesholder> {

    private List<Alert> alertList;

    public AlertAdapter(List<Alert> alertList) {
        this.alertList = alertList;
    }

    @Override
    public AlertAdapter.Expansesholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);

        return new Expansesholder(itemView);
    }

    @Override
    public void onBindViewHolder(AlertAdapter.Expansesholder holder, int position) {

        Alert alert = alertList.get(position);
        holder.target.setText(alert.alert_target);
        holder.date.setText(alert.alert_date);

        final String alert_n = alert.alert_target;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context  = view.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(alert_n)
                        .setCancelable(false)
                        .setNegativeButton("Закрыть",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public class Expansesholder extends RecyclerView.ViewHolder {

        public TextView target;
        public TextView date;
        public Expansesholder(View itemView) {
            super(itemView);

            target = (TextView) itemView.findViewById(R.id.target);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
