package net.styleru.aims;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.styleru.aims.fragments.AimsFragment;
import net.styleru.aims.fragments.FriendsFragment;
import net.styleru.aims.fragments.PageFragment;
import net.styleru.aims.fragments.SettingsFragment;

import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        //PageFragment pageFr;
        AimsFragment aimsFr;
        FriendsFragment friendFr;
        SettingsFragment settingFr;

        // 0 - aimsFr, 1 - friendFr, 2 - settingFr
        byte openedFragment;

        int scrollFlags;

        private FragmentManager fragmentManager;
        private Stack<Fragment> fragmentStack;

        CollapsingToolbarLayout collapsingToolbar;
        Toolbar toolbar;
        Toolbar toolbarFriend;

        AppBarLayout appBarLayoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBarLayoutMain = (AppBarLayout) findViewById(R.id.layout_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Антон Ригин");

        //fragmentStack = new Stack<Fragment>();

        //pageFr = PageFragment.newInstance("kek", "lol");
        aimsFr = AimsFragment.newInstance("kek", "lol");
        friendFr = FriendsFragment.newInstance("kek", "lol");
        settingFr = SettingsFragment.newInstance("kek", "lol");

        fragmentManager = getFragmentManager();
        FragmentTransaction ftransp = fragmentManager.beginTransaction();
        ftransp.replace(R.id.container, aimsFr);
//        ftransp.add(R.id.container, aimsFr);
//        fragmentStack.push(aimsFr);
        ftransp.commit();

        AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();

        scrollFlags = p.getScrollFlags();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            if(fragmentStack.size() == 2) {
//                FragmentTransaction ft = fragmentManager.beginTransaction();
//                fragmentStack.lastElement().onPause();
//                ft.remove(fragmentStack.pop());
//                fragmentStack.lastElement().onResume();
//                ft.show(fragmentStack.lastElement());
//                ft.commit();
//            }
//            else {
                super.onBackPressed();
          //  }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction ftransp = fragmentManager.beginTransaction();

       /* if (id == R.id.my_page) {

            ftransp.replace(R.id.container, pageFr);

        } else */if (id == R.id.target_control) {
            appBarLayoutMain.setExpanded(true, false);
//            AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//            p.setScrollFlags(scrollFlags);
//            collapsingToolbar.setLayoutParams(p);
            ftransp.replace(R.id.container, aimsFr);
//            ftransp.add(R.id.container, aimsFr);
//            fragmentStack.lastElement().onPause();
//            ftransp.hide(fragmentStack.lastElement());
//            fragmentStack.push(aimsFr);
            collapsingToolbar.setTitle("Антон Ригин");

        } else if (id == R.id.friends) {
            appBarLayoutMain.setExpanded(false, false);
//            AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//            p.setScrollFlags(-1);
//            collapsingToolbar.setLayoutParams(p);
            ftransp.replace(R.id.container, friendFr);
            appBarLayoutMain.setExpanded(false, false);

//            ftransp.add(R.id.container, friendFr);
//            fragmentStack.lastElement().onPause();
//            ftransp.hide(fragmentStack.lastElement());
//            fragmentStack.push(friendFr);
            collapsingToolbar.setTitle("Лента");

        } else if (id == R.id.nav_manage) {
            appBarLayoutMain.setExpanded(false, false);
//            AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//            p.setScrollFlags(-1);
//            collapsingToolbar.setLayoutParams(p);
            ftransp.replace(R.id.container, settingFr);
            appBarLayoutMain.setExpanded(false, false);
//            ftransp.add(R.id.container, settingFr);
//            fragmentStack.lastElement().onPause();
//            ftransp.hide(fragmentStack.lastElement());
//            fragmentStack.push(settingFr);
            collapsingToolbar.setTitle("Настройки");

        } /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        ftransp.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addTarget(View view) {
        Intent intent = new Intent(this, AddTarget.class);
        startActivity(intent);
    }
}
