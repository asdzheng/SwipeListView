##这是一个仿知乎安卓客户端滑动删除撤销的ListView

效果图：![Rendering](http://img.blog.csdn.net/20150426214820721)

###使用方法：

layout里

```
 <com.example.slidecutlistview.CustomSwipeListView
        android:id="@+id/slideCutListView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:divider="@drawable/reader_item_divider"
        android:listSelector="@android:color/transparent" >
 </com.example.slidecutlistview.CustomSwipeListView>
 
```

代码里

```
        CustomSwipeListView slideListView = (CustomSwipeListView) findViewById(R.id.slideCutListView);
        CustomSwipeAdapter adapter = new CustomSwipeAdapter(this, makeData());

        slideListView.setAdapter(adapter);
        slideListView.setRemoveListener(adapter)
```
        
###代码的分析请看[我的博客介绍](http://blog.csdn.net/asdzheng/article/details/44278469)

感谢这个开源项目提供部分源码及思路[custom-swipelistview](https://github.com/xyczero/custom-swipelistview)
