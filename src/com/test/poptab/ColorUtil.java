package com.test.poptab;

import android.graphics.Color;

/**
 * ��ɫ������
 * @author zhangshuo
 */
public class ColorUtil {
	
	/**
	 * �����startColor���ȵ�endColor�����аٷֱ�Ϊfranchʱ����ɫֵ
	 * @param startColor ��ʼ��ɫ int����
	 * @param endColor ������ɫ int����
	 * @param franch franch �ٷֱ�0.5
	 * @return ����int��ʽ��color
	 */
	public static int caculateColor(int startColor, int endColor, float franch){
		String strStartColor = "#" + Integer.toHexString(startColor);
		String strEndColor = "#" + Integer.toHexString(endColor);
		return Color.parseColor(caculateColor(strStartColor, strEndColor, franch));
	}
	
	/**
	 * �����startColor���ȵ�endColor�����аٷֱ�Ϊfranchʱ����ɫֵ
	 * @param startColor ��ʼ��ɫ ����ʽ#FFFFFFFF��
	 * @param endColor ������ɫ ����ʽ#FFFFFFFF��
	 * @param franch �ٷֱ�0.5
	 * @return ����String��ʽ��color����ʽ#FFFFFFFF��
	 */
	public static String caculateColor(String startColor, String endColor, float franch){
		
		int startAlpha = Integer.parseInt(startColor.substring(1, 3), 16);
		int startRed = Integer.parseInt(startColor.substring(3, 5), 16);
		int startGreen = Integer.parseInt(startColor.substring(5, 7), 16);
		int startBlue = Integer.parseInt(startColor.substring(7), 16);
		
		int endAlpha = Integer.parseInt(endColor.substring(1, 3), 16);
		int endRed = Integer.parseInt(endColor.substring(3, 5), 16);
		int endGreen = Integer.parseInt(endColor.substring(5, 7), 16);
		int endBlue = Integer.parseInt(endColor.substring(7), 16);
		
		int currentAlpha = (int) ((endAlpha - startAlpha) * franch + startAlpha);
		int currentRed = (int) ((endRed - startRed) * franch + startRed);
		int currentGreen = (int) ((endGreen - startGreen) * franch + startGreen);
		int currentBlue = (int) ((endBlue - startBlue) * franch + startBlue);
		
		return "#" + getHexString(currentAlpha) + getHexString(currentRed)
				+ getHexString(currentGreen) + getHexString(currentBlue);
		
	}
	
	/** 
     * ��10������ɫֵת����16���ơ� 
     */  
	public static String getHexString(int value) {  
        String hexString = Integer.toHexString(value);  
        if (hexString.length() == 1) {  
            hexString = "0" + hexString;  
        }  
        return hexString;  
    }  
}
