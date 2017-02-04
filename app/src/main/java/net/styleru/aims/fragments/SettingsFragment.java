package net.styleru.aims.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.styleru.aims.R;
import net.styleru.aims.myviews.RoundedImageView;

import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 *
 * Ну тут все просто. И никаких адаптеров! Наконец-то!
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    User me;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

        me = DataStorage.getMe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        RoundedImageView avatarSettings = (RoundedImageView) view.findViewById(R.id.settings_avatar);
        EditText name = (EditText) view.findViewById(R.id.set_name);
        EditText surname = (EditText) view.findViewById(R.id.set_surname);

        if(me.getImage() != null) {
            avatarSettings.setImageBitmap(me.getImage());
        }

        String[] names = me.getName().split(" ");
        if(names[0] != null)
            name.setText(names[0]);
        if(names[1] != null)
            surname.setText(names[1]);
        return view;
    }

}
