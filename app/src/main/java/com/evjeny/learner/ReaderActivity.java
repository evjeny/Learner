package com.evjeny.learner;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evjeny.learner.models.FileUtils;
import com.evjeny.learner.models.JSONIO;
import com.evjeny.learner.models.LearnItem;
import com.evjeny.learner.models.OnSwipeTouchListener;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
/**
 * Created by Evjeny on 04.07.2017 21:35.
 */

public class ReaderActivity extends AppCompatActivity {
    private TextView title, content;
    private WebView webView;
    private LinearLayout memory, truthfulness, card;

    private SharedPreferences sp;
    private JSONIO jsonio;
    private FileUtils fileUtils;
    private Random random;
    private FilePickerDialog dialog;

    private ArrayList<LearnItem> items;
    private ArrayList<LearnItem> temp;
    private LearnItem currentItem;
    private String encoding;
    private String page_encoding;
    private String mimeType;
    private boolean isMem = true;
    private int mode = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_screen);
        title = (TextView) findViewById(R.id.r_scr_title);
        content = (TextView) findViewById(R.id.r_scr_content);
        webView = (WebView) findViewById(R.id.r_scr_wv);
        memory = (LinearLayout) findViewById(R.id.r_scr_rm);
        truthfulness = (LinearLayout) findViewById(R.id.r_scr_tr);
        card = (LinearLayout) findViewById(R.id.r_scr_viewer_card);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        jsonio = new JSONIO();
        fileUtils = new FileUtils();
        random = new Random();

        items = new ArrayList<>();
        temp = new ArrayList<>();
        currentItem = new LearnItem("");

        card.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                if(isMem) rem();
                else wrong();
            }

            @Override
            public void onSwipeRight() {
                if(isMem) rem();
                else right();
            }
        });

        mode = sp.getBoolean("pref_learn_content",
                true) ? 0 : 1;
        encoding = sp.getString("read_encoding", getString(R.string.pref_encoding));
        page_encoding = sp.getString("encoding", getString(R.string.pref_encoding));
        mimeType = sp.getString("mimeType", getString(R.string.pref_mimeType));

        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName(page_encoding);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_reader_import:
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.MULTI_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.extensions = new String[]{"json"};
                dialog = new FilePickerDialog(ReaderActivity.this, properties);
                dialog.setTitle("Choose files");
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        try {
                            items.clear();
                            items.addAll(jsonio.getList(fileUtils.readFiles(fileUtils.files(files),
                                    encoding)));
                            temp.clear();
                            temp.addAll(items);
                            initRandomWord(temp);
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.menu_reader_count:
                Toast.makeText(ReaderActivity.this, "All: "+items.size() +
                        "\nLeft: " + temp.size(), Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_reader_clear:
                final AlertDialog.Builder builder = new AlertDialog.Builder(ReaderActivity.this);
                builder.setTitle("Clear");
                builder.setMessage("Clear all items?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentItem = null;
                        temp.clear();
                        items.clear();
                        title.setText("");
                        content.setText("");
                        webView.setVisibility(View.INVISIBLE);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void remember(View v) {
        rem();
    }

    public void right(View v) {
        right();
    }
    public void wrong(View v) {
        wrong();
    }

    private void rem() {
        memory.setVisibility(View.GONE);
        truthfulness.setVisibility(View.VISIBLE);
        if(currentItem!=null) {
            if(mode==0) {
                content.setVisibility(View.VISIBLE);
                content.setText(currentItem.getContent());
                if (!currentItem.getHtml().equals("")) {
                    webView.setVisibility(View.VISIBLE);
                    webView.loadDataWithBaseURL(null, currentItem.getHtml(),
                            mimeType, page_encoding, null);
                }
            } else {
                title.setVisibility(View.VISIBLE);
                title.setText(currentItem.getName());
            }
            isMem = false;
        }
    }
    private void right() {
        temp.remove(currentItem);
        initRandomWord(temp);
        isMem = true;
    }
    private void wrong() {
        initRandomWord(temp);
        isMem = true;
    }

    private void initRandomWord(ArrayList<LearnItem> todo) {
        if(todo.size()!=0) {
            currentItem = todo.get(random.nextInt(todo.size()));
            if(mode==0) {
                title.setText(currentItem.getName());
                content.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
            } else {
                title.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                content.setText(currentItem.getContent());
                webView.setVisibility(View.VISIBLE);
                if (!currentItem.getHtml().equals("")) {
                    webView.loadDataWithBaseURL(null, currentItem.getHtml(),
                            mimeType, page_encoding, null);
                }
            }
            memory.setVisibility(View.VISIBLE);
            truthfulness.setVisibility(View.GONE);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ReaderActivity.this);
            builder.setTitle("Restart");
            builder.setMessage("Would you learn all items one more time?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    temp.addAll(items);
                    initRandomWord(temp);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(dialog!=null)
                    {
                        dialog.show();
                    }
                }
                else {
                    Toast.makeText(ReaderActivity.this,"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
