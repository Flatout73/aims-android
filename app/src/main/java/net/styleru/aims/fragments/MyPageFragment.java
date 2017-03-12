package net.styleru.aims.fragments;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.styleru.aims.AboutTargetActivity;
import net.styleru.aims.adapters.AdapterAims;
import net.styleru.aims.R;
import net.styleru.aims.myviews.NestedScrollingListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.Aim;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String ID = "id";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mListView;
    private ArrayList<HashMap<String, String>> mTargetList = new ArrayList<>();

    //константы для создания мепа
    public static final String TITLE = "targetname";
    public static final String DATE = "date";
    public static final String DESCRIPTION = "description";

    List<Aim> myAims;

    public MyPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPageFragment newInstance(String param1, String param2) {
        MyPageFragment fragment = new MyPageFragment();
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
        mTargetList.clear();

        try {
            //получаем цели
            myAims = DataStorage.getMe().getAims();
            if (myAims.size() != 0) {
                //создаем лист мепов
                for (int i = 0; i < myAims.size(); i++) {
                    Aim currentAim = myAims.get(i);
                    hm = new HashMap<>();
                    hm.put(TITLE, currentAim.getHeader());
                    hm.put(DATE, currentAim.getEndDate().toString());
                    hm.put(DESCRIPTION, currentAim.getText());
                    mTargetList.add(hm);
                }
            } else {
                TextView empty = (TextView) view.findViewById(R.id.empty);
                empty.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }


        TextView rating = (TextView) view.findViewById(R.id.rating);

        rating.setText("Ваш рейтинг: " + DataStorage.getMe().getRating());

//        hm = new HashMap<>();
//        hm.put(TITLE, "Cinema");
//        hm.put(DATE, "Tomorrow");
//        mTargetList.add(hm);

        //и снова адаптер (ненавижу андроид)
       // SimpleAdapter adapter = new SimpleAdapter(getActivity(), mTargetList, R.layout.aims_item, new String[]{TITLE, DATE}, new int[] {R.id.target_aims, R.id.date_aims});
        AdapterAims adapterAims = new AdapterAims(getActivity(), R.layout.aims_item, mTargetList, myAims);
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
            intent.putExtra(ID, i);
//            intent.putExtra("aim", mTargetList.get(i));
            startActivity(intent);
        }
    };

}
