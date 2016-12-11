package net.styleru.aims;

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

public class RegisteredActivity extends AppCompatActivity {

    String sex;
    String login;
    String password;
    String name;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            RequestMethods.register(login, password, email, name, sex);
        } catch (NullPointerException e) {
            Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
        } catch (Exception ex) {
            Snackbar.make(view, ex.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
}

