package net.styleru.aims;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.styleru.aims.adapters.AdapterAims;
import net.styleru.aims.myviews.NestedScrollingListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.models.Aim;
import ru.aimsproject.models.User;

import static net.styleru.aims.fragments.MyPageFragment.DATE;
import static net.styleru.aims.fragments.MyPageFragment.DESCRIPTION;
import static net.styleru.aims.fragments.MyPageFragment.TITLE;

public class FriendActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> mTargetList = new ArrayList<>();

    List<Aim> friendAims;

    User user;

    String login;

    Button buttonAdd;

    ImageView avatar;
    TextView rating;
    CollapsingToolbarLayout collapsingToolbar;
    ProgressBar progressBar;

    AppBarLayout appBarLayout;
    View container;

    LinearLayout cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        login = getIntent().getExtras().getString("User");

        NestedScrollingListView listView = (NestedScrollingListView) findViewById(R.id.friends_ListView);

        buttonAdd = (Button) findViewById(R.id.add_to_friend);
        avatar = (ImageView) findViewById(R.id.friend_avatar_act);

        rating = (TextView) findViewById(R.id.rating_friend);

        progressBar = (ProgressBar) findViewById(R.id.myfriend_progress);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_friend);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout_friend);
        cont = (LinearLayout) findViewById(R.id.texts);
        cont.setVisibility(View.GONE);
       // container = findViewById(R.id.content_friend);
       // container.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setTitle(login);
        appBarLayout.setExpanded(false, false);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setActivated(true);
        AimsAsync aimsAsync = new AimsAsync();
        aimsAsync.execute(login);
    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == android.R.id.home) {
//            finish();
//        }
//        return true;
//    }

    public void addToFriend(View view) {
        AddFriendAsync addFriendAsync = new AddFriendAsync();
        DeleteAsync deleteAsync = new DeleteAsync();
        AcceptAsync acceptAsync = new AcceptAsync();
        try {
            switch (user.getInFriends()) {
                case 0:
                    if(addFriendAsync.execute(user).get()) {
                        Snackbar.make(view, "Пользователь добавлен в друзья!", Snackbar.LENGTH_LONG);
                        buttonAdd.setText("Вы подписаны");
                        buttonAdd.setBackgroundColor(0);
                        user.setInFriends(-2);
                    }
                    else {
                        Snackbar.make(view, "Произошла ошибка! Пользователь не добавлен в друзья!", Snackbar.LENGTH_LONG);
                    }
                    break;
                case 1:
                    String res = deleteAsync.execute(user).get();
                    if(res.equals("")) {
                        buttonAdd.setText("Подписан на вас");
                        buttonAdd.setBackgroundColor(0);
                        user.setInFriends(-1);
                    }
                    else {
                        Snackbar.make(view, res, Snackbar.LENGTH_LONG);
                    }
                    break;
                case -1:
                    res = acceptAsync.execute(user).get();
                    if(res.equals("")) {
                        buttonAdd.setText("Удалить из друзей");
                        buttonAdd.setBackgroundColor(0);
                        user.setInFriends(1);
                    }
                    else {
                        Snackbar.make(view, res, Snackbar.LENGTH_LONG);
                    }
                    break;
                case -2:
                    res = deleteAsync.execute(user).get();
                    if(res.equals("")) {
                        buttonAdd.setText("Добавить в друзья");
                        buttonAdd.setBackgroundColor(getResources().getColor(R.color.blue_button));
                        user.setInFriends(0);
                    }
                    else  {
                        Snackbar.make(view, res, Snackbar.LENGTH_LONG);
                    }
                    break;
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

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            NestedScrollingListView listView = (NestedScrollingListView) findViewById(R.id.friends_ListView);
            HashMap<String, String> hm;

            if(user == null) {
                Toast.makeText(getApplicationContext(), "Не удалось найти пользователя", Toast.LENGTH_LONG).show();
                finish();
            } else {
                friendAims = user.getAims();

                if (!friendAims.isEmpty()) {
                    for (int i = 0; i < friendAims.size(); i++) {
                        Aim currentAim = friendAims.get(i);
                        hm = new HashMap<>();
                        hm.put(TITLE, currentAim.getHeader());
                        hm.put(DATE, currentAim.getDate().toString());
                        hm.put(DESCRIPTION, currentAim.getText());
                        mTargetList.add(hm);
                    }
                } else {
                    TextView empty = (TextView) findViewById(R.id.empty);
                    empty.setVisibility(View.VISIBLE);
                }

                avatar.setImageBitmap(user.getImage());

                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                setTitle(user.getName());
                appBarLayout.setExpanded(true, true);

                rating.setText("Рейтинг: " + user.getRating());

                switch (user.getInFriends()) {
                    case 1:
                        buttonAdd.setText("Удалить из друзей");
                        break;
                    case -1:
                        buttonAdd.setText("Подписан на вас");
                        break;
                    case -2:
                        buttonAdd.setText("Вы подписаны");
                        break;

                }

//        hm = new HashMap<>();
//        hm.put(TITLE, "Cinema");
//        hm.put(DATE, "Tomorrow");
//        mTargetList.add(hm);
//
//        hm = new HashMap<>();
//        hm.put(TITLE, "HSE");
//        hm.put(DATE, "10.10");
//        mTargetList.add(hm);

                AdapterAims adapterAims = new AdapterAims(getBaseContext(), R.layout.aims_item, mTargetList);
                listView.setAdapter(adapterAims);
                listView.setNestedScrollingEnabled(true);
            }

            progressBar.setVisibility(View.GONE);
            cont.setVisibility(View.VISIBLE);
            //container.setVisibility(View.VISIBLE);
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

    class DeleteAsync extends AsyncTask<User, Void, String> {

        @Override
        protected String doInBackground(User... params) {
            try {
                RequestMethods.deleteFriend(params[0]);
            } catch (Exception e) {
                return e.getMessage();
            }
            return "";
        }
    }

    class AcceptAsync extends AsyncTask<User, Void, String> {

        @Override
        protected String doInBackground(User... params) {
            try {
                RequestMethods.acceptFriendship(params[0]);
            } catch (Exception e) {
                return e.getMessage();
            }
            return "";
        }
    }

}
