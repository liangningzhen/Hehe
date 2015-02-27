package com.hehe;

import java.io.IOException;
import java.util.ArrayList;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.banner.AdViewListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.internal.bt;
import com.hehe.bean.JokeBean;
import com.hehe.utils.Constants;
import com.hehe.utils.StringUtils;

public class MainActivity extends ActionBarActivity {

	private static final String TAG = "MainActivity";

	private com.google.android.gms.ads.AdView adView;

	TextView mJoke;
	private Button getJoke;
	private Button copyBtn;
	private Toolbar mToolbar;

	private Handler handler = new Handler();

	private ArrayList<JokeBean> jokes = new ArrayList<JokeBean>();
	private int i;
	private int page = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		mToolbar.setTitle(R.string.app_name);
		getSupportActionBar().setTitle(R.string.app_name);

		// 初始化admob
		adView = new com.google.android.gms.ads.AdView(this);
		adView.setAdUnitId(Constants.ADMOB_ID);
		adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);

		LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.bannerlayout);
		bannerLayout.addView(adView);
		// 启动一般性请求。
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		// 初始化有米广告接口
		// 参数：appId, appSecret, 调试模式
		AdManager.getInstance(this).init(Constants.YOUMI_ID,
				Constants.YOUMI_KEY, false);

		/** 显示广告 **/
		showBanner();

		new Thread() {
			@Override
			public void run() {
				super.run();
				initData(Constants.URL);
			}
		}.start();

		getJoke = (Button) findViewById(R.id.button1);
		mJoke = (TextView) findViewById(R.id.textView1);
		copyBtn = (Button) findViewById(R.id.button2);
		
		copyBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipboardManager c= (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
				c.setText(mJoke.getText());
				toast("已将内容复制到剪切板");
			}
		});
		

		getJoke.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (jokes.size() > 0 & i < jokes.size()) {
					mJoke.setText(jokes.get(i).content);
					i++;
					if (i == (jokes.size() - 3)) {
						final String newURL = StringUtils.AddURL(page);
						new Thread() {
							public void run() {
								initData(newURL);
								page++;
							};
						}.start();
					}
				} else {
					toast("暂时没有笑话");
				}
			}
		});

	}

	private void initData(String url) {
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Element content = doc.getElementById("main");
			Elements divs = content.getElementsByTag("div");
			for (Element div : divs) {
				if (div.id().equals("endtext")) {
					JokeBean jokeBean = new JokeBean();
					jokeBean.content = div.text();
					jokes.add(jokeBean);
				}
				Log.e(TAG, "" + jokes.size());
			}
		} catch (IOException e) {
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showBanner() {

		// 实例化LayoutParams(重要)
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		// 设置广告条的悬浮位置
		layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为右下角
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 调用Activity的addContentView函数

		// 监听广告条接口
		adView.setAdListener(new AdViewListener() {

			@Override
			public void onSwitchedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "广告条切换");
			}

			@Override
			public void onReceivedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "请求广告成功");
			}

			@Override
			public void onFailedToReceivedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "请求广告失败");
			}
		});
		this.addContentView(adView, layoutParams);
	}

	public void toast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		adView.resume();
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

}
