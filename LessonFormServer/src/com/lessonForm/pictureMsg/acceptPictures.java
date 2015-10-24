package com.lessonForm.pictureMsg;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.DiskFileUpload;
import org.apache.tomcat.util.http.fileupload.FileItem;

import com.lessonForm.dao.DaoDealer;


public class acceptPictures extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String id;
		File fNew;
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		String loadpath = request.getSession().getServletContext()
				.getRealPath("/")
				+ "Pictures"; // �ϴ��ļ����Ŀ¼
		DiskFileUpload fu = new DiskFileUpload();
		fu.setSizeMax(1 * 1024 * 1024); // ���������û��ϴ��ļ���С,��λ:�ֽ�
		fu.setSizeThreshold(4096); // �������ֻ�������ڴ��д洢������,��λ:�ֽ�
		// ��ʼ��ȡ�ϴ���Ϣ
		int index = 0;
		List fileItems = null;

		try {
			fileItems = fu.parseRequest(request);
			System.out.println("fileItems=" + fileItems);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Iterator iter = fileItems.iterator(); // ���δ���ÿ���ϴ����ļ�
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();// �������������ļ�������б���Ϣ
			if (!item.isFormField()) {
				String name = item.getName();// ��ȡ�ϴ��ļ���,����·��
				System.out.println(name);
				name = name.substring(name.lastIndexOf("\\") + 1);// ��ȫ·������ȡ�ļ���
				long size = item.getSize();
				if ((name == null || name.equals("")) && size == 0)
					continue;
				int point = name.indexOf(".");
				id = name.substring(0, point);
				System.out.println(id);
				name = (new Date().getTime() + name.substring(point, name.length()));
				index++;
				fNew = new File(loadpath, name);
				try {
					item.write(fNew);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// ����ͼƬ·�������ݿ���
				if (new DaoDealer().savePicturesPath(id,name)) {
					//��ȡname�����ͻ���
					out.print(name);
				} else {
					out.print("fail");
				}
		}
		out.flush();
		out.close();
	}

	}

}