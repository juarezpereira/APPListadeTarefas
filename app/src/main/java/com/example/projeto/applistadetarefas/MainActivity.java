package com.example.projeto.applistadetarefas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtTask;
    private ListView mListView;
    private Button btnSetTask;

    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> mArrayAdapter;
    private List<String> mList;
    private List<Integer> mListPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tarefas");

        edtTask = (EditText)findViewById(R.id.edtTask);
        mListView = (ListView)findViewById(R.id.lstTask);
        btnSetTask = (Button)findViewById(R.id.btnSetTask);


        getTaskDataBase();

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                alert(position);

                return false;
            }
        });

        btnSetTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewTask();

            }
        });

    }

    private void getTaskDataBase(){

        try {

            bancoDados = openOrCreateDatabase("ListaDeTarefas",MODE_PRIVATE,null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR(50))");

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC",null);

            int indiceColunaID = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            mListPosition = new ArrayList<>();
            mList = new ArrayList<>();

            mArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    R.layout.simple_list,
                    android.R.id.text1,
                    mList);

            mListView.setAdapter(mArrayAdapter);

            cursor.moveToFirst();
            while (cursor!=null){
                mList.add(cursor.getString(indiceColunaTarefa));
                mListPosition.add(Integer.parseInt(cursor.getString(indiceColunaID)));
                cursor.moveToNext();
            }


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void addNewTask(){

        try{
            if(edtTask.equals("")){
                Toast.makeText(this, "Empty text field", Toast.LENGTH_SHORT).show();
            }else {
                bancoDados.execSQL("INSERT INTO tarefas(tarefa) VALUES ('"+edtTask.getText().toString()+"')");
                edtTask.setText("");
                getTaskDataBase();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void removeTask(int id){

        try{
            bancoDados.execSQL("DELETE FROM tarefas WHERE id="+id);
            Toast.makeText(this,"Task removed",Toast.LENGTH_LONG).show();
            getTaskDataBase();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void alert(final Integer id){

        new AlertDialog.Builder(this)
                .setTitle("Remove item")
                .setMessage("Do you really want to remove the task?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeTask(mListPosition.get(id));
                getTaskDataBase();
            }
        }).setNegativeButton("NO", null).show();

    }

}
