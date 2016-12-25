package net.styleru.aims;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.styleru.aims.fragments.AdapterAims;
import net.styleru.aims.fragments.NestedScrollingListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.Aim;
import ru.aimsproject.models.User;

import static net.styleru.aims.fragments.AimsFragment.DATE;
import static net.styleru.aims.fragments.AimsFragment.DESCRIPTION;
import static net.styleru.aims.fragments.AimsFragment.TITLE;

public class FriendActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> mTargetList = new ArrayList<>();

    List<Aim> friendAims;

    User user;

    String login;

    Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login = getIntent().getExtras().getString("User");

        NestedScrollingListView listView = (NestedScrollingListView) findViewById(R.id.friends_ListView);

        AimsAsync aimsAsync = new AimsAsync();
        try {
            user = aimsAsync.execute(login).get();
            if(user == null) {
                Toast.makeText(getApplicationContext(), "Не удалось найти пользователя", Toast.LENGTH_LONG).show();
                finish();
            }
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        buttonAdd = (Button) findViewById(R.id.add_to_friend);

    }

    @Override
    protected void onStart() {
        NestedScrollingListView listView = (NestedScrollingListView) findViewById(R.id.friends_ListView);
        HashMap<String, String> hm;
        friendAims = user.getAims();

        if(!friendAims.isEmpty()) {
            for(int i = 0; i < friendAims.size(); i++) {
                Aim currentAim = friendAims.get(i);
                hm = new HashMap<>();
                hm.put(TITLE, currentAim.getHeader());
                hm.put(DATE, currentAim.getDate().toString());
                hm.put(DESCRIPTION, currentAim.getText());
                mTargetList.add(hm);
            }
        }
        else {
            TextView empty = (TextView) findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);
        }

        hm = new HashMap<>();
        hm.put(TITLE, "Cinema");
        hm.put(DATE, "Tomorrow");
        mTargetList.add(hm);

        hm = new HashMap<>();
        hm.put(TITLE, "HSE");
        hm.put(DATE, "10.10");
        mTargetList.add(hm);

        AdapterAims adapterAims = new AdapterAims(this, R.layout.aims_item, mTargetList);
        listView.setAdapter(adapterAims);
        listView.setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void addToFriend(View view) {
        AddFriendAsync addFriendAsync = new AddFriendAsync();
        try {
            if(addFriendAsync.execute(user).get()) {
                Snackbar.make(view, "Пользователь добавлен в друзья!", Snackbar.LENGTH_LONG);
                buttonAdd.setText("Вы подписаны");
            }
            else {
                Snackbar.make(view, "Произошла ошибка! Пользователь не добавлен в друзья!", Snackbar.LENGTH_LONG);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    class AimsAsync extends AsyncTask <String, Void, User> {

        @Override
        protected User doInBackground(String... params) {
            try {
                return RequestMethods.getUserProfile(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class AddFriendAsync extends AsyncTask<User, Void, Boolean> {

        @Override
        protected Boolean doInBackground(User... params) {
            try {
                RequestMethods.addFriend(params[0]);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

}
