package com.example.ddvoice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.database.Cursor;
public class AddLike{//��ӵ��������ݿ⣬���롾���-��/�ȡ� �ļ�ֵ�ԣ�����ʾ���ʱ���������������ݿ⣬��ʾ���ж��������ж����˲� ��1Ϊ�ޣ�2Ϊ��
	//private EditText et1, et2, et3;
	//private Button b1;
	private String mReaction;
	private MainActivity mActivity;
	private int mStatus;
	public AddLike(String reaction,int status,MainActivity activity){
		mReaction=reaction;
		mStatus=status;
		mActivity=activity;
	}
	//public void onCreate(Bundle savedInstanceState) {
	//	super.onCreate(savedInstanceState);
	//	setContentView(R.layout.add);
	//	this.setTitle("����ղ���Ϣ");
		//et1 = (EditText) findViewById(R.id.EditTextName);
		//et2 = (EditText) findViewById(R.id.EditTextUrl);
		//et3 = (EditText) findViewById(R.id.EditTextDesc);
		//b1 = (Button) findViewById(R.id.ButtonAdd);
		//b1.setOnClickListener(new OnClickListener() {
	public void start(){
		ContentValues values = new ContentValues();
		values.put("reaction", mReaction);
		values.put("like",mStatus);
		DBHelper helper = new DBHelper(mActivity);
		helper.insert(values);
		//mActivity.speak("���ݲ���ɹ�", false);
		/*Intent intent = new Intent(AddActivity.this,
				QueryActivity.class);
		startActivity(intent);*/
		/*	public void onClick(View v) {
				String name = et1.getText().toString();
				String url = et2.getText().toString();
				String desc = et3.getText().toString();
				
			}*/
		
	}
	
}