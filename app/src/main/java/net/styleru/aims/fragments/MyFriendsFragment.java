package net.styleru.aims.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.styleru.aims.adapters.AdapterFriends;
import net.styleru.aims.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.User;

/**
 * Use the {@link MyFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;

   // TextView empty;

    View view;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFriendsFragment newInstance(String param1, String param2) {
        MyFriendsFragment fragment = new MyFriendsFragment();
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
        view = inflater.inflate(R.layout.fragment_page, container, false);
        initRecyclerView(view); //иницилизируем адаптер

        //Эта штука нужна была для показа текстового поля, если у пользователя нет друзей,
        //но что-то как-то не зашло
       // empty = (TextView) view.findViewById(R.id.empty_friends);
        return view;
    }

    private void initRecyclerView(View rootView) {
        recyclerView = (RecyclerView)rootView.findViewById(R.id.alerts_list);
        Context context = rootView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        AdapterFriends expensesAdapter = new AdapterFriends(getExpenses());
        recyclerView.setAdapter(expensesAdapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    /**
     * хз почему expanses, по-моему так было в каком-то гайде
     *
     * @return возвращает список друзей
     */
    private List<User> getExpenses() {
        List<User> expenses = new ArrayList<>();
        GetFriendsAsync friendsAsync = new GetFriendsAsync();
        try {
            String res = friendsAsync.execute(DataStorage.getMe()).get();
            if(res.equals("")) {
                expenses = DataStorage.getMe().getFriends();
            }
            else {
                Snackbar.make(view, res, Snackbar.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(!expenses.isEmpty())  {
           // empty.setVisibility(View.GONE);
        }
        return expenses;
    }

    class GetFriendsAsync extends AsyncTask<User, Void, String> {

        @Override
        protected String doInBackground(User... params) {
            try {
                RequestMethods.getFriends(params[0]);
            } catch (Exception e) {
                return e.getMessage();
            }
            return "";
        }
    }
}
