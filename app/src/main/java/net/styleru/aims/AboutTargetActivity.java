package net.styleru.aims;

import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.styleru.aims.R;
import net.styleru.aims.fragments.NestedScrollingListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.*;

import static net.styleru.aims.fragments.AimsFragment.DATE;
import static net.styleru.aims.fragments.AimsFragment.ID;

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


        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("EEE MMM dd HH:mm:ss z yyyy");
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
        AsyncLike like = new AsyncLike();

        try {
            String res = like.execute(aim).get();
            if(res.equals("")) {
                likes.setText(aim.getLikes());
            }
            else {
                Snackbar.make(view, res, Snackbar.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void disliked(View view) {
        AsyncDisLike disLike = new AsyncDisLike();
        try {
            String res = disLike.execute(aim).get();
            if(res.equals("")) {
                dislikes.setText(aim.getDislikes());
            }
            else {
                Snackbar.make(view, res, Snackbar.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
    }
}
