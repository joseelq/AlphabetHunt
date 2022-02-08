package com.example.AlphabetHunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {
    Button start_button;
    Button history_button;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.swoosh);



        start_button = findViewById(R.id.Start);
        history_button = findViewById(R.id.History);


        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();


                startActivity(new Intent(HomeScreen.this, MainActivity.class));
            }
        });

        history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();

                startActivity(new Intent(HomeScreen.this, History.class));
            }
        });



    }




}