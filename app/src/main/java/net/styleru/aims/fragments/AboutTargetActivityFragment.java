package net.styleru.aims.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.styleru.aims.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutTargetActivityFragment extends Fragment {

    private TextView tvProgressHorizontal;

    public AboutTargetActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Цель");

        View view = inflater.inflate(R.layout.fragment_about_target, container, false);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.target_progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvProgressHorizontal = (TextView) view.findViewById(R.id.progress_horizontal);
        tvProgressHorizontal.setText("20%");
        return view;
    }
}
