package NJUPTDataServer.Utile;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NumberUtile {
	private static String hexStr = "0123456789ABCDEF";
	private final static char[] HEX = "0123456789abcdef".toCharArray();

	public NumberUtile() {
		// TODO Auto-generated constructor stub
	}

	public static int byteArrayToInt(byte[] str) {
		return 1;
	}

	public static String bytes2HexString(byte b) {
		String hex = Integer.toHexString(b & 0xFF);
		if (hex.length() == 1) {
			hex = '0' + hex;
		}
		return hex.toUpperCase();
	}
	public static int bytesToInt(byte[] bytes) {  
	    int number = bytes[0] & 0xFF;  
	    // "|="��λ��ֵ��  
	    number |= ((bytes[1] << 8) & 0xFF00);  
	    number |= ((bytes[2] << 16) & 0xFF0000);  
	    number |= ((bytes[3] << 24) & 0xFF000000);  
	    return number;  
	} 
	// 16����ת����BCD
	public static String byteToBCD(byte b) {
		int f1 = (byte) ((b & 0xf0) >>> 4);
		int f2 = ((byte) (b & 0x0f));
		return f1 + "" + f2;
	}

	// ��BCD����ת����������Ȼ�󷵻�
	public static int bytesToBCD(byte[] bytes, int startIndex, int len) {
		// StringBuffer temp=new StringBuffer(bytes.length*2);
		int result = 0;
		for (int i = startIndex; i < startIndex + len; i++) {
			int f1 = (byte) ((bytes[i] & 0xF0) >>> 4);
			result = result * 10 + f1;
			int f2 = (byte) (bytes[i] & 0x0F);
			result = result * 10 + f2;
		}
		return result;
		
	}
	// ��BCD����ת����������Ȼ�󷵻�
	public static int bytesToBCD_DB(byte[] bytes, int startIndex, int len) {
		// StringBuffer temp=new StringBuffer(bytes.length*2);
		int result = 0;
		for (int i = startIndex + len-1; i >=startIndex ; i--) {
			int f1 = (byte) ((bytes[i] & 0xF0) >>> 4);
			result = result * 10 + f1;
			int f2 = (byte) (bytes[i] & 0x0F);
			result = result * 10 + f2;
		}
		//return result;
		return bytesToBCD(bytes,startIndex,len);
	}
	// ��BCD����ת���������ַ�����Ȼ�󷵻�
	public static String bytesToBCDStr(byte[] bytes, int startIndex, int len) {
		// StringBuffer temp=new StringBuffer(bytes.length*2);
		String result = "";
		for (int i = startIndex; i < startIndex + len; i++) {
			int f1 = (byte) ((bytes[i] & 0xF0) >>> 4);
			result = result + f1;
			int f2 = (byte) (bytes[i] & 0x0F);
			result = result + f2;
		}
		//result=PackPre0(result,len*2);
		return result;
	}

	// ��BCD����ת����������Ȼ�󷵻�
	public static long bytesToBCD_L(byte[] bytes, int startIndex, int len) {
		// StringBuffer temp=new StringBuffer(bytes.length*2);
		long result = 0;
		for (int i = startIndex; i < startIndex + len; i++) {
			int f1 = (byte) ((bytes[i] & 0xf0) >>> 4);
			result = result * 10 + f1;
			int f2 = (byte) (bytes[i] & 0x0f);
			result = result * 10 + f2;
		}
		return result;
	}

	// 16�����ַ���ת���ɶ�����
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	public static int GetLenthByID(int id) {
		int len=-1;
		if(id==1){
		//Э������	int	1
			len=1;
		}
		else if(id==2)
		{
		//����״̬	int	1
			len=1;
		}
		else if(id==3)
		{
		//ͨ��״̬	int	1
			len=1;
		}
		else if(id==4)
		{
		//�ϱ���׼ʱ��	BCD	3
			len=3;
		}
		else if(id==5)
		{
		//�ϱ�����1	BCD	2
			len=2;
		}
		else if(id==6)
		{
		//�ϱ�����2	BCD	1
			len=1;
		}
		else if(id==7)
		{
		//����������	BCD	1
			len=1;
		}
		else if(id==8)
		{
		//����ʱ��	BCD	4
			len=4;
		}
		else if(id==9)
		{
		//Уʱ	BCD	6	
			len=6;
		}
		else if(id==10)
		{
		//����	BCD	4
			len=4;
		}
		else if(id==11)
		{
		//��������ַ	int	4	
			len=6;
		}		
		else if(id==12)
		{
		//�˿�	Int	2
			len=3;
		}
		else if(id==13)
		{
		//apn	Int	2
			len=16;
		}else
		{len=-1;}
		return len;
	}
	public static String GetFieldNameByID(int id) {
		String name="";
		if(id==1){
		//Э������	int	1
			name="F_PROTOCOL_TYPE";
		}
		else if(id==2)
		{
		//����״̬	int	1
			name="F_RELAY_STATUS";
		}
		else if(id==3)
		{
		//ͨ��״̬	int	1
			name="F_POWER_STATUS";
		}
		else if(id==4)
		{
		//�ϱ���׼ʱ��	BCD	3
			name="F_REPORT_BASE_TIME";
		}
		else if(id==5)
		{
		//�ϱ�����1	BCD	2
			name="F_REPORT_PERIOD1";
		}
		else if(id==6)
		{
		//�ϱ�����2	BCD	1
			name="F_REPORT_PERIOD2";
		}
		else if(id==7)
		{
		//����������	BCD	1
			name="F_HEARTBEAT_PERIOD";
		}
		else if(id==8)
		{
		//����ʱ��	BCD	4
			name="F_FREEZE_TIME";
		}
		else if(id==9)
		{
		//Уʱ	BCD	6	
			name="F_PROOFREAD";
		}
		else if(id==10)
		{
		//����	BCD	4
			name="F_PASSWORD";
		}
		else if(id==11)
		{
		//��������ַ	int	4	
			name="F_IP";
		}		
		else if(id==12)
		{
		//�˿�	Int	2
			name="F_PORT";
		}else if(id==13)
		{
		//�˿�	Int	2
			name="F_APN";
		}else
		{name="";}
		return name;
	}
	public static String ConvertBCDStr2Update_Str(int id,String BCDStr) {
		String name="";
		if(id==1){
		//Э������	int	1
			int lx=Integer.parseInt(BCDStr);
			if(lx==1)
			name=" F_PROTOCOL_TYPE='1997',";
			else name=" F_PROTOCOL_TYPE='2007',";
		}
		else if(id==2)
		{
		//����״̬	int	1
			int zt=Integer.parseInt(BCDStr);
			name=" F_RELAY_STATUS="+zt+",";
		}
		else if(id==3)
		{
		//ͨ��״̬	int	1
			int td=Integer.parseInt(BCDStr);
			if(td==0)
			name=" F_POWER_STATUS=0,";
			else{
				name=" F_POWER_STATUS=55,";
			}
		}
		else if(id==4)
		{
		//�ϱ���׼ʱ��	BCD	3
			name=" F_REPORT_BASE_TIME='"+BCDStr+"',";
		}
		else if(id==5)
		{
		//�ϱ�����1	BCD	2
			name=" F_REPORT_PERIOD1='"+BCDStr+"',";
		}
		else if(id==6)
		{
		//�ϱ�����2	BCD	1
			name=" F_REPORT_PERIOD2='"+BCDStr+"',";
		}
		else if(id==7)
		{
		//����������	BCD	1
			name=" F_HEARTBEAT_PERIOD='"+BCDStr+"',";
		}
		else if(id==8)
		{
		//����ʱ��	BCD	4
			name=" F_FREEZE_TIME='"+BCDStr+"',";
		}
		else if(id==9)
		{
		//Уʱ	BCD	6	
			name=" F_PROOFREAD='"+BCDStr+"',";
		}
		else if(id==10)
		{
		//����	BCD	4
			name=" F_PASSWORD='"+BCDStr+"',";
		}
		else if(id==11)
		{
		//��������ַ	int	6
			String ip1=BCDStr.substring(0, 3);
			String ip2=BCDStr.substring(3, 6);
			String ip3=BCDStr.substring(6, 9);
			String ip4=BCDStr.substring(9);
			String v=Integer.parseInt(ip1)+"."+Integer.parseInt(ip2)+"."+Integer.parseInt(ip3)+"."+Integer.parseInt(ip4);
			name=" F_IP='"+v+"', ";
		}		
		else if(id==12)
		{
		//�˿�	Int	3
			int pt=Integer.parseInt(BCDStr);
			name=" F_PORT="+pt+",";
		}
		else if(id==13)
		{
		//apn 16
			String apn=NumberUtile.HexStr2ASCIIString(BCDStr);
			name=" F_APN='"+apn+"',";
		}else
		{name="";}
		return name;
	}
	public static String Convert2BCDStr_ByID(int id,String valueStr) {
		String name="";
		if(id==1){
		//Э������	int	1
			if(valueStr.equals("1997"))
			name="01";
			else name="02";
		}
		else if(id==2)
		{
		//����״̬	int	1			
			name="0"+valueStr;
		}
		else if(id==3)
		{
		//ͨ��״̬	int	1
			if(valueStr.equals("0"))
			name="00";
			else{
				name="55";
			}
		}
		else if(id==4)
		{
		//�ϱ���׼ʱ��	BCD	3
			name=valueStr;
		}
		else if(id==5)
		{
		//�ϱ�����1	BCD	2
			name=valueStr;
		}
		else if(id==6)
		{
		//�ϱ�����2	BCD	1
			name=valueStr;
		}
		else if(id==7)
		{
		//����������	BCD	1
			name=valueStr;
		}
		else if(id==8)
		{
		//����ʱ��	BCD	4
			name=valueStr;
		}
		else if(id==9)
		{
		//Уʱ	BCD	6	
			name=valueStr;
		}
		else if(id==10)
		{
		//����	BCD	4
			name=valueStr;
		}
		else if(id==11)
		{
		//��������ַ	int	6
			String[] ips=valueStr.split("\\.");
			String ip1=ips[0];
			String ip2=ips[1];
			String ip3=ips[2];
			String ip4=ips[3];
			name=PackPre0(ip1,3)+PackPre0(ip2,3)+PackPre0(ip3,3)+PackPre0(ip4,3);
		}		
		else if(id==12)
		{
		//�˿�	Int	3
			name=PackPre0(valueStr,6);
		}
		else if(id==13)
		{
		//apn varchar2 16
			name=StrToASCII_hexStr(valueStr,16);
		}else
		{name="";}
		return name;
	}
	public static int byteToInt(byte b) {
		// Java ���ǰ� byte �����з����������ǿ���ͨ������� 0xFF ���ж�������õ������޷�ֵ
		return b & 0xFF;
	}

	// ʮ����ת���ɶ������ַ���
	public static String Integer2toBindaryString(int num, int len) {
		String binaryStr = Integer.toBinaryString(num);
		// System.out.println(hex);
		int bu = len - binaryStr.length();
		if (bu < 0) {
			System.out.println("ת�����ֳ���ָ������");
		}
		for (int m = 0; m < bu; m++) {
			binaryStr = '0' + binaryStr;
		}
		return binaryStr;
	}

	/**
	 * �� int ��������ת��ʮ�����Ƶ��ַ��������� int ����λ��ʱ��ǰ������0���Դ���λ��
	 * 
	 * @param num
	 * @return
	 */

	// �������ַ�����16���Ƶ�ת��==================================
	public static String BinaryStrToHexString(String str, int resultlen) {
		int asd = Integer.valueOf(str, 2);
		String m2 = Integer.toString(asd, 16);
		int bu = resultlen - m2.length();
		if (bu < 0) {
			System.out.println("ת�����ֳ���ָ������");
		}
		for (int m = 0; m < bu; m++) {
			m2 = '0' + m2;
		}
		return m2;
	}

	public static String BinaryStrToHexString(String str) {
		String result = "";
		String hex = "";
		byte[] bytes = str.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			// �ֽڸ�4λ
			hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
			// �ֽڵ�4λ
			hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
			result += hex;
		}
		return result;
	}

	// ==========16������long֮���ת��===========================
	public static String longToHexStr(long i) {
		String hex = Long.toHexString(i);
		if (hex.length() % 2 == 1) {
			hex = '0' + hex;
		}
		return hex.toUpperCase();
	}

	public static String longToHexStr(long i, int len) {
		String hex = Long.toHexString(i);
		// System.out.println(hex);
		int bu = len - hex.length();
		if (bu < 0) {
			System.out.println("ת�����ֳ���ָ������");
		}
		for (int m = 0; m < bu; m++) {
			hex = '0' + hex;
		}
		return hex.toUpperCase();
	}

	// ==========16������int֮���ת��===========================
	public static String intToHexStr(int i) {
		String hex = Integer.toHexString(i);
		if (hex.length() % 2 == 1) {
			hex = '0' + hex;
		}
		return hex.toUpperCase();
	}

	public static String intToHexStr(int i, int len) {
		String hex = Integer.toHexString(i);
		int bu = len - hex.length();
		if (bu < 0) {
			System.out.println("intToHexStrת�����ֳ���ָ������");
		}
		for (int m = 0; m < bu; m++) {
			hex = '0' + hex;
		}
		return hex.toUpperCase();
	}

	// 16����ת����Int�ܼ�
	public static int HexStrToint(String i) {
		return Integer.valueOf(i, 16);
	}

	public static int getInt(char ch) {// ����16���Ƶ��ַ��õ���Ӧ��intֵ��a��10��b��11.
		if (ch >= '0' && ch <= '9')
			return ch -= '0';
		if (ch >= 'a' && ch <= 'z')
			ch -= 32;
		if (ch < 'A' || ch > 'Z')
			throw new RuntimeException("�������Ϸ�!");
		return 10 + (ch - 'A');
	}

	public static int[] hexStr2BitArray(String str) {// ��16�����ַ���ת���ɶ���������
		char chs[] = str.toCharArray();
		int bits[] = new int[chs.length * 4];
		int index = 0;
		for (int i = 0; i < chs.length; i++) {
			for (int j = 3; j >= 0; j--) {
				if (((1 << j) & getInt(chs[i])) != 0) {
					bits[index++] = 1;
				} else
					bits[index++] = 0;
			}
		}
		// System.out.println(Arrays.toString(bits));
		return bits;
	}

	public static int parseSign(int ch[]) {// ��������λ
		return (ch[0] == 1 ? -1 : 1);
	}

	public static int parseExponent(int ch[]) {// ����ָ��
		int result = 0;
		for (int i = 1; i <= 8; i++) {
			result += ch[i] * Math.pow(2, 8 - i);
		}
		// System.out.println(result);
		return result;
	}

	public static double parseEnding(int[] ch) {// ����β��
		double result = 0;
		for (int i = 9; i <= 31; i++) {
			result += ch[i] * Math.pow(0.5, i - 8);
		}
		// System.out.println(result);
		return result;
	}

	public static float hexStr2Float(String str) {
		int[] bits = hexStr2BitArray(str);
		int sign = parseSign(bits);
		int e = parseExponent(bits);
		double m = parseEnding(bits);
		// System.out.println("sign="+sign+",e="+e+",m="+m);
		if (e == 0 && m == 0) {
			return 0;
		} else if ((e == 0) && (m != 0)) {
			return (float) (sign * Math.pow(2.0, -126) * m);
		} else if (e >= 1 && e <= 254 && m != 0) {
			return (float) (sign * Math.pow(2.0, e - 127) * (1 + m));
		} else if ((e == 255) && m != 0) {
			if ((sign == 1) && (m == 0.5))
				return Float.NaN;
			else if ((sign == 1) && (m == 0))
				return Float.NEGATIVE_INFINITY;
			else
				return Float.POSITIVE_INFINITY;
		}
		return 0;
	}

	public static String floatToHexStr(float f) {// floatת����16���ƺܼ�
		// ���㷵�ص�16���Ƴ��ȹ̶�Ϊ4�ֽڣ�8λ��
		String asd = Integer.toString(Float.floatToIntBits(f), 16)
				.toUpperCase();
		int bu = 8 - asd.length();
		if (bu < 0) {
			System.out.println("ת�����ֳ���ָ������");
		}
		for (int m = 0; m < bu; m++) {
			asd = '0' + asd;
		}
		// System.out.println("8wei:"+asd);
		return asd;
	}

	// ==============================================================

	public static int TimeStr2Int(String timestr, String f) {
		int result = 0;
		if (f.equalsIgnoreCase("hh")) {
			result = Integer.parseInt(timestr) * 60 * 60;
		}
		if (f.equalsIgnoreCase("mm")) {
			result = Integer.parseInt(timestr) * 60;
		}
		if (f.equalsIgnoreCase("hhmm")) {
			result = Integer.parseInt(timestr.substring(0, 2)) * 60 * 60
					+ Integer.parseInt(timestr.substring(2)) * 60;
		}
		if (f.equalsIgnoreCase("hhmmss")) {
			result = Integer.parseInt(timestr.substring(0, 2)) * 60 * 60
					+ Integer.parseInt(timestr.substring(2, 4)) * 60
					+ Integer.parseInt(timestr.substring(4));
		}
		return result;
	}
//����ֵ��λ�ǡ��롱
	public static String Int2TimeStr(int time, String f) {
		String result = "";
		int m = time;
		int second = m % 60;
		m = m / 60;
		int minute = m % 60;
		m = m / 60;
		int houre = m;
		if (f.equalsIgnoreCase("hh")) {
			houre=houre%24;
			if (houre < 10) {
				result = "0" + houre;
			}else {
				result = "" + houre;
				//if (result.length() > 2)
				//	result = result.substring(result.length() - 2);
			}
		}
		if (f.equalsIgnoreCase("mm")) {
			minute=minute%60;
			if (minute < 10) {
				result = "0" + minute;
			} else {
				result = "" + minute;
			}
		}
		if (f.equalsIgnoreCase("ss")) {
			second=second%60;
			if (second < 10) {
				result = "0" + second;
			} else {
				result = "" + second;
			}
		}
		if (f.equalsIgnoreCase("hhmm")) {
			result = Int2TimeStr(time, "hh") + Int2TimeStr(time, "mm");
		}
		if (f.equalsIgnoreCase("hhmmss")) {
			result = Int2TimeStr(time, "hh") + Int2TimeStr(time, "mm")
					+ Int2TimeStr(time, "ss");
		}
		return result;
	}
	public static String GetCurrentTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
/*
	public static String GetCurrentTime() {
		java.text.DateFormat format2 = new java.text.SimpleDateFormat(
				"yyyyMMddhhmmss");
		Format dateFormatterChina = DateFormat.getDateTimeInstance(
				DateFormat.MEDIUM, DateFormat.MEDIUM);// ��ʽ�����
		TimeZone timeZoneChina = TimeZone.getTimeZone("Asia/Shanghai");// ��ȡʱ��
		((DateFormat) dateFormatterChina).setTimeZone(timeZoneChina);// ����ϵͳʱ��
		Date curDate = new Date();// ��ȡϵͳʱ��
		String aa = dateFormatterChina.format(curDate);
		String returnStr = "";
		String[] strs = aa.split(" ");
		String[] preStrs = strs[0].trim().split("-");
		String[] afterStrs = strs[1].trim().split(":");
		for (int i = 0; i < preStrs.length; i++) {
			String temp = preStrs[i].trim();
			if (temp.length() < 2) {
				temp = "0" + temp;
			}
			returnStr = returnStr + temp;
		}
		for (int j = 0; j < afterStrs.length; j++) {
			String temp = afterStrs[j].trim();
			if (temp.length() < 2) {
				temp = "0" + temp;
			}
			returnStr = returnStr + temp;
		}
		// System.out.println(returnStr);
		returnStr = returnStr.substring(2);
		returnStr = returnStr.substring(0, returnStr.length() - 2);
		return returnStr;
	}
	*/
	public static String PackPre0(String src, int len) {
		String returnStr = src;
		int bu = len - src.length();
		if (bu < 0) {
			System.out.println("PackPre0ת�����ֳ���ָ������,�Զ���ȡ�Ҳ�����");
			returnStr=src.substring(-bu);
		}
		for (int m = 0; m < bu; m++) {
			returnStr = '0' + returnStr;
		}
		return returnStr;
	}
	/*
	public static  String ConfigAll_DB2Protol1(String DB){
		String result="";
		String[] strs = DB.split(",");
		String K1="0"+strs[0];
		String S1=NumberUtile.Int2TimeStr(Integer.parseInt(strs[1]), "hhmmss");
		String S2=NumberUtile.Int2TimeStr(Integer.parseInt(strs[2]), "hhmm");
		String S3=NumberUtile.Int2TimeStr(Integer.parseInt(strs[3]), "mm");
		String S4=NumberUtile.Int2TimeStr(Integer.parseInt(strs[4]), "mm");;
		String S5=NumberUtile.PackPre0(strs[5],8);
		String S6=NumberUtile.GetCurrentTime();
		String pw = NumberUtile.PackPre0(strs[6], 8);// PW
		String ipStr = strs[7];// IP
		String[] ips = ipStr.split("\\.");
		String ip_hex="";
		ip_hex = ip_hex
				+ NumberUtile.intToHexStr(Integer.parseInt(ips[0]), 2);
		ip_hex = ip_hex
				+ NumberUtile.intToHexStr(Integer.parseInt(ips[1]), 2);
		ip_hex = ip_hex
				+ NumberUtile.intToHexStr(Integer.parseInt(ips[2]), 2);
		ip_hex = ip_hex
				+ NumberUtile.intToHexStr(Integer.parseInt(ips[3]), 2);
		// PORT
		String port = NumberUtile.intToHexStr(Integer.parseInt(strs[8]), 4);
		result=K1+S1+S2+S3+S4+S5+S6+pw+ip_hex+port;
		return result;
	}
	public static  String ConfigAll_DB2ProtolByComma1(String DB){
		String result="";
		String[] strs = DB.split(",");
		String K1="0"+strs[0];
		String S1=NumberUtile.Int2TimeStr(Integer.parseInt(strs[1]), "hhmmss");
		String S2=NumberUtile.Int2TimeStr(Integer.parseInt(strs[2]), "hhmm");
		String S3=NumberUtile.Int2TimeStr(Integer.parseInt(strs[3]), "mm");
		String S4=NumberUtile.Int2TimeStr(Integer.parseInt(strs[4]), "mm");;
		String S5=NumberUtile.PackPre0(strs[5],8);
		String S6=NumberUtile.GetCurrentTime();
		String pw = NumberUtile.PackPre0(strs[6], 8);// PW
		String ipStr = strs[7];// IP
		String[] ips = ipStr.split("\\.");
		String ip_hex="";
		ip_hex = ip_hex
				+ NumberUtile.intToHexStr(Integer.parseInt(ips[0]), 2);
		ip_hex = ip_hex
				+ NumberUtile.intToHexStr(Integer.parseInt(ips[1]), 2);
		ip_hex = ip_hex
				+ NumberUtile.intToHexStr(Integer.parseInt(ips[2]), 2);
		ip_hex = ip_hex
				+ NumberUtile.intToHexStr(Integer.parseInt(ips[3]), 2);
		// PORT
		String port = NumberUtile.intToHexStr(Integer.parseInt(strs[8]), 4);
		result=K1+","+S1+","+S2+","+S3+","+S4+","+S5+","+S6+","+pw+","+ip_hex+","+port;
		return result;
	}
	*/
	public static String getCSStr(String src) {
		int len = src.length();
		byte[] raw = new byte[len / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < len; i += 2) {
			raw[i / 2] = uniteBytes(tmp[i], tmp[i + 1]);
		}
		// byte[] raw=HexString2Buf(input);
		// final StringBuilder hex = new StringBuilder(2 * raw.length);
		int temp = 0;
		for (final byte b : raw) {

			temp = temp + b;
			temp = temp & 0xFF;
		}

		String hex = Integer.toHexString(temp);
		if (hex.length() == 1) {
			hex = '0' + hex;
		}
		return hex.toUpperCase();
	}

	private static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}
	//-------ASCIIת��-----------------
	//���ַ���ת���ɹ̶����ȵ�16�����ַ���,���Ȳ�����00
	public static String StrToASCII_hexStr(String s,int len ){
		String result="";
		int[] temp=string2ASCII(s);
		int s_len=-1;
		if(temp!=null)s_len=temp.length;
		for(int i=0;i<s_len;i++){
			result=result+intToHexStr(temp[i]);
		}
		if (s_len==-1)s_len=0;
		for(int j=0;j<len-s_len;j++){
			result=result+"00";
		}
		return result;
	}
	 private static int[] string2ASCII(String s) {// �ַ���ת��ΪASCII��  
	        if (s == null || "".equals(s)) {  
	            return null;  
	        }  
	  
	        char[] chars = s.toCharArray();  
	        int[] asciiArray = new int[chars.length];  
	  
	        for (int i = 0; i < chars.length; i++) {  
	            asciiArray[i] = char2ASCII(chars[i]);  
	        }  
	        return asciiArray;  
	    }
	 private static int char2ASCII(char c) {  
	        return (int) c;  
	    } 
	 public static String HexStr2ASCIIString(String value){
		 String result="";
		int len=value.length()/2;
		String tempStr=value;
		for(int m=0;m<len;m++){
			String newStr=tempStr.substring(0, 2);
			tempStr=tempStr.substring(2);
			if(!newStr.equals("00")){
				int v=Integer.valueOf(newStr, 16);
				result=result+ascii2Char(v)+"";
			}
		}
		return result;
	 }
	    private static String ASCII2String(int[] ASCIIs) {  
	        StringBuffer sb = new StringBuffer();  
	        for (int i = 0; i < ASCIIs.length; i++) {  
	            sb.append((char)ascii2Char(ASCIIs[i]));  
	        }  
	        return sb.toString();  
	    }
	    private static char ascii2Char(int ASCII) {  
	        return (char) ASCII;  
	    } 
}