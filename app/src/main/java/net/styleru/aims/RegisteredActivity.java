package net.styleru.aims;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;

public class RegisteredActivity extends AppCompatActivity {

    String sex;
    String login;
    String password;
    String name;
    String email;

    public static final String APP_REFERENCES = "settingsForToken";

    public static final String APP_REFERENCE_Token = "token";

    SharedPreferences mToken;
    SharedPreferences.Editor edit;

    View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToken = getSharedPreferences(APP_REFERENCES, Context.MODE_PRIVATE);
        edit = mToken.edit();
        if(mToken.contains(APP_REFERENCE_Token)) {
            edit.remove(APP_REFERENCE_Token);
            edit.clear();
        }

        edit.apply();

        setContentView(R.layout.activity_registered);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContentView = findViewById(R.id.register_form);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RadioButton male = (RadioButton) findViewById(R.id.male);
        male.setOnClickListener(radioButtonListener);
        RadioButton female = (RadioButton) findViewById(R.id.female);
        female.setOnClickListener(radioButtonListener);

    }

    View.OnClickListener radioButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton) v;
            switch (rb.getId()) {
                case R.id.male:
                    sex = "1";
                    break;
                case R.id.female:
                    sex = "0";
                    break;
                default:
                    break;
            }
        }


    };


    public void registerToTheBase(View view) {
        try {
            login = ((EditText) findViewById(R.id.login_register)).getText().toString();
            password = ((EditText) findViewById(R.id.password_register)).getText().toString();
            name = ((EditText) findViewById(R.id.name_register)).getText().toString();
            email = ((EditText) findViewById(R.id.email_register)).getText().toString();

            MyTask myTask = new MyTask(login, password, email, name, sex);
            if(myTask.execute().get()) {
                edit = mToken.edit();
                edit.putString(APP_REFERENCE_Token, DataStorage.getToken());
                edit.apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                Snackbar.make(mContentView, "Вы зарегистрированы", Snackbar.LENGTH_LONG);
            } else {
                Snackbar.make(mContentView, "Что-то пошло не так", Snackbar.LENGTH_LONG);
            }
        } catch (NullPointerException e) {
            Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
        } catch (Exception ex) {
            Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    class MyTask extends AsyncTask<Void, Void, Boolean> {

        String sex;
        String login;
        String password;
        String name;
        String email;

      public MyTask(String l, String p, String e, String n, String s) {
          login = l;
          password = p;
          email = e;
          name = n;
          sex = s;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                RequestMethods.register(login, password, email, name, sex);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }
}

