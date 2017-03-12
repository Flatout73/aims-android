package net.styleru.aims.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.styleru.aims.AboutTargetActivity;
import net.styleru.aims.adapters.AdapterAims;
import net.styleru.aims.R;
import net.styleru.aims.myviews.NestedScrollingListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.Aim;

import static net.styleru.aims.fragments.MyPageFragment.DESCRIPTION;
import static net.styleru.aims.fragments.MyPageFragment.ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private NestedScrollingListView mListView;
    private ArrayList<HashMap<String, String>> mTargetList = new ArrayList<>();
    private static final String TITLE = "targetname";
    private static final String DATE = "date";

    TextView empty;
    ProgressBar progressBar;

    View view;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFeedFragment newInstance(String param1, String param2) {
        NewsFeedFragment fragment = new NewsFeedFragment();
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
        view = inflater.inflate(R.layout.fragment_news, container, false);
        mListView = (NestedScrollingListView) view.findViewById(R.id.friendsListView);
        empty = (TextView) view.findViewById(R.id.empty);

        AsyncNews asyncNews = new AsyncNews();
//        try {
//            String res2 = asyncNews.execute(new Date(115, 12, 20)).get();
        asyncNews.execute(new Date(115, 12, 20));
//            if(!res2.equals("")){
//                Snackbar.make(view, res2, Snackbar.LENGTH_LONG).show();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        //получаем новости
        HashMap<String, String> hm;
        List<Aim> friendAims = DataStorage.getNewsFeed();

        mTargetList.clear();

        // и опять создаем лист мепов
        if (friendAims.size() != 0) {
            try {
                for (int i = 0; i < friendAims.size(); i++) {
                    progressBar.setVisibility(View.GONE);
                    Aim currentAim = friendAims.get(i);
                    hm = new HashMap<>();
                    hm.put(TITLE, currentAim.getHeader());
                    hm.put(DATE, currentAim.getDate().toString());
                    hm.put(DESCRIPTION, currentAim.getText());
                    mTargetList.add(hm);
                }
            } catch (Exception ex) {
                Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_LONG);
            }
        } else {
            //empty.setVisibility(View.VISIBLE);
        }

//        hm = new HashMap<>();
//        hm.put(TITLE, "Header");
//        hm.put(DATE, "12.12.16");
//        hm.put(DESCRIPTION, "kek");
//        mTargetList.add(hm);

        //и опять адаптер
        AdapterAims adapterAims = new AdapterAims(getActivity(), R.layout.aims_item_feed, mTargetList, friendAims);
        mListView.setAdapter(adapterAims);
        mListView.setOnItemClickListener(itemClickListener);
        mListView.setNestedScrollingEnabled(false);

        return view;
    }

    //Кликлистенер на каждый элемент списка. Если бы кто-то реализовал парсабл, он бы работал нормально,
    //а так все через жопу, впрочем ничего нового
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(), AboutTargetActivity.class);
//            intent.putExtra("aim", mTargetList.get(i));
            intent.putExtra(ID, i);
            intent.putExtra("type", 1);
            startActivity(intent);
        }
    };

    class AsyncNews extends AsyncTask<Date, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) view.findViewById(R.id.news_progress);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setActivated(true);
            empty.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Date... params) {
            DataStorage.lock.lock();
            try {
                    RequestMethods.getNews(params[0]);
            } catch (Exception e) {
                return e.getMessage();
            } finally {
                DataStorage.lock.unlock();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
            List<Aim> friendAims = DataStorage.getNewsFeed();
            HashMap<String, String> hm;

                mTargetList.clear();
            if (friendAims.size() != 0) {
                    for (int i = 0; i < friendAims.size(); i++) {
                        Aim currentAim = friendAims.get(i);
                        hm = new HashMap<>();
                        hm.put(TITLE, currentAim.getHeader());
                        hm.put(DATE, currentAim.getDate().toString());
                        hm.put(DESCRIPTION, currentAim.getText());
                        mTargetList.add(hm);
                    }
            } else {
                empty.setVisibility(View.VISIBLE);
            }
                AdapterAims adapterAims = new AdapterAims(getActivity(), R.layout.aims_item_feed, mTargetList, friendAims);
                mListView.setAdapter(adapterAims);
                mListView.setOnItemClickListener(itemClickListener);
                mListView.setNestedScrollingEnabled(false);

                progressBar.setVisibility(View.GONE);
 //               ((AdapterAims) mListView.getAdapter()).notifyDataSetChanged();
            } catch (Exception ex) {
               // Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}