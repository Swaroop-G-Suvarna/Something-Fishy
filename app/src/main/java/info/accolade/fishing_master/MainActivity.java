package info.accolade.fishing_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import info.accolade.fishing_master.fragment.AboutFragment;
import info.accolade.fishing_master.fragment.ContactFragment;
import info.accolade.fishing_master.fragment.FeedBackFragment;
import info.accolade.fishing_master.fragment.FishersHomeFragment;
import info.accolade.fishing_master.fragment.HomeFragment;
import info.accolade.fishing_master.fragment.LoginFragment;
import info.accolade.fishing_master.fragment.MagazineFragment;
import info.accolade.fishing_master.fragment.MapsFragment;
import info.accolade.fishing_master.fragment.NearByFragment;
import info.accolade.fishing_master.fragment.SearchFragment;
import info.accolade.fishing_master.fragment.ViewRequestFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation);

        ActionBarDrawerToggle toggleButton =new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggleButton);
        toggleButton.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.homepage);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_manu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.share:
                Intent i =  new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,"This is Fishing Master project");
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.homepage:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();
                break;

            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new SearchFragment()).commit();
                break;
            case R.id.requestm:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ViewRequestFragment()).commit();
                break;

            case R.id.about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new AboutFragment()).commit();
                break;

            case R.id.magazine:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MagazineFragment()).commit();
                break;

            case R.id.contact:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ContactFragment()).commit();
                break;

            case R.id.feedback:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new FeedBackFragment()).commit();
                break;

            case R.id.logout:
                    logout();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserId", "");
        editor.putString("UserName", "");
        editor.putString("UserType", "");
        editor.putBoolean("IsLogin", false);
        editor.apply();

        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        MainActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setIcon(R.drawable.boat)
                    .setTitle("Alert..!")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }
}