##这是一个仿知乎安卓客户端滑动删除撤销的List

效果图：![Renderings](http://vdisk-thumb-1.wcdn.cn/frame.1024x768/download.weipan.cn/35119591/fcab3d2fcd1ee297e6d5e459728776784a26cf2e?ssig=MBg9xZfWWy&Expires=1426406259&KID=sae,l30zoo1wmz)

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

感谢这个开源项目提供源码(https://github.com/xyczero/custom-swipelistview
