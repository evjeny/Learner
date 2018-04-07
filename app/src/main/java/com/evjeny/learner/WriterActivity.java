package com.evjeny.learner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.evjeny.dozer.IOInterface;
import com.evjeny.dozer.Lexer;
import com.evjeny.dozer.Parser;
import com.evjeny.dozer.Token;
import com.evjeny.dozer.ast.Statement;
import com.evjeny.dozer.lib.ArrayValue;
import com.evjeny.dozer.lib.Functions;
import com.evjeny.dozer.lib.IntValue;
import com.evjeny.dozer.lib.StringValue;
import com.evjeny.dozer.visitors.FunctionAdder;
import com.evjeny.learner.models.FileUtils;
import com.evjeny.learner.models.JSONIO;
import com.evjeny.learner.models.LearnItem;
import com.evjeny.learner.models.WIAdapter;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by evjeny on 04.07.2017 21:05.
 */

public class WriterActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private LinearLayout root;
    private EditText title;

    private LayoutInflater inflater;
    private SharedPreferences sp;
    private JSONIO jsonio;
    private FileUtils fileUtils;
    private FilePickerDialog dialog;
    private WIAdapter adapter;

    private ArrayList<LearnItem> items;
    private String save_encoding;

    private final int CODER_REQUEST_CODE = 1;
    private String code = "";
    private String coder_debug = "";
    private String coder_output = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writer_screen);

        root = (LinearLayout) findViewById(R.id.w_scr_root);
        title = (EditText) findViewById(R.id.w_scr_title);
        ListView listView = (ListView) findViewById(R.id.w_scr_lv_items);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        jsonio = new JSONIO();
        fileUtils = new FileUtils();

        items = new ArrayList<>();
        adapter = new WIAdapter(this, items);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        save_encoding = sp.getString("save_encoding", getString(R.string.pref_encoding));

        initFunctions();
    }

    public void add(View view) {
        String title_text;
        if (!(title_text = title.getText().toString()).equals("")) {
            LearnItem lt = new LearnItem(title_text);
            items.add(lt);
            title.setText("");
            adapter.notifyDataSetChanged();
        } else {
            Snackbar.make(root, "Text field is empty!", Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(WriterActivity.this);
        builder.setTitle(R.string.edit_item);
        @SuppressLint("InflateParams") View ETDialog = inflater.inflate(R.layout.learn_item_edit_dialog, null);
        final EditText title = (EditText) ETDialog.findViewById(R.id.lit_ed_title);
        title.setText(items.get(position).getName());
        final EditText content = (EditText) ETDialog.findViewById(R.id.lit_ed_content);
        content.setText(items.get(position).getContent());
        final EditText html = (EditText) ETDialog.findViewById(R.id.lit_ed_html);
        html.setText(items.get(position).getHtml());
        builder.setView(ETDialog);
        builder.setPositiveButton(R.string.ok, (dialog1, which) -> {
            String title_str = title.getText().toString();
            String content_str = content.getText().toString();
            String html_str = html.getText().toString();
            if (!title_str.equals("")) {
                LearnItem current = items.get(position);
                current.setName(title_str);
                current.setContent(content_str);
                current.setHtml(html_str);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog12, which) -> {

        });
        builder.setNeutralButton(R.string.delete, (dialog13, which) -> {
            final AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(WriterActivity.this);
            deleteBuilder.setTitle(R.string.delete);
            deleteBuilder.setPositiveButton(R.string.ok, (dialog1312, which12) -> {
                items.remove(position);
                adapter.notifyDataSetChanged();
            });
            deleteBuilder.setNegativeButton(R.string.cancel, (dialog131, which1) -> dialog131.dismiss());
            deleteBuilder.create().show();
        });
        builder.create().show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.writer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_writer_code:
                Intent codeActivity = new Intent(this, CodeActivity.class);
                codeActivity.putExtra("code", code);
                codeActivity.putExtra("output", coder_output);
                codeActivity.putExtra("debug", coder_debug);
                startActivityForResult(codeActivity, CODER_REQUEST_CODE);
                break;
            case R.id.menu_writer_import:
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.MULTI_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.extensions = new String[]{"json"};
                dialog = new FilePickerDialog(WriterActivity.this, properties);
                dialog.setTitle(R.string.choose_files);
                dialog.setDialogSelectionListener(files -> {
                    try {
                        addItems(jsonio.getList(fileUtils.readFiles(fileUtils.files(files))));
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                });
                dialog.show();
                break;
            case R.id.menu_writer_export:
                DialogProperties save_properties = new DialogProperties();
                save_properties.selection_mode = DialogConfigs.SINGLE_MODE;
                save_properties.selection_type = DialogConfigs.DIR_SELECT;
                save_properties.root = new File(DialogConfigs.DEFAULT_DIR);
                save_properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
                dialog = new FilePickerDialog(WriterActivity.this, save_properties);
                dialog.setTitle(getString(R.string.choose_dir));
                dialog.setDialogSelectionListener(files -> {
                    String filename = sp.getString("default_prefix",
                            getString(R.string.pref_file_prefix)) +
                            getCurrentDate() + ".json";
                    File output = new File(new File(files[0]), filename);
                    try {
                        fileUtils.saveFile(output, jsonio.getJson(items), save_encoding);
                        Toast.makeText(WriterActivity.this, getString(R.string.file) + "\""
                                        + filename + "\"" + getString(R.string.saved_to_sd),
                                Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                dialog.show();
                break;
            case R.id.menu_writer_count:
                Toast.makeText(WriterActivity.this, getString(R.string.all) + ": " + items.size(),
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_writer_clear:
                final AlertDialog.Builder builder = new AlertDialog.Builder(WriterActivity.this);
                builder.setTitle(R.string.clear);
                builder.setMessage(getString(R.string.clear_items) + "?");
                builder.setPositiveButton(R.string.ok, (dialog12, which) -> {
                    items.clear();
                    adapter.notifyDataSetChanged();
                });
                builder.setNegativeButton(R.string.cancel, (dialog1, which) -> dialog1.dismiss());
                builder.create().show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getCurrentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy_HH.mm.ss");
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    private void addItems(ArrayList<LearnItem> _items) {
        items.addAll(_items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {
                        dialog.show();
                    }
                } else {
                    Toast.makeText(WriterActivity.this, R.string.permission_message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initFunctions() {
        Functions.set("addItem", args -> {
            final int length = args.length;
            if (length > 3) throw new RuntimeException("Arguments are incorrect!");
            String itemText = "";
            String content = "";
            String html = "";
            if (length >= 1) itemText = args[0].asString();
            if (length >= 2) content = args[1].asString();
            if (length == 3) html = args[2].asString();
            items.add(new LearnItem(itemText, content, html));
            adapter.notifyDataSetChanged();
            return null;
        });
        Functions.set("addItemWithIndex", args -> {
            final int length = args.length;
            if (length > 4 || length < 1) throw new RuntimeException("Arguments are incorrect!");
            int index = args[0].asInt();
            String itemText = "";
            String content = "";
            String html = "";
            if (length >= 2) itemText = args[1].asString();
            if (length >= 3) content = args[2].asString();
            if (length == 4) html = args[3].asString();
            items.add(index, new LearnItem(itemText, content, html));
            adapter.notifyDataSetChanged();
            return null;
        });
        Functions.set("setItem", args -> {
            final int length = args.length;
            if (length > 4 || length == 0) throw new RuntimeException("Arguments are incorrect!");
            int index = args[0].asInt();
            String itemText = "";
            String content = "";
            String html = "";
            if (length >= 2) itemText = args[1].asString();
            if (length >= 3) content = args[2].asString();
            if (length == 4) html = args[3].asString();
            items.remove(index);
            items.add(index, new LearnItem(itemText, content, html));
            adapter.notifyDataSetChanged();
            return null;
        });
        Functions.set("setItemText", args -> {
            final int length = args.length;
            if (length != 2) throw new RuntimeException("Arguments are incorrect!");
            int index = args[0].asInt();
            String itemText = args[1].asString();
            items.get(index).setName(itemText);
            adapter.notifyDataSetChanged();
            return null;
        });
        Functions.set("setItemContent", args -> {
            final int length = args.length;
            if (length != 2) throw new RuntimeException("Arguments are incorrect!");
            int index = args[0].asInt();
            String itemContent = args[1].asString();
            items.get(index).setContent(itemContent);
            adapter.notifyDataSetChanged();
            return null;
        });
        Functions.set("setItemHtml", args -> {
            final int length = args.length;
            if (length != 2) throw new RuntimeException("Arguments are incorrect!");
            int index = args[0].asInt();
            String itemHtml = args[1].asString();
            items.get(index).setHtml(itemHtml);
            adapter.notifyDataSetChanged();
            return null;
        });
        Functions.set("length", args -> {
            if (args.length != 0) throw new RuntimeException("Arguments are incorrect!");
            return new IntValue(items.size());
        });
        Functions.set("getItemText", args -> {
            if (args.length != 1) throw new RuntimeException("Arguments are incorrect!");
            return new StringValue(items.get(args[0].asInt()).getName());
        });
        Functions.set("getItemContent", args -> {
            if (args.length != 1) throw new RuntimeException("Arguments are incorrect!");
            return new StringValue(items.get(args[0].asInt()).getContent());
        });
        Functions.set("getItemHtml", args -> {
            if (args.length != 1) throw new RuntimeException("Arguments are incorrect!");
            return new StringValue(items.get(args[0].asInt()).getHtml());
        });
        Functions.set("getAllTexts", args -> {
            if (args.length != 0) throw new RuntimeException("Arguments are incorrect!");
            int size = items.size();
            StringValue[] values = new StringValue[size];
            for (int i = 0; i < size; i++) {
                LearnItem curr = items.get(i);
                values[i] = new StringValue(curr.getName());
            }
            return new ArrayValue(values);
        });
        Functions.set("getAllContents", args -> {
            if (args.length != 0) throw new RuntimeException("Arguments are incorrect!");
            int size = items.size();
            StringValue[] values = new StringValue[size];
            for (int i = 0; i < size; i++) {
                LearnItem curr = items.get(i);
                values[i] = new StringValue(curr.getContent());
            }
            return new ArrayValue(values);
        });
        Functions.set("getAllHtmls", args -> {
            if (args.length != 0) throw new RuntimeException("Arguments are incorrect!");
            int size = items.size();
            StringValue[] values = new StringValue[size];
            for (int i = 0; i < size; i++) {
                LearnItem curr = items.get(i);
                values[i] = new StringValue(curr.getHtml());
            }
            return new ArrayValue(values);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODER_REQUEST_CODE && resultCode == RESULT_OK) {
            code = data.getStringExtra("code");

            StringBuilder debug_builder = new StringBuilder();
            debug_builder.append("[TOKENS]");
            StringBuilder output_builder = new StringBuilder();

            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();

            for (Token t : tokens) {
                debug_builder.append(t).append("\n");
            }
            debug_builder.append("[TOKENS]");

            Parser parser = new Parser(tokens, new IOInterface() {
                @Override
                public String input() {
                    return "";
                }

                @Override
                public void output(Object out) {
                    output_builder.append(out);
                }
            });
            final Statement program = parser.parse();
            program.accept(new FunctionAdder());
            program.execute();
            coder_debug = debug_builder.toString();
            coder_output = output_builder.toString();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.exit);
        builder.setMessage(R.string.exit_writer);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> WriterActivity.this.finish());
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }
}
