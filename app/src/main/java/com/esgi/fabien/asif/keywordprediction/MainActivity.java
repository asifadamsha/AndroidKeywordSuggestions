package com.esgi.fabien.asif.keywordprediction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private static ListMultimap<String, String> suggestionMapList = ArrayListMultimap.create();
    private ListView listView;
    private List<String> suggestions = new ArrayList<>();
    private ArrayAdapter<String> suggestionAdapter;

    private static final int GRAM_0 = 0;
    private static final int GRAM_1 = 1;
    private static final int GRAM_2 = 2;

    private static final int MAX_SUGGETIONS = 3;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        listView = findViewById(R.id.list_view);

        suggestionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        listView.setAdapter(suggestionAdapter);

        // csv to memory
        ListMultimap<String, String> zeroGramSuggestions = getSuggestionListFromCSV(GRAM_0, "zeroGram.csv");

        if (zeroGramSuggestions != null) {
            suggestionMapList.putAll(zeroGramSuggestions);
        }

        ListMultimap<String, String> oneGramSuggestions = getSuggestionListFromCSV(GRAM_1, "oneGram.csv");

        if (oneGramSuggestions != null) {
            suggestionMapList.putAll(oneGramSuggestions);
        }

        ListMultimap<String, String> twoGramSuggestions = getSuggestionListFromCSV(GRAM_2, "twoGram.csv");

        if (twoGramSuggestions != null) {
            suggestionMapList.putAll(twoGramSuggestions);
        }

        // suggestion for empty value
        if (suggestionMapList != null) {
            showSuggestions(getTextSuggestions(" "));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (suggestions != null) {

                    String editTextString = editText.getText().toString();

                    if (!editTextString.isEmpty()) {
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

                String userEntry = s.toString().trim();

                if (suggestionMapList != null) {

                    if (userEntry.isEmpty()) {
                        showSuggestions(getTextSuggestions(" "));
                    } else {
                        showSuggestions(getTextSuggestions(userEntry));
                    }

                }

            }
        });

    }

    private ListMultimap getSuggestionListFromCSV(int gramType, String csvFileName) {

        if (checkReafPermission()) {

            String filePath = getFilePath(csvFileName);

            Log.i(TAG, "File path : " + filePath);

            if (isFileReadable(filePath)) {

                CSVReader reader = null;

                try {
                    reader = new CSVReader(new FileReader(filePath), ';');
                    String[] nextLine;

                    ListMultimap<String, String> multiMap = ArrayListMultimap.create();

                    String key = " ";
                    String value = " ";

                    while ((nextLine = reader.readNext()) != null) {

                        if (gramType == GRAM_0) {
                            key = " ";
                            value = nextLine[0];
                        }

                        if (gramType == GRAM_1) {
                            key = nextLine[0];
                            value = nextLine[1];
                        }

                        if (gramType == GRAM_2){
                            key = nextLine[0] + " " + nextLine[1];
                            value = nextLine[2];
                        }

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

    private List<String> getTextSuggestions(String word) {

        List<String> suggestionStrings = suggestionMapList.get(word);

        /*if (suggestionStrings != null) {
            return suggestionStrings.subList(0, MAX_SUGGETIONS);
        }*/

        return suggestionStrings;
    }

    private void showSuggestions(List<String> foundSuggestions) {
        if (foundSuggestions != null) {
            suggestions.clear();
            suggestions.addAll(foundSuggestions);
            suggestionAdapter.notifyDataSetChanged();
        }
    }

    private String getFilePath(String fileName) {
        return Environment.
                getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/" + fileName).getPath();
    }

    private boolean isFileReadable(String filePath) {
        return new File(filePath).canRead();
    }

    private boolean checkReafPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
