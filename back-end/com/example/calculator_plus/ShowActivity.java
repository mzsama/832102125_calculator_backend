package com.example.calculator_plus;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends Activity {
    private ListView listView;
    private List<String> resultList;
    public  ShowActivity (){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        MySqliteHelper dbHelper=new MySqliteHelper(this);
        // 初始化视图
        listView = findViewById(R.id.listView);
        resultList = new ArrayList<>();
        // 获取最近的计算结果
        resultList = dbHelper.getRecentResults();

        // 创建适配器并将结果显示在ListView中
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultList);
        listView.setAdapter(adapter);
    }

    public void Clear(View view) {
        MySqliteHelper dbHelper = new MySqliteHelper(this);
        dbHelper.deleteAllResults();
        // 清空列表
        resultList.clear();
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        adapter.clear();

    }

}

