
package com.example.slidecutlistview;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        CustomSwipeListView slideListView = (CustomSwipeListView) findViewById(R.id.slideCutListView);
        CustomSwipeAdapter adapter = new CustomSwipeAdapter(this, makeData());

        slideListView.setAdapter(adapter);
        slideListView.setRemoveListener(adapter);
    }

    /**
     * 初始化一些模拟数据
     */
    private List<TestModel> makeData() {

        List<TestModel> dataSourceList = new LinkedList<TestModel>();

        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.setTestDate("2015-01-0" + i);
            model.setTestTitle("TestItem" + i);
            dataSourceList.add(model);
        }
        return dataSourceList;
    }

}
