package net.styleru.aims.fragments;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.util.EventLogTags;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.styleru.aims.AboutTargetActivity;
import net.styleru.aims.LoginActivity;
import net.styleru.aims.MainActivity;
import net.styleru.aims.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.Aim;

import static net.styleru.aims.LoginActivity.APP_REFERENCE_Token;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AimsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AimsFragment extends Fragment {
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

    List<Aim> myAims;

    public AimsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AimsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AimsFragment newInstance(String param1, String param2) {
        AimsFragment fragment = new AimsFragment();
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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_aims, container, false);
        NestedScrollingListView listView = (NestedScrollingListView) view.findViewById(R.id.aimsListView);
        HashMap<String, String> hm;

        synchronized (DataStorage.class) {
            try {
                myAims = DataStorage.getMe().getAims();

                for (int i = 0; i < myAims.size(); i++) {
                    Aim currentAim = myAims.get(i);
                    hm = new HashMap<>();
                    hm.put(TITLE, currentAim.getHeader());
                    hm.put(DATE, currentAim.getDate().toString());
                    mTargetList.add(hm);
                }
            } catch (Exception ex) {
                Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }

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

       // SimpleAdapter adapter = new SimpleAdapter(getActivity(), mTargetList, R.layout.aims_item, new String[]{TITLE, DATE}, new int[] {R.id.target_aims, R.id.date_aims});
        AdapterAims adapterAims = new AdapterAims(getActivity(), R.layout.aims_item, mTargetList);
       // listView.setAdapter(adapter);
        listView.setAdapter(adapterAims);
        listView.setOnItemClickListener(itemClickListener);
        listView.setNestedScrollingEnabled(true);
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
