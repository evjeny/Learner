package com.evjeny.learner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Evjeny on 01.10.2017 17:18.
 */

public class CodeActivity extends AppCompatActivity {
    private EditText code;
    private String output = "", debug = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_screen);
        code = (EditText) findViewById(R.id.code_et);
        Intent p = getIntent();
        String t = p.getStringExtra("code");
        output = p.getStringExtra("output");
        debug = p.getStringExtra("debug");
        code.setText(t);
    }

    public void run(View v) {
        String text = code.getText().toString();
        Intent data = new Intent();
        data.putExtra("code", text);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.code, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_code_output:
                final AlertDialog.Builder outputDialog = new AlertDialog.Builder(CodeActivity.this);
                outputDialog.setTitle("Output");
                outputDialog.setMessage(output);
                outputDialog.setNegativeButton("Copy", (dialog, which) -> {
                    // copy to clipboard
                });
                outputDialog.create().show();
                break;
            case R.id.menu_code_debug:
                final AlertDialog.Builder debugDialog = new AlertDialog.Builder(CodeActivity.this);
                debugDialog.setTitle("Debug");
                debugDialog.setMessage(debug);
                debugDialog.setNegativeButton("Copy", (dialog, which) -> {
                    // copy to clipboard
                });
                debugDialog.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
