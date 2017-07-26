package com.forler.obd_ble.utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Byte2HexUtil {

	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param String
	 *            str 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 十六进制转换字符串
	 * 
	 * @param String
	 *            str Byte字符串(Byte之间无分隔符 如:[616C6B])
	 * @return String 对应的字符串
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * bytes转换成十六进制字符串
	 * 
	 * @param byte[] b byte数组
	 * @return String 每个Byte值之间空格分隔
	 */
	public static String byte2HexStr(byte[] b) {
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
			sb.append(" ");
		}
		return sb.toString().toUpperCase().trim();
	}
	
	/**
	 * 字节转十六进制为相应的字符串显示
	 * 
	 * @param data
	 * @return
	 */
	public static String byte2Hex(byte data[]) {
		if (data != null && data.length > 0) {
			StringBuilder sb = new StringBuilder(data.length);
			for (byte tmp : data) {
				sb.append(String.format("%02X ", tmp));
			}
			return sb.toString();
		}
		return "no data";
	}

	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param String
	 *            src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
		}
		return ret;
	}
	

	/**
	 * String的字符串转换成unicode的String
	 * 
	 * @param String
	 *            strText 全角字符串
	 * @return String 每个unicode之间无分隔符
	 * @throws Exception
	 */
	public static String strToUnicode(String strText) throws Exception {
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u" + strHex);
			else
				// 低位在前面补00
				str.append("\\u00" + strHex);
		}
		return str.toString();
	}

	/**
	 * unicode的String转换成String的字符串
	 * 
	 * @param String
	 *            hex 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 */
	public static String unicodeToString(String hex) {
		int t = hex.length() / 6;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++) {
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 高位需要补上00再转
			String s1 = s.substring(2, 4) + "00";
			// 低位直接转
			String s2 = s.substring(4);
			// 将16进制的string转为int
			int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
			// 将int转换为字符
			char[] chars = Character.toChars(n);
			str.append(new String(chars));
		}
		return str.toString();
	}
	

	/**
	 * 二维数组转换为一维数组
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] twoArray2OneArray(byte[][] data) {
		if (data != null) {
			int dLength = data.length;
			// 获取总元素个数
			int l = 0;
			for (byte[] b : data) {
				l += b.length;
			}
			// new一个一维数组，length为总元素个数
			byte[] k = new byte[l];
			// 已复制的个数
			int g = 0;
			// 复制二维数组每一个子数组到新的一维数组
			for (byte[] b : data) {
				System.arraycopy(b, 0, k, g, b.length);
				g += b.length;
			}
			return k;
		}
		return null;
	}

	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 时间戳转化为Sting
	 * 
	 * @param time
	 * @return
	 */
	public static String longTimeToStr(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = format.format(time);
		return d;
	}

	/**
	 * 时间戳转化为Date
	 * 
	 * @param time
	 * @return
	 */
	public static Date dateToStr(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = format.format(time);
		Date date = null;
		try {
			date = format.parse(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 时间戳转化为年月日时分秒数组
	 * 
	 * @param time
	 * @return
	 */
	public static int[] longTimeToByteArray(long time) {
		String d = longTimeToStr(time);
		String spStr[] = d.split(" ", -1);
		String[] t1 = spStr[0].split("-", -1);
		String[] t2 = spStr[1].split(":", -1);
		int[] times = new int[6];
		times[0] = Integer.parseInt(t1[0].split("0")[1]);
		times[1] = Integer.parseInt(t1[1]);
		times[2] = Integer.parseInt(t1[2]);
		times[3] = Integer.parseInt(t2[0]);
		times[4] = Integer.parseInt(t2[1]);
		times[5] = Integer.parseInt(t2[2]);
		return times;
	}

	/**
	 * 保留n位小数
	 * 
	 * @param d
	 *            数据
	 * @param n
	 *            保留位数
	 * @return
	 */
	public static String retainNPositionDecimals(double data, int num) {
		BigDecimal bd = new BigDecimal(String.valueOf(data));
		bd = bd.setScale(num, BigDecimal.ROUND_HALF_UP);
		return bd.toString();

	}
	
	/**
	  * 将int类型的数据转换为byte数组
	  * 原理：将int数据中的四个byte取出，分别存储
	  * @param n int数据
	  * @return 生成的byte数组（低位在前）
	  */
	 public static byte[] intToBytes(int n){
	  byte[] b = new byte[4];
	  for(int i = 0;i < 4;i++){
		  b[i] = (byte)(n >> (i * 8)); 
	  }
	  return b;
	 }
	 
	 /**
	  * 将4个byte的数组的数据转换为int类型
	  * @param res	byte数组（低位在前）
	  * @return	int数据
	  */
	 public static int bytes2int(byte[] res) {   
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000   
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);   
		return targets;   
		}   
	 
	 /**
	  * 将2个byte的数组的数据转换为int类型
	  * @param res	byte数组（低位在前）
	  * @return	int数据
	  */
	 public static int twoBytes2int(byte[] res) {   
		 // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000   
		 int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00);   
		 return targets;   
	 }   
	 
	 /**
	  * 将3个byte的数组的数据转换为int类型
	  * @param res	byte数组（高位在前）
	  * @return	int数据
	  */
	 public static int threeBytes2int(byte[] res) {   
		 // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000   
		 int targets = (res[2] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[0] << 24) >>> 8);   
		 return targets;   
	 }   
	 
	/**
	 * int转8位二进制字符串
	 * @param data
	 * @return
	 */
	public static String intToBinaryString(int data) {
		String binaryStr = Integer.toBinaryString(data);// 整数转二进制字符串
		String w = "";
		if (binaryStr.length() < 8) {
			for (int i = 0; i < 8 - binaryStr.length(); i++) {
				w = w + "0";
			}
		} else {
			binaryStr = binaryStr.substring(binaryStr.length()-8, binaryStr.length());
		}
		binaryStr = w + binaryStr;
		return binaryStr;
	}
	
	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {  
        String dest = "";  
        if (str!=null) {  
        	Pattern p = Pattern.compile("\\s*|\t|\r|\n");  
        	Matcher m = p.matcher(str);  
        	dest = m.replaceAll("");  
        }  
        return dest;  
	}  
	
	
	/**
	 * 当int time<10时，显示0X
	 * @param time
	 * @return
	 */
	public static String showStringTime(int time){
		if(time < 10){
			return "0"+time;
		}
		return String.valueOf(time);
	}
	
	
}
