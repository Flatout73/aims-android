package net.styleru.aims.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import net.styleru.aims.AboutTargetActivity;
import net.styleru.aims.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mListView;
    private ArrayList<HashMap<String, String>> mTargetList = new ArrayList<>();
    private static final String TITLE = "targetname";
    private static final String DATE = "date";


    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        NestedScrollingListView listView = (NestedScrollingListView) view.findViewById(R.id.friendsListView);

        HashMap<String, String> hm;
        hm = new HashMap<>();
        hm.put(TITLE, "Cinema");
        hm.put(DATE, "Tomorrow");
        mTargetList.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);
        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);

        AdapterAims adapterAims = new AdapterAims(getActivity(), R.layout.aims_item_2, mTargetList);
        listView.setAdapter(adapterAims);
        listView.setOnItemClickListener(itemClickListener);
        listView.setNestedScrollingEnabled(false);

        return view;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), AboutTargetActivity.class);
            startActivity(intent);
        }
    };
}