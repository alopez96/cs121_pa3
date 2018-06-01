package com.example.dustinadams.assignment3test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddMemo extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    static final public String MYPREFS = "myprefs";
    static final public String PREF_URL = "restore_url";
    static final public String WEBPAGE_NOTHING = "about:blank";
    static final public String MY_WEBPAGE = "https://users.soe.ucsc.edu/~dustinadams/CMPS121/assignment3/www/index.html";
    static final public String LOG_TAG = "webview_example";

    WebView myWebView;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer ;
    String AudioSavePath = null;
    int position, fileNum;
    public JSONObject jos = null;
    public JSONArray ja = null;
    public static final int RequestPermissionCode = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);

        myWebView = (WebView) findViewById(R.id.webView1);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Binds the Javascript interface
        myWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        myWebView.loadUrl(MY_WEBPAGE);

    }

    public class JavaScriptInterface {
        Context mContext; // Having the context is useful for lots of things,
        // like accessing preferences.

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void record() {
            Log.i(LOG_TAG, "I am in the record javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Toast.makeText(AddMemo.this, "Recording",
                            Toast.LENGTH_LONG).show();
                    if(checkPermission()) {
                        //open the ser file
                        //read the content into a string object and parse to int (fileNum)instance variable
                        //try catch to make sure ser exists
                        //if ser dne you know first file saved will be 1.3gp
                        //else add 1 to fileNum
                        //and create a string for the path(global or instance)
                        try{
                            // Reading a file that already exists
                            File f = new File(getFilesDir(), "file.ser");
                            FileInputStream fi = new FileInputStream(f);
                            ObjectInputStream o = new ObjectInputStream(fi);
                            String j = null;
                            try{
                                j = (String) o.readObject();
                            }
                            catch(ClassNotFoundException c){
                                c.printStackTrace();
                            }
                            position = Integer.valueOf(j);
                            AudioSavePath =
                                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                            (position+1) + ".3gp";
                        }
                        //if ser dne you know first file saved will be 1.3gp
                        catch(IOException e){
                            position = 1;
                            AudioSavePath =
                                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                            position + ".3gp";

                        }
                        MediaRecorderReady();
                        try {
                            // recording starts
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Toast.makeText(AddMemo.this, "Recording started",
                                Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }
                }
            });

        }
        @JavascriptInterface
        public void stop() {
            Log.i(LOG_TAG, "I am in the stop javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Toast.makeText(AddMemo.this, "Stopping",
                            Toast.LENGTH_LONG).show();
                    Toast.makeText(AddMemo.this, "Saved voice memo " +(position + 1),
                            Toast.LENGTH_LONG).show();
                    if(mediaRecorder != null) {
                        mediaRecorder.stop();
                    }
                    else{
                        Toast.makeText(AddMemo.this,"null pointer at media", Toast.LENGTH_SHORT).show();
                    }
                    //convert fileNum to string, and serialize (write it out to the output buffer and close file)
                    String a = String.valueOf(position + 1);
                    // write the file
                    try{
                        File f = new File(getFilesDir(), "file.ser");
                        FileOutputStream fo = new FileOutputStream(f);
                        ObjectOutputStream o = new ObjectOutputStream(fo);
                        o.writeObject(a);
                        o.close();
                        fo.close();
                    }
                    catch(IOException e2){
                    }

                }
            });

        }
        @JavascriptInterface
        public void play() {
            Log.i(LOG_TAG, "I am in the play javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    //see playLastRecordAudio
                    mediaPlayer = new MediaPlayer();    // object to play the audio
                    try {
                        mediaPlayer.setDataSource(AudioSavePath);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();// play the audio
                    Toast.makeText(AddMemo.this, "Playing",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        @JavascriptInterface
        public void stoprec() {
            Log.i(LOG_TAG, "I am in the stoprec javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Toast.makeText(AddMemo.this, "Stopping recording",
                            Toast.LENGTH_LONG).show();
                    //see stop playing recording
                    if(mediaPlayer != null){
                        mediaPlayer.stop(); // stop audio
                        mediaPlayer.release(); // free up memory
                        MediaRecorderReady();
                    }
                }
            });

        }



        @JavascriptInterface
        public void exit() {
            Log.i(LOG_TAG, "I am in the exit javascript call.");
            runOnUiThread(new Runnable() {
                public void run() {
                    /*Method code here*/
                    Toast.makeText(AddMemo.this, "Exit",
                            Toast.LENGTH_LONG).show();

                    //pop the activity off the stack
                    Intent i = new Intent(AddMemo.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }
            });
        }

    }


    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePath);
    }

    // permissions from user
    private void requestPermission() {
        ActivityCompat.requestPermissions(AddMemo.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }
    // callback method
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AddMemo.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddMemo.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}
