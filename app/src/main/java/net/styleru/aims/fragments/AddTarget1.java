package net.styleru.aims.fragments;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.styleru.aims.Aim;
import net.styleru.aims.R;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import ru.aimsproject.connectionwithbackend.RequestMethods;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddTarget1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTarget1 extends Fragment {

    DatePickerDialog dateStartDatePicker, dateEndDatePicker;
    Button forDate, forEnd, addTarget;

    Date start, end;

    RadioGroup typeOfTargets;

    EditText header, description, tags;

    int type;

    Aim aim;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "kek";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddTarget1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTarget1.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTarget1 newInstance(String param1, String param2) {
        AddTarget1 fragment = new AddTarget1();
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

        View view = inflater.inflate(R.layout.fragment_new_add_target_1, container, false);;

        forDate = (Button) view.findViewById(R.id.addTarget1_start_date);
        forEnd = (Button) view.findViewById(R.id.addTarget1_end_date);
        addTarget = (Button) view.findViewById(R.id.button_add_target);

        header = (EditText) view.findViewById(R.id.addTarget1_name);
        description = (EditText) view.findViewById(R.id.addTarget1_description);
        tags = (EditText) view.findViewById(R.id.addTarget1_tags);

        typeOfTargets = (RadioGroup) view.findViewById(R.id.target_type);

        addTarget = (Button) view.findViewById(R.id.button_add_target);

        View.OnClickListener mylistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.addTarget1_start_date:
                        dateStartDatePicker.show();
                        break;
                    case R.id.addTarget1_end_date:
                        dateEndDatePicker.show();
                        break;
                    case R.id.button_add_target:
                        aim = new Aim(header.getText().toString(), description.getText().toString(), type, end, start, tags.getText().toString());
                        AsyncAdd asyncAdd = new AsyncAdd();
                        try {
                          asyncAdd.execute(aim).get();
                        } catch (InterruptedException e) {
                            Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        } catch (ExecutionException e) {
                            Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                        break;
                }

            }
        };

        typeOfTargets.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.target0:
                        type = 0;
                        break;
                    case R.id.target1:
                        type = 1;
                        break;
                    case R.id.target2:
                        type = 2;
                    case R.id.target3:
                        type = 3;
                    default:
                        break;
                }
            }
        });


        addTarget.setOnClickListener(mylistener);
        forDate.setOnClickListener(mylistener);
        forEnd.setOnClickListener(mylistener);

        initStartDatePicker();
        initEndDatePicker();
        return view;
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void initStartDatePicker(){

        Calendar newCalendar = Calendar.getInstance();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        dateStartDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newCal=Calendar.getInstance();
                newCal.set(year, month, dayOfMonth);
                forDate.setText(dateFormat.format(newCal.getTime()));
                start = newCal.getTime();
            }
        }, newCalendar.get(Calendar.YEAR),newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void initEndDatePicker(){

        Calendar newCalendar = Calendar.getInstance();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        dateEndDatePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newCal=Calendar.getInstance();
                newCal.set(year, month, dayOfMonth);
                forEnd.setText(dateFormat.format(newCal.getTime()));
                end = newCal.getTime();
            }
        }, newCalendar.get(Calendar.YEAR),newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
    class AsyncAdd extends AsyncTask<Aim, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Aim... params) {
            try {
                RequestMethods.addAimType1(params[0].getHeader(), params[0].getDesription(), params[0].getType(), params[0].getEndDate(), params[0].getStartDate(), params[0].getTags());
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

}
