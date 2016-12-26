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
import android.widget.TextView;

import net.styleru.aims.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;

    TextView empty;

    View view;

    private OnFragmentInteractionListener mListener;

    public PageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PageFragment newInstance(String param1, String param2) {
        PageFragment fragment = new PageFragment();
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
        // Inflate the layout for this fragment
        getActivity().setTitle("Мои друзья");
        view = inflater.inflate(R.layout.fragment_page, container, false);
        initRecyclerView(view);

        empty = (TextView) view.findViewById(R.id.empty_friends);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void initRecyclerView(View rootView) {
        recyclerView = (RecyclerView)rootView.findViewById(R.id.alerts_list);
        Context context = rootView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        AlertAdapter expensesAdapter = new AlertAdapter(getExpenses());
        recyclerView.setAdapter(expensesAdapter);
    }


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
//        expenses.add(new Friend("Cinema", "10.10"));
//        expenses.add(new Friend("HSE", "Tomorrow"));

        if(!expenses.isEmpty())  {
           // empty.setVisibility(View.GONE);
        }
        return expenses;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
