package com.ehappy.exsqlite03;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db = null;
    private final static String	CREATE_TABLE = "CREATE TABLE table01" +
            "(_id INTEGER PRIMARY KEY,name TEXT,price INTERGER)";

    ListView listview01;
    Button btnSearch,btnSearchAll;
    EditText edtID;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 取得元件
        edtID=(EditText)findViewById(R.id.edtID);
        btnSearch=(Button)findViewById(R.id.btnSaerch);
        btnSearchAll=(Button)findViewById(R.id.btnSearchAll);
        listview01=(ListView)findViewById(R.id.ListView01);
        // 設定偵聽
        btnSearch.setOnClickListener(myListener);
        btnSearchAll.setOnClickListener(myListener);
        listview01.setOnItemClickListener(listview01Listener);
        // 建立資料庫，若資料庫已經存在則將之開啟
        db =openOrCreateDatabase("db1.db", MODE_PRIVATE, null);
        try{
            db.execSQL(CREATE_TABLE); // 建立資料表
            db.execSQL("INSERT INTO table01 (name,price) values ('香蕉',30)"); // 新增資料
            db.execSQL("INSERT INTO table01 (name,price) values ('西瓜',120)");
            db.execSQL("INSERT INTO table01 (name,price) values ('梨子',250)");
            db.execSQL("INSERT INTO table01 (name,price) values ('水蜜桃',280)");
        }catch (Exception e){
        }
        cursor=getAll();       // 查詢所有資料
        UpdateAdapter(cursor); // 載入資料表至 ListView 中
    }

    private ListView.OnItemClickListener listview01Listener=
            new ListView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    cursor.moveToPosition(position);
                    Cursor c=get(id);
                    String s= "id=" + id + "\r\n" + "name=" + c.getString(1) + "\r\n" + "price=" + c.getInt(2);
                    Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
                }
            };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close(); // 關閉資料庫
    }

    private Button.OnClickListener myListener=new Button.OnClickListener(){
        public void onClick(View v){
            try{
                switch (v.getId()){
                    case R.id.btnSaerch:{      // 查詢單筆
                        long id = Integer.parseInt(edtID.getText().toString());
                        cursor=get(id);
                        UpdateAdapter(cursor); // 載入資料表至 ListView 中
                        break;
                    }case R.id.btnSearchAll:{   // 查詢全部
                        cursor=getAll();       // 查詢所有資料
                        UpdateAdapter(cursor); // 載入資料表至 ListView 中
                        break;
                    }
                }
            }catch (Exception err){
                Toast.makeText(getApplicationContext(), "查無此資料!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void UpdateAdapter(Cursor cursor){
        if (cursor != null && cursor.getCount() >= 0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.mylayout,// 自訂的 mylayout.xml
                cursor, // 資料庫的 Cursors 物件
                new String[] {"_id","name", "price" }, // _id、name、price 欄位
                new int[] {R.id.txtId,R.id.txtName, R.id.txtPrice}, //與 _id、name、price對應的元件
                0); // adapter 行為最佳化
            listview01.setAdapter(adapter); // 將adapter增加到listview01中
        }
    }

    public Cursor getAll(){ // 查詢所有資料
        Cursor cursor= db.rawQuery("SELECT _id, name, price FROM table01",null);
        return cursor;
    }

    public Cursor get(long rowId) throws SQLException { // 查詢指定 ID 的資料
        Cursor cursor= db.rawQuery("SELECT _id, name, price FROM table01 WHERE _id="+rowId,null);
        if (cursor.getCount()>0)
            cursor.moveToFirst();
        else
            Toast.makeText(getApplicationContext(), "查無此筆資料!", Toast.LENGTH_SHORT).show();
        return cursor;
    }
}