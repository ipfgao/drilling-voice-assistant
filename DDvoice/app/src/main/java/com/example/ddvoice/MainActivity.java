package com.example.ddvoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ddvoice.util.SystemUiHider;


import com.google.gson.Gson;
import com.iflytek.cloud.*;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.example.bean.Result;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity implements OnItemClickListener ,OnClickListener{

	//���޹���
	protected static final int LIKE = 1;
	protected static final int DISLIKE = 2;
	protected static String selectedMessage=null;//���������
	
	//�����ϳ�
	// �����ϳɶ���
		private SpeechSynthesizer mTts;

		// Ĭ�Ϸ�����
		private String voicer="xiaoyan";
	
	
	
	//����
	
	
	
	public static boolean serviceFlag=false;//��ʾ�Ƿ���һ������� 
	
	
	private String mainService=null;//��ʾĿǰ�Ի���������������һ����������
	private String branchService=null;//��ʾĿǰ�Ի��������������������е���һ���֧������
	
	public static JSONObject semantic = null,slots =null,answer=null,datetime=null,location=null,data=null;public static String operation = null,service=null;
	public static JSONArray result=null;
	public static String receiver=null, name = null,price=null,code=null,song = null,keywords=null,content=null,
			url=null,text=null,time=null,date=null,city=null,sourceName=null,target=null,source=null;
	public static String[] weatherDate=null,weather=null,tempRange=null,airQuality=null,wind=null,humidity=null,windLevel=null;

	
	
	
	private TextUnderstander mTextUnderstander;// �����������ı������壩��
	
	
	//from SiriCN
	private ProgressDialog mProgressDialog;//������ʾ��
	private MediaPlayer player;//��������
	
	private ListView mListView;
	private ArrayList<SiriListItem> list;
	ChatMsgViewAdapter mAdapter;



	public static  String SRResult="";	//ʶ����
	private static String SAResult="";//����ʶ����
	private static String TAG = MainActivity.class.getSimpleName();
	//Toast��ʾ��Ϣ
	private Toast info;
	//�ı�����
	private TextView textView;
	//����ʶ��
	private SpeechRecognizer mIat;
	// ������дUI
	private RecognizerDialog mIatDialog;
	// ��HashMap�洢��д���
		private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	// ��������
		private String mEngineType = SpeechConstant.TYPE_CLOUD;
		private String mEngineTypeTTS = SpeechConstant.TYPE_CLOUD;
		private SharedPreferences mSharedPreferences;
		private SharedPreferences mSharedPreferencesTTS;
		
		
	//����ʶ�������
		private RecognizerListener recognizerListener = new RecognizerListener() { 
			public void onBeginOfSpeech() {
				//info.makeText(getApplicationContext(), "��ʼ˵��", 100).show();
			}	 
			public void onError(SpeechError error) {
				// Tips��
				// �����룺10118(��û��˵��)��������¼����Ȩ�ޱ�������Ҫ��ʾ�û���Ӧ�õ�¼��Ȩ�ޡ�
				// ���ʹ�ñ��ع��ܣ�����+����Ҫ��ʾ�û���������+��¼��Ȩ�ޡ�
				//info.makeText(getApplicationContext(), error.getPlainDescription(true), 1000).show();
				showTip(error.getPlainDescription(true));
				speak("û��������˵����",false);
				//startSpeenchRecognition();
			} 
			public void onEndOfSpeech() {
				//info.makeText(getApplicationContext(), "����˵��", 100).show();
				showTip("����˵��");
			} 
			public void onResult(RecognizerResult results, boolean isLast) {
				//Log.d("dd", results.getResultString());
				printResult(results,isLast);

				if (isLast) {
					// TODO ���Ľ��
				}
			} 
			public void onVolumeChanged(int volume,byte[] data) {
				showTip("�����mic˵������ǰ������С��" + volume);
				Log.d(TAG, "������Ƶ���ݣ�"+data.length);
			} 
			public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			}
		};



	
	/**
	 * ��ʼ����������
	 */
	private InitListener mInitListener = new InitListener() {
		public void onInit(int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("��ʼ��ʧ�ܣ������룺" + code);
			}
		}
	};

	//��ʼ�����������ı������壩��
    private InitListener textUnderstanderListener = new InitListener() {
		public void onInit(int code) {
			Log.d(TAG, "textUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		//showTip("��ʼ��ʧ��,�����룺"+code);
				Log.d("dd","��ʼ��ʧ��,�����룺"+code);
        	}
		}
    };
	
	
	//private SemanticAnalysis semanticAnalysis;//�������ʵ��
  
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		//���������̷߳���http����
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		info=Toast.makeText(this, "", Toast.LENGTH_SHORT);
			/*mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("���ڳ�ʼ�������Ժ򡭡� ^_^");
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();*/
			showTip("��ʼ����...");
			//info.makeText(getApplicationContext(), "��ʼ����...", 5).show();
	       // showTip("hai");
			initIflytek();
			initUI();
			speechRecognition();
			//mProgressDialog.dismiss();
			showTip("��ʼ�����");
			//info.makeText(getApplicationContext(), "��ʼ�����", 5).show();
			player = MediaPlayer.create(MainActivity.this, R.raw.lock);
			player.start();
			speak("��ã�����СD�����������������֡�", false);
			//runOnUiThread(new Runnable() {
				//@Override
				//public void run() {
					// TODO Auto-generated method stub
					//xiaoDReaction();//�����Ի�����ϵͳ
				//}
			//});
			
		/*	new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(true)
					xiaoDReaction();
				}});*/
    }
  

    public void getJsonData() {
    			//speak("here",false);
    			
				try {
					JSONObject SAResultJson = new JSONObject(SAResult);
					operation=SAResultJson.optString("operation");
					service=SAResultJson.optString("service");
					semantic=SAResultJson.optJSONObject("semantic");
					answer=SAResultJson.optJSONObject("answer");
					data=SAResultJson.optJSONObject("data");
					
					if(data==null){
					}else result=data.optJSONArray("result");
					
					if(result==null){
					}else{
						//����Ҫ��ʼ�����鲻Ȼ���еò����κν��������
						airQuality=new String[10];
						weatherDate=new String[10];
						wind=new String[10];
						humidity=new String[10];
						windLevel=new String[10];
						weather=new String[10];
						tempRange=new String[10];
						for(int i=1;i<7;i++){
							airQuality[i-1]=result.getJSONObject(i).optString("airQuality");
							weatherDate[i-1]=result.getJSONObject(i).optString("date");
							wind[i-1]=result.getJSONObject(i).optString("wind");
							humidity[i-1]=result.getJSONObject(i).optString("humidity");
							windLevel[i-1]=result.getJSONObject(i).optString("windLevel");
							weather[i-1]=result.getJSONObject(i).optString("weather");
							tempRange[i-1]=result.getJSONObject(i).optString("tempRange");
							sourceName=result.getJSONObject(i).optString("sourceName");
						}
						
					}
					
					if(answer==null){
					}else text=answer.optString("text");
					
					if(semantic==null){
					}else slots=semantic.optJSONObject("slots");
					
					if(slots==null){	
					}else{
						receiver=slots.optString("receiver");
						location=slots.optJSONObject("location");
						name = slots.optString("name");
						price= slots.optString("price");
						code = slots.optString("code");
						song = slots.optString("song");
						keywords=slots.optString("keywords");
						content=slots.optString("content");
						url=slots.optString("url");
						target=slots.optString("target");
						source=slots.optString("source");
					}
					
					if(location==null){
					}else{
						city=location.optString("city");
					}
					
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					speak("����json����������",false);
					e.printStackTrace();
				}
				xiaoDReaction();
}

    public void xiaoDReaction(){


		if(serviceFlag==false){//�������һ������вŽ��з�����ж�
			//speak("�жϷ�������",false);
				switch(service){

					case "telephone":{//1 �绰��ط���

						switch(operation){

							case "CALL":{	//1��绰
											//��Ҫ�������绰����code��
											//��ѡ����������name��������category�������������location������Ӫ��operator�����Ŷ�head_num����β��tail_num��
											//���ɶ����ѡ����ȷ����Ҫ����
								//speak("name:"+name+"code:"+code,false);
								CallAction callAction=new CallAction(name,code,this);//Ŀǰ�ɸ������ֻ�绰���벦��绰
								callAction.start();
								break;
							}

							case "VIEW":{	//2�鿴�绰�����¼
											//��Ҫ������
											//��ѡ������δ�ӵ绰�����Ѳ��绰�����ѽӵ绰��
								CallView callview =new CallView(this);
								callview.start();
								break;
							}

							default :break;

						}

						break;
					}

					case "message":{//2 ������ط���

						switch(operation){

							case "SEND":{//1���Ͷ���

								SendMessage sendMessage = new SendMessage(name,code,content,MainActivity.this);
								sendMessage.start();
								break;
							}

							case "VIEW":{//2�鿴���Ͷ���ҳ��

								MessageView messageView=new MessageView(this);
								messageView.start();
								break;
							}



							case "SENDCONTACTS":{//3������Ƭ,Ŀǰֻ��ʶ�����ַ�������
								SendContacts sendContacts = new SendContacts(name,receiver,this);
								sendContacts.start();
								break;
							}
							default :break;
						}

						break;
					}

					case "app":{//3 Ӧ����ط���

						switch(operation){

							case "LAUNCH":{//1��Ӧ��
								OpenAppAction openApp = new OpenAppAction(name,MainActivity.this);
								openApp.start();
								break;
							}

							case "QUERY":{//2Ӧ����������Ӧ��
								SearchApp searchApp = new SearchApp(price,name,this);
								searchApp.start();
								break;
							}

							default:break;

						}
						break;
					}

					case "website":{//4 ��վ��ط���

						switch(operation){

							case "OPEN":{//1��ָ����ַ

								OpenWebsite openWebsite=new OpenWebsite(url,name,this);
								openWebsite.start();
								break;
							}

							default:break;
						}

						break;
					}

					case "websearch":{//5 ������ط���

						switch(operation){

							case "QUERY":{//1����

								SearchAction searchAction =new SearchAction(keywords,MainActivity.this);
								searchAction.Search();
								break;
							}

							default:break;

						}


							break;
					}

					case "faq":{//6 �����ʴ���ط���

						switch(operation){

							case "ANSWER":{//1�����ʴ�
								OpenQA openQA = new OpenQA(text,this);
								openQA.start();

								break;
							}

							default:break;
						}

						break;
					}

					case "chat":{//7 ������ط���

						switch(operation){
							case "ANSWER":{//1����ģʽ

								OpenQA openQA = new OpenQA(text,this);
								openQA.start();

								break;
							}

							default:break;
						}

						break;
					}

					case "openQA":{//8 �����ʴ���ط���

						switch(operation){

							case "ANSWER":{//1�����ʴ�

								OpenQA openQA = new OpenQA(text,this);
								openQA.start();

								break;
							}

							default:break;
						}

						break;
					}

					case "baike":{//9 �ٿ�֪ʶ��ط���

						switch(operation){

						case "ANSWER":{//1�ٿ�

							OpenQA openQA = new OpenQA(text,this);
							openQA.start();

							break;
						}

						default:break;
						}

						break;
					}

					case "schedule":{//10 �ճ���ط���

						switch(operation){

						case "CREATE":{//1�����ճ�/����(ֱ����ת��Ӧ���ý���)

							ScheduleCreate scheduleCreate=new ScheduleCreate(name,time,date,content,this);
							scheduleCreate.start();

							break;
						}

						case "VIEW":{//1�鿴����/����(δʵ��)

							ScheduleView scheduleView = new ScheduleView(name,time,date,content,this);
							scheduleView.start();
							break;
						}


						default:break;
						}

						break;
					}

					case "weather":{//11 ������ط���

						switch(operation){

						case "QUERY":{//1��ѯ����

							SearchWeather searchWeather= new SearchWeather(city,sourceName,weatherDate,weather,tempRange,airQuality,wind,humidity,windLevel,this);
							searchWeather.start();

							break;
						}

						default:break;

						}

						break;
					}

					case "translation":{//12 ������ط���

						switch(operation){

						case "TRANSLATION":{//1����

							Translation translation=new Translation(target,source,content,this);
							translation.start();

							break;
						}

						default:break;

						}

						break;
					}

				default:{
					//speak("��֪����Ҫ������������һ��ʱ���Ҿͻᶮ�ˡ�",false);
					tuling(SRResult);//����ͼ�������������
					SRResult=null;//�ÿ�s
					SAResult=null;
					break;
				}
			}
		}//����ĳ����������

	semantic = null;slots =null;answer=null;datetime=null;location=null;data=null;operation = null;service=null;result=null;
	receiver=null;name = null;price=null;code=null;song = null;keywords=null;content=null;url=null;text=null;time=null;
	date=null;city=null;sourceName=null;target=null;source=null;
	weatherDate=null;weather=null;tempRange=null;airQuality=null;wind=null;humidity=null;windLevel=null;
   }
				
    	//});
			
			
			
	
		/*	if(operation.equals("LAUNCH")){//��Ӧ��
				speak("�õģ�Ϊ������"+name+"...",false);
				OpenAppAction openApp = new OpenAppAction(name,MainActivity.this);
				openApp.runApp();
			}
			if(operation.equals("PLAY")){//�������ֻ���Ƶ
				speak("����֪����ô��...",false);
				if(service.equals("music")){
					PlayAction playAction= new PlayAction(song,MainActivity.this);
					playAction.Play();
				}
				if(service.equals("video")){
					PlayAction playAction= new PlayAction(keywords,MainActivity.this);
					playAction.Play();
				}
			}
			if(operation.equals("QUERY")){//����
				speak("�õģ���������"+keywords+"...",false);
				SearchAction searchAction =new SearchAction(keywords,MainActivity.this);
				searchAction.Search();
			}*/
			
		
	//}
    
    public void initIflytek(){
		//��ʼѶ������
    	//�ҵ�Siri����
    	findViewById(R.id.voice_input).setOnClickListener(MainActivity.this);
    	//�����û��������ö����ſ���ʹ���������񣬽����ڳ�����ڴ����á�����appid��Ҫ�Լ�ȥ�ƴ�Ѷ����վ���룬����ʹ��Ĭ�ϵĽ�����ҵ��;��
		//5aa5fd18���꾮�������ֵ�Ѷ��id
    	SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID +"=5aa5fd18");
    
    }
    
    public void initUI(){//��ʼ��UI�Ͳ���
    	SRResult="";
    	list = new ArrayList<SiriListItem>();
		mAdapter = new ChatMsgViewAdapter(this, list);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setFastScrollEnabled(true);
		registerForContextMenu(mListView);
    }
    
    public void speechRecognition(){
		//��ʼ��
    	//1.����SpeechRecognizer���󣬵ڶ��������� ������дʱ��InitListener
    	mIat= SpeechRecognizer.createRecognizer(MainActivity.this, mInitListener);
    	// ��ʼ����дDialog�����ֻʹ����UI��д���ܣ����贴��SpeechRecognizer
    	mIatDialog = new RecognizerDialog(MainActivity.this, mInitListener);
    	//���������ʼ��
    	mTextUnderstander = TextUnderstander.createTextUnderstander(MainActivity.this, textUnderstanderListener);
    	
    	// ��ʼ���ϳɶ���
    	mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
    }

    //����ʶ��
    public void startSpeenchRecognition(){
    	player = MediaPlayer.create(MainActivity.this, R.raw.begin);
		player.start();

    	mIatDialog.setListener(recognizerDialogListener);
		//��ʾ��д�Ի���
		//mIatDialog.show();
		ret = mIat.startListening(recognizerListener);
		if (ret != ErrorCode.SUCCESS) {
			Log.d(TAG, ""+ret);
			showTip("��дʧ��,�����룺" + ret);
		}
		
    }
    
    //����ʶ����������
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results,isLast);//�õ�ʶ���� 
		}

		/**
		 * ʶ��ص�����.
		 */
		public void onError(SpeechError error) {
			speak(error.getPlainDescription(true),true);
			info.makeText(getApplicationContext(), "error.getPlainDescription(true)", 1000).show();
			//showTip(error.getPlainDescription(true));
		}

	};
	
    
    
   /* //��ʼ����ʶ��
    private void startSemanticAnalysis(){
    	//semanticAnalysis=new SemanticAnalysis();
    	//SAResult=semanticAnalysis.getSAResult("����������");//��ʼ�������
    	//UnderstanderDemo testSA=new UnderstanderDemo();
    	//SAResult=testSA.startSA("����������");
    	
    	
    	
    	Intent SAActivity = new Intent(MainActivity.this,SemanticAnalysis.class);
    	SAActivity.putExtra("SRResult", SRResult);
    	Log.d("dd","ʶ������"+SRResult);
    	startActivityForResult(SAActivity,0 );
    
    	//onActivityResult(0, 0, SAActivity);
    	
    	Intent SAActivity = new Intent(MainActivity.this,SemanticAnalysis.class);
    	startActivity(SAActivity);
    	
    	SemanticAnalysis semanticAnalysis = new SemanticAnalysis();
    	SAResult=semanticAnalysis.SAResult;
    	speak(SAResult, false);
    	
    	
    	// SRResult=MainActivity.SRResult;
 	
 		//Log.d("dd","SRResult:"+SRResult);
 		ret=0 ;
 		
 		mTextUnderstander = TextUnderstander.createTextUnderstander(MainActivity.this, textUnderstanderListener);
 		
 		startAnalysis();
    }*/
    
  //��ʼ�������
  	private void startAnalysis(){
  		
  		mTextUnderstander.setParameter(SpeechConstant.DOMAIN,  "iat");
  		if(mTextUnderstander.isUnderstanding()){
  			mTextUnderstander.cancel();
  			//showTip("ȡ��");
  			Log.d("dd","ȡ��");
  		}else {
  			//SRResult="�鿴���ݿ⡣";
  			ret = mTextUnderstander.understandText(SRResult, textListener);
  			if(ret != 0)
  			{
  				//showTip("�������ʧ��,������:"+ ret);
  				Log.d("dd","�������ʧ��,������:"+ ret);
  			}
  		}
  		/*ret = mTextUnderstander.understandText(SRResult, textListener);
  		if(ret != 0)
  		{
  			showTip("�������ʧ��,������:"+ ret);
  			
  		}*/
  	}
  	 //ʶ��ص�
      private TextUnderstanderListener textListener = new TextUnderstanderListener() {
  		
  		public void onResult(final UnderstanderResult result) {
  	       	runOnUiThread(new Runnable() {
  					
  					public void run() {
  						if (null != result) {
  			            	// ��ʾ
  							//Log.d(TAG, "understander result��" + result.getResultString());
  							String text = result.getResultString();
  							SAResult=text;
  							Log.d("dd","SAResult:"+SAResult);
  							/*CreateTXT createTXT=new CreateTXT();
  							File SAResultTXT = new File("SAResultTXT.txt");
  							try {
								createTXT.createFile(SAResultTXT);
								createTXT.writeTxtFile(SAResult, SAResultTXT);
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
  							
  							if (TextUtils.isEmpty(text)) {
  								//Log.d("dd", "understander result:null");
  								//showTip("ʶ��������ȷ��");
  							}
  							//mainActivity.speak();
  							//speak(SAResult,false);
  							getJsonData();
  							//finish();
  			            } 
  					}

					

					/*private void dialogueManagement(int mainServiceID,int branchServiceID) {//�Ի�������
						// TODO Auto-generated method stub
						if(mainServiceID==1){
							if(branchServiceID==1){//�����˴�绰���񣬱�Ҫ�����ǡ��绰���롿,��ѡ�����С���������ء�����Ӫ�̡����ŶΡ���β�š���
								//���ɶ����ѡ����ȷ����Ҫ����
								
							}
							if(branchServiceID==2){//�����˲鿴�绰���ż�¼
								
							}
							
						}
						if(mainServiceID==2){//�����˷����ŷ��񣬱�Ҫ�����ǵ绰����Ͷ�������
							
						}
					}*/
  				});
  		}
  		
  		public void onError(SpeechError error) {
  			//showTip("onError Code��"	+ error.getErrorCode());
  			Log.d("dd","onError Code��"	+ error.getErrorCode());
  		}
  	};
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){//��дonActivityResult
        if(requestCode == 0){
            //System.out.println("REQUESTCODE equal");
            if(resultCode == 0){
                 //    System.out.println("RESULTCODE equal");
            	SAResult = data.getStringExtra("SRResult");
            }
        }
    }
    
   
    
    private void printResult(RecognizerResult results,boolean isLast) {
		String text = JsonParser.parseIatResult(results.getResultString());

		//Log.d("dd","text:"+text);
		String sn = null;
		// ��ȡjson����е�sn�ֶ�
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			Log.d("dd","json:"+results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}
		SRResult=resultBuffer.toString();//���������SRResult
		if(isLast==true){
			speak(SRResult, true);
			if(SRResult.equals("�鿴���ݿ⡣")){//����ָ��鿴���ݿ⣬�����з������
				QueryDB queryDB=new QueryDB(this);
				queryDB.start();
			}
			else{
				startAnalysis();//ת��������
			}
			//�������ݿ�
			//AddLike addLike=new AddLike(SRResult,this);
			//addLike.startInsert();
			//�鿴���ݿ�
			//addLike.startQuery();
			
			
			/*startSemanticAnalysis();*/
		}
	}
    
    int ret = 0; // �������÷���ֵ
    
	@SuppressWarnings("static-access")
	@Override
	//����ʶ�����
	public void onClick(View view) {
		//���ʱȡ�������ϳ�
		mTts.stopSpeaking();
		//QueryDB queryDB=new QueryDB(this);
		//queryDB.start();
		startSpeenchRecognition();
		
		//GetLocation getLocation =new GetLocation(this);
		//getLocation.start();
		/*Thread mThreadTest= new Thread(){
			public void run(){
				try {
					final NewsService test=new NewsService();
					//speak("hi",false);
					Log.d("dd","ok"+test.start());
				} catch (Exception e) {
					Log.d("dd",e.toString());
					//speak(e.toString(),false);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			};
			mThreadTest.start();*/
		//startSemanticAnalysis();
		
		
		
		//���´��벻��ֱ�ӷ��������Ȼ����������
		/*info.makeText(getApplicationContext(), "5", 1000).show();
		// TODO Auto-generated method stub
		if(view.getId()==R.id.voice_input){
			//3.��ʼ��д
			
			setParam();
			info.makeText(getApplicationContext(), "��ʼ��д", 1000).show();
				// ����ʾ��д�Ի���
				ret = mIat.startListening(recognizerListener);
				if (ret != ErrorCode.SUCCESS) {
					Log.d(TAG, ""+ret);
					//showTip("��дʧ��,�����룺" + ret);
				} else {
					//showTip("�ɹ�");
				}
			}*/

	}

	public void setParam(){
		// ��ղ���
				mIat.setParameter(SpeechConstant.PARAMS, null);

				// ������д����
				mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
				// ���÷��ؽ����ʽ
				mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

				String lag = mSharedPreferences.getString("iat_language_preference",
						"mandarin");
				if (lag.equals("en_us")) {
					// ��������
					mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
				} else {
					// ��������
					mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
					// ������������
					mIat.setParameter(SpeechConstant.ACCENT, lag);
				}
				// ��������ǰ�˵�
				mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
				// ����������˵�
				mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
				// ���ñ�����
				mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
				// ������Ƶ����·��
				mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
				// ������д����Ƿ�����̬������Ϊ��1��������д�����ж�̬�����ط��ؽ��������ֻ����д����֮�󷵻����ս��
				// ע���ò�����ʱֻ��������д��Ч
				mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {//����Ի���
		
		// TODO Auto-generated method stub
		//if (!isChatMode) {
			//SiriListItem item = list.get(pos);
			//
			/*if (item.isSiri) {
				new CustomDialog(MainActivity.this,
						CustomDialog.DIALOG_DETAILS,"��ϸ",item.message).show();
			}*/
	//	}
	}
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {//�����Ի���
		//speak("hai",false);
		super.onCreateContextMenu(menu, v, menuInfo);
        //��ȡ�������ֵ
		int pos=((AdapterView.AdapterContextMenuInfo) menuInfo).position;
		SiriListItem item = list.get(pos);
		selectedMessage=item.message;//��ȡ������������ 
		//speak(item.message,false);
		menu.add(0, LIKE, 0, "����ش����");
		menu.add(0, DISLIKE, 0, "����ش��ˮ");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case LIKE:
			//���ϲ�������ݿ�
			//switchChatMode(true);
			AddLike addLike = new AddLike(selectedMessage,1,this);
			addLike.start();
			showTip("��ӵ���ϲ���Ļش����");
			break;
		case DISLIKE:
			//��Ӳ�ϲ�������ݿ�
			//switchChatMode(false);
			AddLike addDislike = new AddLike(selectedMessage,2,this);
			addDislike.start();
			showTip("��ӵ��㲻ϲ���Ļش����");
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
	
	/**
	 * ��ʼ��������
	 */
	private InitListener mTtsInitListener = new InitListener() {
		
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		showTip("��ʼ��ʧ��,�����룺"+code);
        	} else {
				// ��ʼ���ɹ���֮����Ե���startSpeaking����
        		// ע���еĿ�������onCreate�����д�����ϳɶ���֮�����Ͼ͵���startSpeaking���кϳɣ�
        		// ��ȷ�������ǽ�onCreate�е�startSpeaking������������
			}		
		}
	};
	
	/**
	 * ��������
	 * @param
	 * @return 
	 */
	private void setParamTTS(){
		// ��ղ���
		mTts.setParameter(SpeechConstant.PARAMS, null);
		//���úϳ�
		if(mEngineTypeTTS.equals(SpeechConstant.TYPE_CLOUD)) {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			//���÷�����
			mTts.setParameter(SpeechConstant.VOICE_NAME,voicer);
			//��������
			//mTts.setParameter(SpeechConstant.SPEED,mSharedPreferencesTTS.getString("speed_preference", "50"));
			//��������
			//mTts.setParameter(SpeechConstant.PITCH,mSharedPreferencesTTS.getString("pitch_preference", "50"));
			//��������
			//mTts.setParameter(SpeechConstant.VOLUME,mSharedPreferencesTTS.getString("volume_preference", "50"));
			//���ò�������Ƶ������
			//mTts.setParameter(SpeechConstant.STREAM_TYPE,mSharedPreferencesTTS.getString("stream_preference", "3"));
		}else {
			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
			//���÷����� voicerΪ��Ĭ��ͨ������+����ָ�������ˡ�
			mTts.setParameter(SpeechConstant.VOICE_NAME,"");
		}
	}
	
	/**
	 * �ϳɻص�������
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		
		public void onSpeakBegin() {
			showTip("��ʼ����");
		}

		
		public void onSpeakPaused() {
			showTip("��ͣ����");
		}

		
		public void onSpeakResumed() {
			showTip("��������");
		}

		
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
			// �ϳɽ���
			//mPercentForBuffering = percent;
			//showTip(String.format(getString(R.string.tts_toast_format),
				//	mPercentForBuffering, mPercentForPlaying));
		}

		
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			// ���Ž���
			//mPercentForPlaying = percent;
			//showTip(String.format(getString(R.string.tts_toast_format),
				//	mPercentForBuffering, mPercentForPlaying));
		}

		
		public void onCompleted(SpeechError error) {
			if (error == null) {
				showTip("�������");
			} else if (error != null) {
				showTip(error.getPlainDescription(true));
			}
		}

		
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			
		}
	};
	
	private void textToSpeach(String text){//�����ϳ�
		
		// ���ò���
		setParamTTS();
		int code = mTts.startSpeaking(text, mTtsListener);
		if (code != ErrorCode.SUCCESS) {
				showTip("�����ϳ�ʧ��,������: " + code);	
		}
	}
	
	
    //from SiriCN
	public void speak(String msg, boolean isSiri) {//
		//info.makeText(getApplicationContext(), "here", 1000).show();
		addToList(msg, isSiri);//��ӵ��Ի��б�
		if(isSiri==false){
		textToSpeach(msg);
			}
		//if(isHasTTS)
		//mSiriEngine.SiriSpeak(msg);
	}
	
	private void addToList(String msg, boolean isSiri) {
		//
		list.add(new SiriListItem(msg, isSiri));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(list.size() - 1);
	}
    
	public class SiriListItem {
		String message;
		boolean isSiri;

		public SiriListItem(String msg, boolean siri) {
			message = msg;
			isSiri = siri;
		}
	}
	
	private void showTip(final String str) {
		info.setText(str);
		info.show();
	}


	//ͼ������˲���
	private static final String URL = "http://www.tuling123.com/openapi/api";
	//"����������apikey"
	private static final String API_KEY = "2e4a7223fcaf415cad9c507adaf61d57";
	public void tuling(String msg)
	{
		String jsonRes = doGet(msg);
		String text =null;
		Gson gson = new Gson();
		Result result = null;
		try
		{
			result = gson.fromJson(jsonRes, Result.class);
			text=result.getText();
		} catch (Exception e)
		{
			speak("��������æ�����Ժ�����",false);
		}
		speak(text,false);
	}

	public static String doGet(String msg)
	{
		String result = "";
		String url = setParams(msg);
		ByteArrayOutputStream baos = null;
		InputStream is = null;
		try
		{
			java.net.URL urlNet = new java.net.URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlNet
					.openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			is = conn.getInputStream();
			int len = -1;
			byte[] buf = new byte[128];
			baos = new ByteArrayOutputStream();

			while ((len = is.read(buf)) != -1)
			{
				baos.write(buf, 0, len);
			}
			baos.flush();
			result = new String(baos.toByteArray());
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (baos != null)
					baos.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			try
			{
				if (is != null)
				{
					is.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	private static String setParams(String msg)
	{
		String url = "";
		try
		{
			url = URL + "?key=" + API_KEY + "&info="
					+ URLEncoder.encode(msg, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return url;
	}

}
