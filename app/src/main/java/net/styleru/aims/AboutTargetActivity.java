package net.styleru.aims;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.styleru.aims.adapters.CommentsAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.*;

import static net.styleru.aims.fragments.MyPageFragment.ID;

public class AboutTargetActivity extends AppCompatActivity {

    int id;
    ru.aimsproject.models.Aim aim;

    ProgressBar progressBar;
    TextView tvProgressHorizontal;
    TextView targetDescription;

    List<Comment> commentList;

    TextView textProgress;

    TextView date, likes, dislikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_target);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AsyncInfo info = new AsyncInfo();

        progressBar = (ProgressBar) findViewById(R.id.target_progressBar);
        tvProgressHorizontal = (TextView) findViewById(R.id.progress_horizontal);

//        aim = (Aim) getIntent().getExtras().get("aim");
        id = getIntent().getExtras().getInt(ID);
        int type = getIntent().getExtras().getInt("type");

        if(type == 1) {
            aim = DataStorage.getNewsFeed().get(id);
        }
        else {
            aim = DataStorage.getMe().getAims().get(id);
        }

        try {
            Aim res2 = info.execute(aim).get();
            if(res2 != null) {
                aim = res2;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        setTitle(aim.getHeader());


        ListView comments = (ListView) findViewById(R.id.comments);

        CardView descriptionCard = (CardView) findViewById(R.id.card_target);
        CardView proofsCars = (CardView) findViewById(R.id.target_proofs);

        textProgress = (TextView) findViewById(R.id.progress_description);

        targetDescription = (TextView) findViewById(R.id.target_description);

        date = (TextView) findViewById(R.id.date_about);
        likes = (TextView) findViewById(R.id.like_text);
        dislikes = (TextView) findViewById(R.id.dislike_text);


     //   comments.addHeaderView(descriptionCard);
       // comments.addHeaderView(proofsCars);

        commentList = aim.getComments();

//        if(commentList.isEmpty()) {
//            commentList.add(new Comment("kekes", "leonid", "Leonid", null, null));
//            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
//            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
//            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
//            commentList.add(new Comment("kek", "leonid", "Le", null, null));
//            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
//            commentList.add(new Comment("kek", "leonid", "Le", null, null));
//            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
//            commentList.add(new Comment("kek", "leonid", "Le", null, null));
//            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
//            commentList.add(new Comment("kek", "leonid", "Le", null, null));
//        }

        comments.setAdapter(new CommentsAdapter(this, R.layout.comment_item, commentList));
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        int progress = (int)(aim.getEndDate().getTime() - (new Date()).getTime());
        int allTime = (int)(aim.getEndDate().getTime() - aim.getStartDate().getTime());
        progressBar.setProgress(progress/allTime);

        tvProgressHorizontal.setText(progress/allTime + "%");
        textProgress.setText(progress/100000000 + "/" + allTime/100000000);

        targetDescription.setText(aim.getText());


        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Date aimDate = null;
        try {
            aimDate = format.parse(aim.getEndDate().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat printDate = new SimpleDateFormat("dd.MM.yyyy");
        date.setText("Выполнить до: " + printDate.format(aimDate));

        likes.setText("" + aim.getLikes());
        dislikes.setText("" + aim.getDislikes());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void liked(View view) {
        int l = (Integer.parseInt(likes.getText().toString()) + 1);
        likes.setText((Integer.parseInt(likes.getText().toString()) + 1) + "");

        AsyncLike like = new AsyncLike();
            like.execute(aim);
    }

    public void disliked(View view) {

        dislikes.setText((Integer.parseInt(dislikes.getText().toString()) + 1) + "");
        AsyncDisLike disLike = new AsyncDisLike();
            disLike.execute(aim);
    }

    class AsyncLike extends AsyncTask<Aim, Void, String> {

        @Override
        protected String doInBackground(Aim... params) {
            try {
                RequestMethods.likeAim(params[0]);
            } catch (Exception e) {
                return e.getMessage();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("")) {
                likes.setText("" + aim.getLikes());
            }
            else {
                likes.setText("" + aim.getLikes());
                Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
            }
        }
    }

    class AsyncDisLike extends AsyncTask<Aim, Void, String> {

        @Override
        protected String doInBackground(Aim... params) {
            try {
                RequestMethods.dislikeAim(params[0]);
            } catch (Exception e) {
                return e.getMessage();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("")) {
                dislikes.setText("" + aim.getDislikes());
            }
            else {
                dislikes.setText("" + aim.getDislikes());
                Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
            }
        }
    }

    class AsyncInfo extends AsyncTask<Aim, Void, Aim> {

        @Override
        protected Aim doInBackground(Aim... params) {
            try {
                return RequestMethods.getAimInfo(params[0]);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
