package com.mahmoudelshamy.corrector;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;


public class MainActivity extends ActionBarActivity {
    // views
    private EditText textSentence;
    private Button buttonCheck;
    private Button buttonCorrect;
    private TextView textWrong;
    private TextView textValid;

    // logic objects
    ArrayList<String> allArabicWords = new ArrayList<>();
    String[] splited;
    ArrayList<String> allArabicLetters = new ArrayList<>();
    String letters;
    ArrayList<String> allExpectedWords = new ArrayList<>();
    HashSet<String> wrongWords = new HashSet<>();
    HashSet<String> correctWords = new HashSet<>();
    HashSet<String> reWrongWords = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init components
        textSentence = (EditText) findViewById(R.id.text_sentence);
        buttonCheck = (Button) findViewById(R.id.button_check);
        buttonCorrect = (Button) findViewById(R.id.button_correct);
        textWrong = (TextView) findViewById(R.id.text_wrong);
        textValid = (TextView) findViewById(R.id.text_valid);

        // read arabic words
        allArabicWords.clear();
        try {
            InputStream is = getResources().openRawResource(R.raw.words);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                allArabicWords.add(line);
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // read arabic letters
        allArabicLetters.clear();
        try {
            InputStream is = getResources().openRawResource(R.raw.letters);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                letters = line;
                allArabicLetters.add(line);
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // add click listeners
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correctWords.clear();
                wrongWords.clear();
                textWrong.setText("");
                textValid.setText("");
                if (!textSentence.getText().toString().isEmpty()) {
                    String sentence = textSentence.getText().toString();
                    splited = sentence.split("\\s+");
                    for (String splited1 : splited) {
                        if (allArabicWords.contains(splited1)) {
                            correctWords.add(splited1);
                        } else {
                            wrongWords.add(splited1);
                        }
                    }
                    for (String wrong : wrongWords) {
                        textWrong.setText(wrong);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "???? ???!", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reWrongWords.clear();
                textValid.setText("");
                if (wrongWords.isEmpty() == false) {
                    reWrongWords.clear();
                    for (String wrong : wrongWords) {
                        // check for plus charaters and remove them and check if new word exist !
                        // puls characters test way
                        pulsCharactersTestWay(wrong);
                        addingTheIdentifiersCharacters(wrong);
                        replaceOneCharacter(wrong);
                        testExistingWordAfterReplacing();
                    }
                }
            }
        });
    }

    private void pulsCharactersTestWay(String word) {
        for (int x = 0; x < word.length(); x++) {
            String newWordOfWrongWord = word.substring(0, x) + word.substring(x + 1);
            if (allArabicWords.contains(newWordOfWrongWord)) {
                reWrongWords.add(newWordOfWrongWord);
                textValid.setText(newWordOfWrongWord);
                break;
            }
        }
    }

    private void addingTheIdentifiersCharacters(String wrong) {
        String identifiers = "??" + wrong;
        for (int x = 0; x < allArabicWords.size(); x++) {
            if (allArabicWords.get(x).equals(identifiers)) {
                reWrongWords.add(identifiers);
                textValid.setText(identifiers);
                break;
            }
        }
    }

    private void replaceOneCharacter(String wrong) {
        allExpectedWords.clear();
        for (int x = 0; x < wrong.length(); x++) {
            for (int y = 0; y < letters.length(); y++) {
                StringBuilder newWord = new StringBuilder(wrong);
                newWord.setCharAt(x, letters.charAt(y));
                allExpectedWords.add(newWord.toString());
            }
        }
    }

    private void testExistingWordAfterReplacing() {
        for (int x = 0; x < allExpectedWords.size(); x++) {
            if (allArabicWords.contains(allExpectedWords.get(x))) {
                textValid.setText(allExpectedWords.get(x));
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuItem_about) {
            String msg = "Developed By: Mahmoud Mostafa Mostafa Elsahamy\nSection: 5";
            AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage(msg).setTitle("About").create();
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
