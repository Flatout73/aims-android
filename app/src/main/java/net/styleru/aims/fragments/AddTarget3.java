package net.styleru.aims.fragments;


import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.RadioGroup;

import net.styleru.aims.MainActivity;
import net.styleru.aims.MyAim;
import net.styleru.aims.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.aimsproject.connectionwithbackend.RequestMethods;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTarget3 extends Fragment {


    DatePickerDialog dateStartDatePicker, dateEndDatePicker;
    Button forDate, forEnd, addTarget;

    Date start, end;

    RadioGroup typeOfTargets;

    EditText header, description, tags, days, hours, minuts;

    int type;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AddTarget3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTarget2.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTarget3 newInstance(String param1, String param2) {
        AddTarget3 fragment = new AddTarget3();
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_targets3, container, false);;

        forDate = (Button) view.findViewById(R.id.addTarget1_start_date);
        forEnd = (Button) view.findViewById(R.id.addTarget1_end_date);
        addTarget = (Button) view.findViewById(R.id.button_add_target);

        header = (EditText) view.findViewById(R.id.addTarget1_name);
        description = (EditText) view.findViewById(R.id.addTarget1_description);
        tags = (EditText) view.findViewById(R.id.addTarget1_tags);

        days = (EditText) view.findViewById(R.id.day_timer);
        hours = (EditText) view.findViewById(R.id.hour_timer);
        minuts = (EditText) view.findViewById(R.id.minuts_timer);

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
                        try {
                            MyAim aim = new MyAim(header.getText().toString(), description.getText().toString(), type, end, start, tags.getText().toString());
                            int sumMin = Integer.parseInt(days.getText().toString()) * 24 + Integer.parseInt(hours.getText().toString()) * 60 + Integer.parseInt(minuts.getText().toString());
                            aim.setSelectionDate(sumMin);
                            AsyncAdd asyncAdd = new AsyncAdd();
                            if(asyncAdd.execute(aim).get()) {
                                Snackbar.make(v, "Цель успешно добавлена", Snackbar.LENGTH_LONG);
                                Intent inten = new Intent(getActivity(), MainActivity.class);
                                inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(inten);
                            }
                            else {
                                Snackbar.make(v, "Ошибка добавления цели", Snackbar.LENGTH_LONG);
                            }

                        } catch (NumberFormatException ex) {
                            Snackbar.make(v, "Введите числа в поля интервала", Snackbar.LENGTH_LONG).show();
                        }
                        catch (Exception e) {
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
                newCal.set(year, month, dayOfMonth, 23, 59, 59);
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
                newCal.set(year, month, dayOfMonth, 23, 59, 59);
                forEnd.setText(dateFormat.format(newCal.getTime()));
                end = newCal.getTime();
            }
        }, newCalendar.get(Calendar.YEAR),newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    class AsyncAdd extends AsyncTask<MyAim, Void, Boolean> {

        @Override
        protected Boolean doInBackground(MyAim... params) {
            MyAim aim = params[0];
            try {
                RequestMethods.addAimType2(aim.getHeader(), aim.getDesription(), aim.getModif(), aim.getEndDate(), aim.getStartDate(), aim.getSelectionDate(), aim.getTags());
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
}