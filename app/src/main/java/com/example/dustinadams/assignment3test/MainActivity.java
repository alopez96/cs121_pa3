package com.example.dustinadams.assignment3test;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public JSONObject jos = null;
    public JSONArray ja = null;
    private static final String TAG = "JSON_LIST";

    MediaPlayer mediaPlayer ;
    String AudioSavePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onResume(){
        super.onResume();
        ListView list = findViewById(R.id.data_list_view);
        TextView text = findViewById(R.id.text);
        text.setVisibility(View.INVISIBLE);

        Log.d(TAG, ""+getFilesDir());

        jos = null;
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
            int aSize = Integer.valueOf(j);
            // Create an array and assign each element to be the title
            // field of each of the ListData objects (from the array list)
            String[] listItems = new String[aSize];
            for(int i = 0; i < aSize; i++){
                listItems[i] = "audio recording" + (i+1);
            }
            // Show the list view with the each list item an element from listItems
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
            list.setAdapter(adapter);

            // Set an OnItemClickListener for each of the list items
            final Context context = this;
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // object to play the audio
                    // the path we'll be saving the file to. Notice it is from the external storage
                    // directory
                    AudioSavePath =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    (position+1)  + ".3gp";

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(AudioSavePath);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();// play the audio
                    Toast.makeText(MainActivity.this, "Recording Playing",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        catch(IOException e){
            // There's no JSON file that exists, so don't
            // show the list. But also don't worry about creating
            // the file just yet, that takes place in AddText.
            //Here, disable the list view
            list.setEnabled(false);
            list.setVisibility(View.INVISIBLE);

            //show the text view
            text.setVisibility(View.VISIBLE);
        }
    }







    // This method will just show the menu item (which is our button "ADD")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    // Here is the event handler for the menu button that I forgot in class.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                //the R.id.action_favorite is the ID of our button (defined in strings.xml).
                Intent i = new Intent(this, AddMemo.class);
                startActivity(i);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
}
