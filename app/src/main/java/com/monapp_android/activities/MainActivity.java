package com.monapp_android.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.github.nkzawa.socketio.client.Socket;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.monapp_android.Adapters.MessageAdapter;
import com.monapp_android.DAOs.MessageDAO;
import com.monapp_android.DTOs.ChildDTO;
import com.monapp_android.DTOs.MessageDTO;
import com.monapp_android.GPS.GPSTracker;
import com.monapp_android.R;
import com.monapp_android.application.MONAPP;
import com.monapp_android.services.BackgroundService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FusedLocationProviderClient client;
    private Socket mSocket;
    private TextView name;
    private ImageView image;
    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ChildDTO childDTO = MONAPP.getChild();
        final MessageDAO messageDAO = new MessageDAO();
        final File messagesFile = new File(getFilesDir().getAbsolutePath() + File.pathSeparator + childDTO.getToken() + "_messages");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//
//        View header = navigationView.getHeaderView(0);
//        name = (TextView) header.findViewById(R.id.name);
////        name.setTypeface(null, Typeface.BOLD);
//        name.setTextColor(Color.WHITE);
//        name.setText(childDTO.getFirstName() + " " + childDTO.getLastName());

//        image = (ImageView)header.findViewById(R.id.image);
//        byte[] decodedString = Base64.decode(childDTO.getImage(),Base64.NO_WRAP);
//        InputStream input = new ByteArrayInputStream(decodedString);
//        Bitmap bitmap = BitmapFactory.decodeStream(input);
//        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
//        circularBitmapDrawable.setCircular(true);
//        image.setImageBitmap(bitmap);
//        image.setImageDrawable(circularBitmapDrawable);

        saveCurrentState();
        startService();

        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final MessageAdapter messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);

        updateListViewTask(messageAdapter);
        messageDAO.readFile(messagesFile);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                messageDAO.removeMessage(messagesFile, messageAdapter.getMessageAt(viewHolder.getAdapterPosition()));
                messageAdapter.setMessages(getMessagesList());
                Toast.makeText(MainActivity.this, "The message was deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File f = new File(getFilesDir().getAbsolutePath() + File.pathSeparator + childDTO.getToken() + "_messages");
                f.delete();
                messageAdapter.setMessages(getMessagesList());
                Toast.makeText(MainActivity.this, "All messages have been deleted!", Toast.LENGTH_SHORT).show();
            }
        });

//        reqPerm();

    }

    private void reqPerm(){
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//        new AlertDialog.Builder(MONAPP.getAppContext())
//                .setTitle("USAGE_STATS Perminssion")
//                .setMessage("Allow USAGE_STATS Permission in settings!")
//                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//                    }
//                })
//                .show();
    }

    private void updateListViewTask(final MessageAdapter messageAdapter) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.setMessages(getMessagesList());
                    }
                });
            }
        }, 0, 1000 * 10);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }


    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout) {
            logout();
        } else if (id == R.id.nav_share) {
            startService();

        } else if (id == R.id.nav_send) {
            stopService();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void saveCurrentState() {
        ChildDTO childDTO = MONAPP.getChild();
        String filename = "currentState";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(childDTO.toJson().getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadState() {
        BufferedReader bufferedReader = null;
        System.out.println("Read");
        try {
            bufferedReader = new BufferedReader(new FileReader(new
                    File(getFilesDir() + File.separator + "currentState")));

            String read;
            while ((read = bufferedReader.readLine()) != null) {
                ChildDTO childDTO = new ChildDTO(read);
                MONAPP.setChild(childDTO);
                break;
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        File file = new File(getFilesDir() + File.separator + "currentState");
        if (file.exists()) {
            deleteFile("currentState");
        }

        stopService();

        Intent intent = new Intent(getApplicationContext(), TokenRegister.class);
        startActivity(intent);
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        stopService(serviceIntent);

    }

    private List<MessageDTO> getMessagesList() {
        List<MessageDTO> messageDTOs = new ArrayList<>();
        File messages = new File(getFilesDir().getAbsolutePath() + File.pathSeparator + MONAPP.getChild().getToken() + "_messages");
        final Scanner reader;
        try {
            reader = new Scanner(new FileInputStream(messages), "UTF-8");
            while (reader.hasNextLine()) {
                messageDTOs.add(new MessageDTO(reader.nextLine()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return messageDTOs;
    }
}
