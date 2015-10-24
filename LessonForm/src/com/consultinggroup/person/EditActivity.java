package com.consultinggroup.person;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.consultinggroup.classtime.R;
import com.consultinggroup.classtime.R.layout;
import com.consultinggroup.classtime.R.menu;
import com.consultinggroup.login.MyDialog;
import com.consultinggroup.login.SelectData;
import com.consultinggroup.tools.CircleImageView;
import com.consultinggroup.tools.IP;
import com.consultinggroup.tools.MyhttpClient;
import com.consultinggroup.tools.Tools;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends Activity implements OnClickListener {
	private ImageView back, confirmData;
	private TextView identify;
	private EditText nickName, schoolName, institute, gender, like, email;
	private CircleImageView face;
	private static final int success_saveInfo = 3;
	private String[] items = new String[] { "ѡ�񱾵�ͼƬ", "����" };
	private static final int IMAGE_REQUEST_CODE = 0;
	/* ͷ������ */
	private static final String IMAGE_FILE_NAME = "faceimage.jpg";
	private String idName;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private Dialog dialog;
	private File file;
	private static final int success_upload = 4;
	private static final int fail_upload = 5;
	String facepath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/����ʱ��/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		initView();
		initData();
	}

	private void initView() {
		identify=(TextView) findViewById(R.id.identify);
		nickName = (EditText) findViewById(R.id.nickName);
		back = (ImageView) findViewById(R.id.back);
		confirmData = (ImageView) findViewById(R.id.confirmData);
		schoolName = (EditText) findViewById(R.id.school);
		institute = (EditText) findViewById(R.id.institute);
		gender = (EditText) findViewById(R.id.gender);
		like = (EditText) findViewById(R.id.like);
		email = (EditText) findViewById(R.id.email);
		face = (CircleImageView) findViewById(R.id.face);
		face.setOnClickListener(this);
		back.setOnClickListener(this);
		confirmData.setOnClickListener(this);
	}

	private void initData() {
		confirmData.setOnClickListener(this);
		back.setOnClickListener(this);
		Intent intent = getIntent();
		schoolName.setText(intent.getStringExtra("schoolName"));
		nickName.setText(intent.getStringExtra("nickName"));
		institute.setText(intent.getStringExtra("institute"));
		gender.setText(intent.getStringExtra("gender"));
		like.setText(intent.getStringExtra("like"));
		email.setText(intent.getStringExtra("email"));
		idName = getSharedPreferences("studentInfo", 0).getString("id", "");
		identify.setText("�û�id:"+idName);
		File file=new File(facepath + "/" + idName
				+ ".jpg");
		if(file.exists()){
		face.setImageBitmap(BitmapFactory.decodeFile(facepath + "/" + idName
				+ ".jpg"));
		}else {
			face.setImageResource(R.drawable.face);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == success_saveInfo) {
				Editor sp = getSharedPreferences("studentInfo", 0).edit();
				sp.putString("schoolName", schoolName.getText().toString());
				sp.putString("nickName", nickName.getText().toString());
				sp.putString("institute", institute.getText().toString());
				sp.putString("gender", gender.getText().toString());
				sp.putString("like", like.getText().toString());
				sp.putString("email", email.getText().toString());
				sp.commit();
				EditActivity.this.dialog.dismiss();
				Toast.makeText(EditActivity.this, "�޸ĳɹ�", 0).show();
				finish();
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_right_out);
			} else if (msg.what == success_upload) {
				dialog.dismiss();
				Toast.makeText(EditActivity.this, "ͷ����ĳɹ�", 0).show();
			} else if (msg.what == fail_upload) {
				dialog.dismiss();
			}
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirmData:
			dialog = new MyDialog(this).loadDialog1();
			new SaveChangeData().start();
			break;
		case R.id.back:
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_right_out);
			break;
		case R.id.face:
			showDialog();
			break;
		default:
			break;
		}
	}

	class SaveChangeData extends Thread {

		@Override
		public void run() {
			String urlStr = "http://"+IP.ip+"/LessonForm/SaveChangeData";
			HttpPost request = new HttpPost(urlStr);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id",idName));
			params.add(new BasicNameValuePair("schoolName", schoolName
					.getText().toString()));
			params.add(new BasicNameValuePair("nickName", nickName.getText()
					.toString()));
			params.add(new BasicNameValuePair("institute", institute.getText()
					.toString()));
			params.add(new BasicNameValuePair("gender", gender.getText()
					.toString()));
			params.add(new BasicNameValuePair("like", like.getText().toString()));
			params.add(new BasicNameValuePair("email", email.getText()
					.toString()));
			try {
				// �������������
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				HttpClient client = new MyhttpClient().getHttpClient();
				// ִ�����󷵻���Ӧ
				HttpResponse response = client.execute(request);
				if (response.getStatusLine().getStatusCode() == 200) {
					handler.sendEmptyMessage(success_saveInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
				.setTitle("����ͷ��")
				.setItems(items, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // �����ļ�����
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// �жϴ洢���Ƿ�����ã����ý��д洢
							if (Tools.hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory()
									+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(EditActivity.this, "δ�ҵ��洢�����޷��洢��Ƭ��",
							Toast.LENGTH_SHORT).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
					uploadPicture(facepath + idName + ".jpg");
				}
				Bitmap bitmap = data.getExtras().getParcelable("data");
				saveBitmapFile(bitmap);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void uploadPicture(final String filepath) {
		dialog = new MyDialog(this).loadDialog2();
		new Thread(new Runnable() {

			public void run() {
				Looper.prepare();
				uploadFile(filepath);
				Looper.loop();
			}
		}).start();

	}

	private void uploadFile(String filePath) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			String newName = idName + ".jpg";
			String actionUrl = "http://"+IP.ip+"/LessonForm/uploadPic";
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* ����Input��Output����ʹ��Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* ���ô��͵�method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* ����DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);
			/* ȡ���ļ���FileInputStream */
			FileInputStream fStream = new FileInputStream(filePath);
			/* ����ÿ��д��1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* ���ļ���ȡ������������ */
			while ((length = fStream.read(buffer)) != -1) {
				/* ������д��DataOutputStream�� */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			fStream.close();
			ds.flush();
			/* ȡ��Response���� */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* ��Response��ʾ��Dialog */
			// showDialog("�ϴ��ɹ�" + b.toString().trim());
			if (b.toString().equals("success")) {
				handler.sendEmptyMessage(success_upload);
			} else if (b.toString().equals("fail")) {
				Toast.makeText(getApplicationContext(), "����ͷ��ʧ��", 0).show();
			}
			/* �ر�DataOutputStream */
			ds.close();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "����ͷ��ʧ��", 0).show();
			handler.sendEmptyMessage(fail_upload);
		}
	}

	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			face.setImageDrawable(drawable);

		}
	}

	// Bitmap���󱣴�ζͼƬ�ļ�
	public void saveBitmapFile(Bitmap bitmap) {
		File file = new File("/sdcard/����ʱ��");
		if (!file.exists()) {
			file.mkdir();
		}
		file = new File(file, idName + ".jpg");// ��Ҫ����ͼƬ��·��
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ü�ͼƬ����ʵ��
	 * 
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// ���òü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}
}
