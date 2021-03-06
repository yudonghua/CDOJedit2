package com.example.pc.cdojedit2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String word[];
    ArrayAdapter<String> adapter;
    AutoCompleteEditText autoCompleteEditText;
    SharedPreferences content;
    Vector<String> vector;
    String sd="";
    int t;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        autoCompleteEditText = (AutoCompleteEditText) findViewById(R.id.text);
        content = getSharedPreferences("content",
                Activity.MODE_PRIVATE);
        String contentstring = content.getString("content", "");
        autoCompleteEditText.setText(contentstring);
        autoCompleteEditText.setSelection(contentstring.length());
        try {
            sd=Environment.getExternalStorageDirectory().getCanonicalPath()+"/CDOJ/";
        } catch (IOException e) {
            e.printStackTrace();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static Vector<String> GetFileName(String fileAbsolutePath) {
        Vector<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if(subFile==null)return vecFile;
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                vecFile.add(filename);
            }
        }
        return vecFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            try {
                FileInputStream fis = new FileInputStream(uri.toString().substring("file://".length()));
                byte[] data0 = new byte[102400];
                int length = fis.read(data0);
                ;
                autoCompleteEditText.setText(new String(data0, 0, length));
                autoCompleteEditText.setSelection(length);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        String strFilePath = filePath + fileName;
        String strContent = strcontent;
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
//            else {
//                file.delete();
//                file.createNewFile();
//            }
//            new FileOutputStream(file);
            FileOutputStream io = new FileOutputStream(file);
            io.write(strContent.getBytes());
            io.close();
//            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//            raf.seek(0);
//            raf.write(strContent.getBytes());
//            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            SharedPreferences content = getSharedPreferences("content", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = content.edit();
            editor.putString("content", autoCompleteEditText.getText().toString());
            editor.commit();
            dialog(null);
            return true;
        }
        if (id == R.id.action_search) {
            vector = GetFileName(sd);
            dialog2(autoCompleteEditText);
        }
        if (id == R.id.action_up) {
            autoCompleteEditText.up();
            return true;
        }
        if (id == R.id.action_down) {
            autoCompleteEditText.down();
            return true;
        }
        if (id == R.id.action_run) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getnum(View view){
        int n[]=new int[2];
        view.getLocationInWindow(n);
        t=listView.pointToPosition(n[0],n[1]);
        Log.d(t+"","??");
    }
    public AlertDialog dialog;
    public void dialog2(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        adapter = new ArrayAdapter<String>(this, R.layout.word, getData2());
        listView = new ListView(this);
        if (adapter != null) {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    t=i;
                    String filePath = sd;
                    String fileName = vector.get(t);
                    try {
                        FileInputStream fis=new FileInputStream(filePath+fileName);
                        byte[] data0=new byte[102400];
                        int length= 0;
                        length = fis.read(data0);
                        autoCompleteEditText.setText(new String(data0,0,length));
                        autoCompleteEditText.setSelection(length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    Log.d(""+t+":",vector.get(t));
                }
            });
            builder.setView(listView);
        }
        builder.setTitle("请选择文件名");
        dialog=builder.show();
    }

    List<String> data;

    public List<String> getData2() {
        data = new ArrayList<String>();
        for (int i = 0; i < vector.size(); i++) {
            data.add(vector.get(i));
        }
        return data;
    }

    public void dialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText editText = new EditText(this);
        editText.setHint("文件名");
        TextView text = new TextView(this);
        text.setText("文件保存在CDOJ目录下");
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(editText);
        linearLayout.addView(text);
        builder.setView(linearLayout);
        builder.setTitle("请输入文件名");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String filePath = sd;
                String fileName = editText.getText().toString();
                writeTxtToFile(autoCompleteEditText.getText().toString(), filePath, fileName);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.pc.cdojedit2/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.pc.cdojedit2/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
