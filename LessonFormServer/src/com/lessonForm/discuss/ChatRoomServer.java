package com.lessonForm.discuss;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.lessonForm.dao.DaoDealer;

/**
 * ������ Server
 *
 */
public class ChatRoomServer {
	private ServerSocket ss;
	private int port; //�˿�
	private List<WorkThread> list;  //����ͻ�����Ϣ�������߳�
	public ChatRoomServer(int port) {
		this.port = port;
		list = new ArrayList<WorkThread>();
	}
	
	//����������
	public void startServer() {
		new Thread(){
			public void run() {
				try {
					ss = new ServerSocket(port);  //���������s
					while(true) {
						Socket s = ss.accept();  //��ͣ�Ľ��տͻ�������
						WorkThread wt = new WorkThread(s); //����һ���̴߳���ͻ�����Ϣ
						wt.start();  //�������߳�
						list.add(wt);  //��ӵ� list 
					}
				} catch (Exception e) {
					System.out.println("�������Ͽ�����..");
				}
			};
		}.start();
	}
	
	//����ͻ�����Ϣ���߳�
	class WorkThread extends Thread {
		private Socket s;  //���ӽ����Ŀͻ���
		private String name;  //�ͻ�������
		private BufferedReader in;  //������,��������
		private PrintWriter out;  //�����,��������s
		public WorkThread(Socket s) throws Exception{
			this.s = s;
			in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
		}
		
		
		//������Ϣ
		public void sendMsg(String msg) {
			out.println(msg);
			out.flush();
		}
		
		//���͹㲥,�����ߵ�ÿ���ͻ��˷�����Ϣ
		public void sendToAll(String msg) {
			if(list != null) {
				//���� list,����ÿ�� WorkThread ����� sendMsg ����
				for(WorkThread wt: list) {
					wt.sendMsg(msg);
				}
			}
		}
		
//		//���͹㲥,��ǰ��������
//		public  void sendOnlineCount(String msg,int onlineCount) {
//			sendToAll(msg +"&" +onlineCount);
//		}
		
		//run() ������Ҫ���ڲ��Ͻ��տͻ��˷��͹�������Ϣ,�ڷַ���ȥ
		@Override
		public void run() {
			try {
				//���Ȼ�ȡ���ӽ����Ŀͻ��˵�����
				name = in.readLine();
				System.out.println(name);
				//���͹㲥, xxx ����������
				sendToAll(name);
//				String temp=name.substring(0, name.indexOf("?"));
				//�㲥��ǰ��������
//				sendOnlineCount(temp,list.size());
				
				//Ȼ�󲻶ϵļ����ͻ��˷��͹�������Ϣ
				String msg;  //���ڱ���ÿ����Ϣ����
				while((msg = in.readLine()) != null) {
					sendToAll(msg);
					String id=msg.substring(0, msg.indexOf("/"));
					String message=msg.substring(msg.indexOf("/")+1, msg.indexOf("*"));
					String nickName=msg.substring(msg.indexOf("*")+1, msg.indexOf("?"));
					String time=msg.substring(msg.indexOf("?")+1,msg.indexOf("&"));
					String lessonName=msg.substring(msg.indexOf("&")+1);
					System.out.println(lessonName+"llll");
					new DaoDealer().saveMsg(lessonName,id,nickName,message,time);
					System.out.println(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//��������쳣���� �յ�����ϢΪ null,��Ϊ�����쳣,����ǰ�ͻ��˵� WorkThread �Ƴ� list
			list.remove(this);
		}
	}
}
