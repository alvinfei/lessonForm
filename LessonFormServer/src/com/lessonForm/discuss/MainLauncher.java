package com.lessonForm.discuss;
/**
 * ���������� ChatRoomServer
 *
 */
public class MainLauncher {
	private static final int PORT = 33333; //�˿ں�
	public static void main(String[] args) {
		ChatRoomServer server = new ChatRoomServer(PORT);  //����˿�: 4444
		server.startServer();  //����������
		
		System.out.println("�����ҷ����������ɹ�!!");
	}
}
