package com.lessonForm.studentInfo;

import java.util.Random;

public class GenerateID {
	
	   //���Ӻ�
      public static String getFormID(){
    	  Random random=new Random();
    	  String id=random.nextInt(10000000)+"";
    	  return id;
      }
}
