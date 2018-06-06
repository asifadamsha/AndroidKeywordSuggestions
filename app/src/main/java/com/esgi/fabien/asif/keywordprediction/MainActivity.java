package com.esgi.fabien.asif.keywordprediction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.opencsv.CSVReader;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private ListMultimap oneGramMapList = null;
    private ListView listView;
    private List<String> suggestions = new ArrayList<>();
    private ArrayAdapter<String> suggestionAdapter;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        listView = findViewById(R.id.list_view);

        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        listView.setAdapter(suggestionAdapter);

        oneGramMapList = getSuggestionListFromCSV("oneGramSuggestion.csv");

        if (oneGramMapList != null) {

            suggestions.addAll(oneGramMapList.get("je"));
            suggestionAdapter.notifyDataSetChanged();
            Log.i(TAG, "Prediction for 'je' : " + suggestions);

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (suggestions != null) {

                    String editTextString = editText.getText().toString();

                    if(!editTextString.isEmpty()){
                        editText.append(" ");
                    }

                    String clickedItem = suggestions.get(position);
                    editText.append(clickedItem);
                }

            }
        });

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                //Log.i(TAG, "onTextChanged : " + sequence);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (oneGramMapList != null) {

                    //String typedText = s.toString();

                    //suggestions = oneGramMapList.get(typedText);



                   // Log.i(TAG, "Prediction for " + typedText + " : " + arraySuggestion);
                }
            }
        });

    }

    private ListMultimap getSuggestionListFromCSV(String csvFileName) {

        if (checkReafPermission()) {

            String filePath = getFilePath(csvFileName);

            Log.i(TAG, "File path : " + filePath);

            if (isFileReadable(filePath)) {

                CSVReader reader = null;

                try {
                    reader = new CSVReader(new FileReader(filePath));
                    String[] nextLine;

                    ListMultimap<String, String> multiMap = ArrayListMultimap.create();

                    while ((nextLine = reader.readNext()) != null) {

                        String key = nextLine[0];
                        String value = nextLine[1];
                        int occurrence = Integer.parseInt(nextLine[2]);

                        Log.i(TAG, "key : " + key + ", value : " + value + ", occurrence : " + occurrence);

                        multiMap.put(key, value);
                    }

                    return multiMap;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getBaseContext(), "File read error", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getBaseContext(), "Allow storage read permission", Toast.LENGTH_SHORT).show();
        }

        return null;

    }

    /*private MultiMap getSuggestionMapListFromCSV(String csvFileName) {

        if (checkReafPermission()) {

            String filePath = getFilePath(csvFileName);

            Log.i(TAG, "File path : " + filePath);

            if (isFileReadable(filePath)) {

                CSVReader reader = null;

                try {
                    reader = new CSVReader(new FileReader(filePath));
                    String[] nextLine;



                    while ((nextLine = reader.readNext()) != null) {

                        String key = nextLine[0];
                        String value = nextLine[1];
                        String occurrence = nextLine[2];

                        Log.i(TAG, "key : " + key + ", value : " + value + ", occurrence : " + occurrence);


                    }

                    return multiMap;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getBaseContext(), "File read error", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getBaseContext(), "Allow storage read permission", Toast.LENGTH_SHORT).show();
        }

        return null;

    }*/

    private String getFilePath(String fileName) {
        return Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + fileName).getPath();
    }

    private boolean isFileReadable(String filePath) {
        return new File(filePath).canRead();
    }

    private boolean checkReafPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
