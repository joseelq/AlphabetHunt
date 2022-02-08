package com.example.AlphabetHunt;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.AnnotateImageRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;


public class MainActivity extends AppCompatActivity {
    GridLayout ll = null;
    // RELATIVE LAYOUT
    RelativeLayout rl = null;
    RelativeLayout[] children = new RelativeLayout[26];

    ImageView block;
    ImageView gleam;

    TextView timer;

    // ALPHABET
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // TextToSpeech Array
    TextToSpeech[] speech_array = new TextToSpeech[26];
    TextToSpeech speak_text;


    // Schedule Executors Arrays
    ScheduledExecutorService[] talk = new ScheduledExecutorService[26];
    ScheduledExecutorService[] shines = new ScheduledExecutorService[26];

    // Boolean to determine if first image was accepted
    Boolean was_accepted;
    // First image lables array
    String[] first_labels = new String[3];
    // Image Lables array
    String[] labels = new String[3];

    //COUNT FOR SCHEDULES ARRAY
    int count = 0;

    // Activity Result Launcher
    ActivityResultLauncher<Intent> activityResultLauncher;

    // RANDOM NUMBERS AND LETTERS
    Random random = new Random();
    int numb = random.nextInt(alphabet.length());
    char let = alphabet.charAt(numb);
    CharSequence letter_sequence = new StringBuilder(1).append(let);

    String tags;

    // TIMER
    Chronometer game_timer;
    Chronometer chronometer;
    // EXIT BUTTON
    Button exit;

    //DATABASE
    photoDatabase database;
    SQLiteDatabase sqLiteDatabase;
    int numba = 0;

    MediaPlayer song;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ll = new GridLayout(this);
        rl = (RelativeLayout) findViewById(R.id.Relative);

        ll.setColumnCount(5);
        ll.setRowCount(6);

        chronometer = new Chronometer(this);
        exit = new Button(this);
        exit.setText("EXIT");


        for (int i = 0; i < 26; i++){
            View v =  getLayoutInflater().inflate(R.layout.activity_main, null);

            RelativeLayout relativeLayout = v.findViewById(R.id.Relative);
            relativeLayout.setId(i);

            children[i] = relativeLayout;

            // TEXT VIEW
            TextView tv = v.findViewById(R.id.Text);
            tv.setText("" + alphabet.charAt(i));
            tv.setId(i);
            tv.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // BLOCK IMAGE VIEW
            ImageView ib = v.findViewById(R.id.Box);
            ib.setImageResource(R.drawable.b1);
            ib.setId(i);

            // SHINE IMAGE VIEW
            ImageView iv = v.findViewById(R.id.Shine);
            iv.setImageResource(R.drawable.shine);
            iv.setId(i);

            ll.addView(v);
        }
        ll.addView(chronometer);
        ll.addView(exit);
        setContentView(ll);


        // DATABASE
        database = new photoDatabase(getApplicationContext());
        sqLiteDatabase = database.getWritableDatabase();




        // Initialize first block to animate
        block = (ImageView) children[numb].getChildAt(0);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                song = MediaPlayer.create(getApplicationContext(), R.raw.sound);
                song.start();
            }
        }).start();



        Thread speak = new Thread(new Runnable() {
            @Override
            public void run() {
                do_Speak();
            }
        });
        speak.start();
        Thread sheen = new Thread(new Runnable() {
            @Override
            public void run() {
                do_Shine();
            }
        });
        sheen.start();




        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    Bitmap picture = bitmap;

                    // Convert Photo for Database
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    byte[] photo = stream.toByteArray();

                    // Get Current Chronometer Time
                    long currentTime = SystemClock.elapsedRealtime()-chronometer.getBase();



                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                myVisionTester(photo);
                                try {
                                    tags = "";
                                    for (int i=0; i < 3; i++){
                                        tags = tags + labels[i] + ", ";
                                    }


                                  //if (checkLabels(labels, 'W')) {
                                   if (checkLabels(labels, let)) {
                                        // Set first block to accepted image
                                        was_accepted = true;
                                        chronometer.stop();

                                        block.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        MediaPlayer cheers = MediaPlayer.create(getApplicationContext(), R.raw.cheer);
                                                        cheers.start();
                                                        String db_labels = TextUtils.join(",", labels);

                                                        String letta = "" + let + "";
                                                        letta = "" + letta +"";

                                                        database.addPhoto(photo, db_labels, letta, "12/01", currentTime );
                                                        block.setImageBitmap(bitmap);
                                                        block.setOnClickListener(null);
                                                        talk[count].shutdown();
                                                        shines[count].shutdown();
                                                        song.start();
                                                        count++;
                                                        numba++;

                                                      //  boolean is_game_finished = check_Board();

                                                       /* if(is_game_finished){
                                                            startActivity(new Intent(MainActivity.this, HomeScreen.class));
                                                        }*/

                                                        if (count == 25){
                                                            startActivity(new Intent(MainActivity.this, HomeScreen.class));
                                                        }

                                                        // CHANGE TO NEXT RANDOM BLOCK
                                                        // MAKE A METHOD THAT HANDLES ALL CHANGES
                                                        change_Block();







                                                    }
                                                });
                                            }
                                        });

                                    } else {
                                        MediaPlayer boo = MediaPlayer.create(getApplicationContext(), R.raw.boo);
                                        boo.start();
                                        Log.v("Not a Match", let + "");
                                    }

                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });


        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        song.pause();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        activityResultLauncher.launch(intent);
                    }
                }).start();

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                talk[count].shutdown();
                shines[count].shutdown();
                song.stop();
                song.release();
                startActivity(new Intent(MainActivity.this, HomeScreen.class));
            }
        });


        }





    void myVisionTester(byte[] photo) throws IOException
    {
        //1. ENCODE image.

        Image myimage = new Image();
        myimage.encodeContent(photo);

        //2. PREPARE AnnotateImageRequest
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
        annotateImageRequest.setImage(myimage);
        Feature f = new Feature();
        f.setType("LABEL_DETECTION");
        f.setMaxResults(3);
        List<Feature> lf = new ArrayList<Feature>();
        lf.add(f);
        annotateImageRequest.setFeatures(lf);

        //3.BUILD the Vision
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(new VisionRequestInitializer("AIzaSyAt_ud0mRIvXcI5FWLZ9Dw4XiTDDF3x_vY"));
        Vision vision = builder.build();

        //4. CALL Vision.Images.Annotate
        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        List<AnnotateImageRequest> list = new ArrayList<AnnotateImageRequest>();
        list.add(annotateImageRequest);
        batchAnnotateImagesRequest.setRequests(list);
        Vision.Images.Annotate task = vision.images().annotate(batchAnnotateImagesRequest);
        BatchAnnotateImagesResponse response = task.execute();
        for (int i=1; i<4; i++){
            String description = response.getResponses().toString().split("description")[i];
            description = description.split(",")[0];
            description = description.split(":")[1];
            labels[i-1] = description;
            Log.v("word", labels[i-1]);
        }
    }


    public void change_Block(){
        // Get new Random Number, Letter, and Letter Sequence
        int new_number = random_Number(numb);
        numb = new_number;
        char new_letter = alphabet.charAt(numb);
        let = new_letter;
        CharSequence new_sequence = new StringBuilder(1).append(let);
        letter_sequence = new_sequence;

        // Change Block and Gleam
        block = (ImageView) children[numb].getChildAt(0);
        gleam = (ImageView) children[numb].getChildAt(1);

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
            }
        });

        // Start Speech and Shine Schedules
        do_Speak();
        do_Shine();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

    }

    public void do_Speak(){
        ScheduledExecutorService speech_scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        speech_scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        speak_text = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int i) {
                                if (i != TextToSpeech.ERROR){
                                    speak_text.setLanguage(Locale.ENGLISH);
                                    speak_text.setPitch(2);
                                    speak_text.speak((CharSequence) letter_sequence, TextToSpeech.QUEUE_FLUSH, null, "Letter " + letter_sequence);
                                    Log.v("Spoken Letter", ""+letter_sequence);

                                }
                            }
                        });
                    }
                });
            }
        }, 1, 5, TimeUnit.SECONDS);
        talk[count] = speech_scheduledExecutorService;
    }

    public void do_Shine(){
        ScheduledExecutorService shine_scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        shine_scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        block = (ImageView) children[numb].getChildAt(0);
                        gleam = (ImageView) children[numb].getChildAt(1);

                        Animation animation = new TranslateAnimation(
                                0, block.getWidth()+ gleam.getWidth(), 0, 0
                        );

                        animation.setDuration(550);
                        animation.setFillAfter(false);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        gleam.startAnimation(animation);
                    }
                });
            }
        }, 1, 5, TimeUnit.SECONDS);
        shines[count] = shine_scheduledExecutorService;

    }





    public boolean checkLabels(String[] labels, char letter){
        for (int i=0; i< labels.length; i++){
            if (labels[i].charAt(1) == letter){
                return true;
            }
        }
        return false;
    }

    public int random_Number(int old_number){
        Random random = new Random();
        int new_number = random.nextInt(alphabet.length());
        if (new_number == old_number){
            random_Number(old_number);
        } return new_number;
    }



    /*public boolean check_Board() {
        for (int i = 0; i < 26; i++) {
            ImageView letter_image = (ImageView) children[i].getChildAt(0);
            Drawable box_box = this.getResources().getDrawable(R.drawable.b1);
            if (letter_image.getBackground().getConstantState().equals(box_box.getConstantState())) {
                return false;
            } else {
               continue;
            }
        }
        return true;
    }*/

}