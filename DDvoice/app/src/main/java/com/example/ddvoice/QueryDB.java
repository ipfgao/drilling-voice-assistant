package com.example.ddvoice;

import android.database.Cursor;

public class QueryDB {
	private MainActivity mActivity;
	public QueryDB(MainActivity activity){
		mActivity=activity;
	}
	public void start(){
		DBHelper helper = new DBHelper(mActivity);
		Cursor c= helper.query();
		if (c.moveToFirst() == false)
		{
			mActivity.speak("数据库中没有内容",false);
		//为空的Cursor
		return;
		}
		//读取出数据
		int reactionColumnIndex=-1;
		int likeColumnIndex=-1;
		String reaction=null;
		int like=0;
		//获得第一个值
		reactionColumnIndex = c.getColumnIndex("reaction");
		reaction = c.getString(reactionColumnIndex);
		likeColumnIndex=c.getColumnIndex("like");
		like=c.getInt(likeColumnIndex);
		mActivity.speak(reaction+":"+like,false);
		//获取剩下的值
		while(c.moveToNext())
		{
			reactionColumnIndex = c.getColumnIndex("reaction");
			reaction = c.getString(reactionColumnIndex);
			likeColumnIndex=c.getColumnIndex("like");
			like=c.getInt(likeColumnIndex);
			mActivity.speak(reaction+":"+like,false);
		//光标移动成功
		//把数据取出
		}
		
	}
}
