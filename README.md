##这是一个仿知乎安卓客户端滑动删除撤销的List

效果图：![Renderings](http://vdisk-thumb-1.wcdn.cn/frame.1024x768/download.weipan.cn/35119591/fcab3d2fcd1ee297e6d5e459728776784a26cf2e?ssig=MBg9xZfWWy&Expires=1426406259&KID=sae,l30zoo1wmz)

###效果图

        CustomSwipeListView slideListView = (CustomSwipeListView) findViewById(R.id.slideCutListView);
        CustomSwipeAdapter adapter = new CustomSwipeAdapter(this, makeData());

        slideListView.setAdapter(adapter);
        slideListView.setRemoveListener(adapter);
