package info.accolade.fishing_master;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import info.accolade.fishing_master.fragment.AboutFragment;
import info.accolade.fishing_master.fragment.ContactFragment;
import info.accolade.fishing_master.fragment.FeedBackFragment;
import info.accolade.fishing_master.fragment.FishersHomeFragment;
import info.accolade.fishing_master.fragment.HomeFragment;
import info.accolade.fishing_master.fragment.MagazineFragment;
import info.accolade.fishing_master.fragment.MapsFragment;
import info.accolade.fishing_master.fragment.MapsFragment2;
import info.accolade.fishing_master.fragment.NearByFragment;
import info.accolade.fishing_master.fragment.RequestFragment;
import info.accolade.fishing_master.fragment.SearchFragment;

public class FishersHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fishers_home);

        toolbar = findViewById(R.id.toolbarfisher);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayoutfisher);
        navigationView = findViewById(R.id.navigationfisher);

        ActionBarDrawerToggle toggleButton =new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggleButton);
        toggleButton.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new FishersHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.homepagef);
        }
        if (ActivityCompat.checkSelfPermission(FishersHomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FishersHomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FishersHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
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
            case R.id.homepagef:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new FishersHomeFragment()).commit();
                break;

            case R.id.escorts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new NearByFragment()).commit();
                break;

            case R.id.map2:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new MapsFragment()).commit();
                break;

            case R.id.aboutf:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new AboutFragment()).commit();
                break;

            case R.id.magazinef:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new MagazineFragment()).commit();
                break;

            case R.id.contactf:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new ContactFragment()).commit();
                break;

            case R.id.feedbackf:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new FeedBackFragment()).commit();
                break;

            case R.id.requestfish:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentfisher, new RequestFragment()).commit();
                break;

            case R.id.logoutf:
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
        editor.putString("BoatName", "");
        editor.putString("BoatNumber", "");
        editor.putString("UserAddress", "");
        editor.putBoolean("IsLogin", false);
        editor.apply();

        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        FishersHomeActivity.this.finish();
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
                            FishersHomeActivity.this.finish();
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }
}