package net.styleru.aims;

import android.graphics.Shader;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ru.aimsproject.connectionwithbackend.RequestMethods;
import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.*;

import static net.styleru.aims.fragments.AimsFragment.ID;

public class AboutTargetActivity extends AppCompatActivity {

    int id;
    ru.aimsproject.models.Aim aim;

    ProgressBar progressBar;
    TextView tvProgressHorizontal;

    List<Comment> commentList;

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

     //   comments.addHeaderView(descriptionCard);
       // comments.addHeaderView(proofsCars);

        commentList = aim.getComments();

        if(commentList.isEmpty()) {
            commentList.add(new Comment("kekes", "leonid", "Leonid", null, null));
            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
            commentList.add(new Comment("kek", "leonid", "Le", null, null));
            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
            commentList.add(new Comment("kek", "leonid", "Le", null, null));
            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
            commentList.add(new Comment("kek", "leonid", "Le", null, null));
            commentList.add(new Comment("kek", "leonid", "Leo", null, null));
            commentList.add(new Comment("kek", "leonid", "Le", null, null));
        }
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvProgressHorizontal.setText("20%");

        comments.setAdapter(new CommentsAdapter(this, R.layout.comment_item, commentList));
    //    comments.setNestedScrollingEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
