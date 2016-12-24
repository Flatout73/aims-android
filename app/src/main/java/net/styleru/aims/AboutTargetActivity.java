package net.styleru.aims;

import android.graphics.Shader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.styleru.aims.R;

import java.util.Date;
import java.util.HashMap;

import ru.aimsproject.data.DataStorage;
import ru.aimsproject.models.*;

import static net.styleru.aims.fragments.AimsFragment.ID;

public class AboutTargetActivity extends AppCompatActivity {

    int id;
    ru.aimsproject.models.Aim aim;

    ProgressBar progressBar;
    TextView tvProgressHorizontal;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvProgressHorizontal.setText("20%");
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
