package NJUPTDataServer.Mina;

import java.sql.Timestamp;
import NJUPTDataServer.Utile.ConstVar;
import NJUPTDataServer.Utile.DBOperation;
import NJUPTDataServer.Utile.IDBOperation;
import NJUPTDataServer.Utile.NumberUtile;

public class Adress {
	private IDBOperation db = new DBOperation();
	long timestamp;
	private String ID;
	// ���ؽ����Ľ��
	private String returnStr;
	// ��ǰ�ļĴ���
	private int currentJCQ = ConstVar.JCQ_UNKNOWN;

	/*
	 * =============0 ��=============================
	 * 0x0000+(i-1)*8+j-1:��ʾ��i��I/O���ӵĵ�j·�����ֵ��0<i<9;0<j<9 ���ֵΪ 63
	 * 
	 * 0x0040~0041 ���ذ��ϵ�2���̵����� I/O���ֵ ���ֵΪ 0x0041
	 * 
	 * 0x0048+(i-1)*8+j-1:��ʾ��i����ӵĵ�j·��I/O��������ã�ʹ�ã������1��ʾʹ�ã�0��ʾδ�ã�0<i<9;0<j<9
	 * ;���ֵΪ0x0048+63
	 * 
	 * 0x0088+(i-1)*8+j-1:��ʾ��i����ӵĵ�j·��I/O��������ã�ʹ�ã������1��ʾʹ�ã�0��ʾδ�ã�0<i<9;0<j<9
	 * 
	 * 0x00C8~00C9 ���ذ����̵�������(ʹ��)�����1��ʾʹ�ã�0��ʾδ�ã�
	 * 
	 * 0x00D0+(i-1)*12+j-1:��ʾ��i���¶Ȳɼ���ĵ�j·�����ã�ʹ�ã������1��ʾʹ�ã�0��ʾδ�ã�0<i<9;0<j<13
	 * 
	 * 0x0150+(i-1)*12+j-1:��ʾ��i��λ�Ʋɼ���ĵ�j·�����ã�ʹ�ã������1��ʾʹ�ã�0��ʾδ�ã�0<i<9;0<j<13
	 * 
	 * =============1 ��=============================
	 * 0x0000+(i-1)*8+j-1:��ʾ��i��I/O���ӵĵ�j·������ֵ��0<i<9;0<j<9
	 * 
	 * =============3 ��=============================
	 * 0x0000+(i-1)*24+(j-1)*2:��ʾ��i���¶Ȳɼ���ĵ�j·������ֵ��0<i<9;0<j<13 ���ֵ
	 * 0x0000+190=0xBE
	 * 
	 * 0x0100+(i-1)*16+j-1:��ʾ��i��λ�Ʋɼ���ĵ�j·������ֵ��0<i<9;0<j<17 ���ֵ 0x0100+127=0x017F
	 * 
	 * =============4 ��============================= <=0x007F�ĵ�ַԤ��
	 * 
	 * 0x0100+(i-1)*120+(j-1)*10 +z ��ʾ��i���¶Ȳɼ���ĵ�j·����ֵ��0<i<9;0<j<13;z=0
	 * ��2��4��6��8��10 �ֱ��ʾ��һ������ֵ������һ������ֵ��������������ֵ��������������ֵ��������������ֵ��������������ֵ�������ֵ0x057E
	 * 
	 * 0x0580+(i-1)*96+(j-1)*6 +z ��ʾ��i��λ�Ʋɼ���ĵ�j·����ֵ��0<i<9;0<j<17;z=0 ��1��2��3��4��5
	 * �ֱ��ʾ��һ������ֵ������һ������ֵ��������������ֵ��������������ֵ��������������ֵ��������������ֵ�� ���ֵ0x087F
	 * 
	 * 0900~0901 IP��ַ
	 * 
	 * 0902 �˿ڵ�ַ
	 */
	public String Parser(int sub_function, byte[] data, long _timestamp,
			String _ID, boolean needDBOperate) {
		timestamp = _timestamp;
		ID = _ID;
		// ���� ��ʼ����Ҫ���ص��ַ���
		returnStr = "";
		if (sub_function == ConstVar.F_SUB_READ_RELAY_STATE_AND_CONFIG
				|| sub_function == ConstVar.F_SUB_SET_RELAY_STATE_AND_CONFIG) {
			currentJCQ = ConstVar.JCQ_0_QU;
		} else if (sub_function == ConstVar.F_SUB_READ_CONTROL_STATE) {
			currentJCQ = ConstVar.JCQ_1_QU;
		} else if (sub_function == ConstVar.F_SUB_READ_TEMPERATURE_DISPLACEMENT) {
			currentJCQ = ConstVar.JCQ_3_QU;
		} else if (sub_function == ConstVar.F_SUB_READ_TEMPERATURE_DOMAIN_IP
				|| sub_function == ConstVar.F_SUB_SET_TEMPERATURE_DOMAIN_IP) {
			currentJCQ = ConstVar.JCQ_4_QU;
		} else {
			currentJCQ = ConstVar.JCQ_UNKNOWN;
		}
		int containsContext = NumberUtile.byteToInt(data[0]);
//		int containsContext = data[0];
		// ����������������ǲ������ɼ����ݵ�����Ƭ�Σ�����ʱ��Ҫֻ������ʼ��ַ�����ݳ��ȼ���Ӧ�����ݣ���־������ΪFF��
		if (containsContext == 0)
			returnStr = returnStr + "FF";
		// �������������Ҫ�������ݿ⣬���۷������������Ƿ�����ɼ����ݵ�����Ƭ�Σ�������Ҫ��������
		if (!needDBOperate)
			returnStr = returnStr + "00";
		// ����������������ǰ����ɼ����ݵ�����Ƭ�Σ�����ʱ��Ҫֻ������ʼ��ַ�����ݳ��ȣ���־������Ϊ00��
		if (containsContext == 0xFF)
			returnStr = returnStr + "00";
		int i = 1;
		int len = data.length;
		if (currentJCQ == ConstVar.JCQ_0_QU) {
			while (i < len) {
				i = ReadBitType(i, ID, data, containsContext, needDBOperate);
			}
			System.out.println("0��������ɣ�");
		} else if (currentJCQ == ConstVar.JCQ_1_QU) {
			while (i < len) {
				i = ReadBitType(i, ID, data, containsContext, needDBOperate);
			}
			System.out.println("1���������");
		} else if (currentJCQ == ConstVar.JCQ_3_QU) {
			while (i < len) {
				int StartAdress = (data[i] << 8) & 0xFF00;// ������ʼ��ַ
				StartAdress |= data[i + 1] & 0xFF;
				if (StartAdress <= 0x005F) {
					i = Read16_Signed(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x017F) {
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else {
				}
			}
			System.out.println("3��������ɣ�");
		} else if (currentJCQ == ConstVar.JCQ_4_QU) {
			while (i < len) {
				int StartAdress = ((data[i] << 8) & 0xFF00);// ������ʼ��ַ
				StartAdress |= (data[i + 1] & 0xFF);
				if (StartAdress <= 0x007F) {
					System.out.println("Ԥ��");
				} else if (StartAdress <= 0x033F) {
					i = Read16_Signed(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x087F) {
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x0901) {
					// IP
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress == 0x0902) {
					// Port
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x0904) {
					// ��׼ʱ��
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x0906) {
					// �ϱ�����
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress == 0x0907) {
					// ��������
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x090A) {
					// ID
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x090C) {
					// ����
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else {
				}
			}
			System.out.println("4��������ɣ�");
		} else {
		}

		// Integer.parseInt(NumberUtile.bytes2HexString(data[4]), 16);
		return returnStr;
	}

	/**
	 * ���ش������ݵ��ֽ�����λ�ã���������data�ֽ�������±꣩������ú����ߴ������ݱ㹹�������ַ���
	 * 
	 * @param byteIndex
	 *            ��ǰ׼���������ֽ��α꣬����������data�ֽ�������±�
	 * @param data
	 *            ��ǰ�������ֽ�����
	 * @return
	 */
	private int ReadBitType(int byteIndex, String _ID, byte[] data,
			int containsSensorData, boolean needDBOperate) {
		int i = byteIndex;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		int StartAdress = ((data[i]) << 8) & 0xFF00;// ������ʼ��ַ
		i++;// ���ݴ�������ֽ���+1
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		StartAdress |= data[i] & 0xFF;
		i++;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		int currentAdress = StartAdress;// ��ǰ���ݵ�ַ���൱���α꣩
		// ��ȡ���ݱ������������Ĵ�������������һ���Ĵ�����Ӧһ��������
		int var_count = ((data[i]) << 8) & 0xFF00;
		i++;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		var_count |= data[i] & 0xFF;
		i++;
		if (containsSensorData == 0xFF) {// ���������ž���ɼ������ݣ�����Ҫ�����ݽ���Ȼ��������ݿ�
			// ��Ϊ�Ǳ������ͣ�������Ҫ�������������ռ���˼����ֽ�
			int byteCount = var_count / 8;
			for (int m = 0; m < byteCount; m++) {
				for (int n = 8; n > 0; n--) {
					int value = (byte) ((data[i] >> (n - 1)) & 0x1);
					boolean b_value;
					if (value == 0) {
						b_value = false;
					} else {
						b_value = true;
					}
					if (needDBOperate) {
						String sql = "";
						if (currentJCQ == ConstVar.JCQ_0_QU) {
							sql = "update sys_register_zero set value='"
									+ b_value + "',timestamp=" + timestamp
									+ ",create_date=" + "'"
									+ new Timestamp(System.currentTimeMillis())
									+ "'" + " where address=" + currentAdress
									+ " and del_flag='0' and device_number='" + _ID + "'";
						} else if (currentJCQ == ConstVar.JCQ_1_QU) {
							sql = "update sys_register_one set value='"
									+ b_value + "',timestamp=" + timestamp
									+ ",create_date=" + "'"
									+ new Timestamp(System.currentTimeMillis())
									+ "'" + " where address=" + currentAdress
									+ " and del_flag='0' and device_number='" + _ID + "'";
						} else {
							System.out.println("δ֪�Ĵ�������");
						}
						db.excuteNoResult(sql);
					}
					System.out.println("��ַ��"
							+ Integer.toHexString(currentAdress) + ", ֵ��"
							+ value);

					currentAdress++;
				}
				i++;
			}
			// ���������˵������һ��δռ�����ֽ�
			int remainder;
			remainder = var_count % 8;
			if (remainder > 0) {
				for (int n = remainder; n > 0; n--) {
					int value = (byte) ((data[i] >> (n - 1)) & 0x1);
					boolean b_value;
					if (value == 0) {
						b_value = false;
					} else {
						b_value = true;
					}
					if (needDBOperate) {
						String sql = "";
						if (currentJCQ == ConstVar.JCQ_0_QU) {
							sql = "update sys_register_zero set value='"
									+ b_value + "',timestamp=" + timestamp
									+ ",create_date=" + "'"
									+ new Timestamp(System.currentTimeMillis())
									+ "'" + " where address=" + currentAdress
									+ " and del_flag='0' and device_number='" + _ID + "'";
						} else if (currentJCQ == ConstVar.JCQ_1_QU) {
							sql = "update sys_register_one set value='"
									+ b_value + "',timestamp=" + timestamp
									+ ",create_date=" + "'"
									+ new Timestamp(System.currentTimeMillis())
									+ "'" + " where address=" + currentAdress
									+ " and del_flag='0' and device_number='" + _ID + "'";
						} else {
							System.out.println("δ֪�Ĵ�������");
						}
						db.excuteNoResult(sql);
					}
					// �޸�-�ѵ�ַת����16�����ַ������
					System.out.println("��ַ��"
							+ Integer.toHexString(currentAdress) + ", ֵ��"
							+ value);

					currentAdress++;
				}
				i++;
			}
		} else if (containsSensorData == 0) {// �������û���ž���ɼ������ݣ�����Ҫ�����ݿ�ȡ�����ݣ�Ȼ����ڷ����ַ���
			String sub_return_str = "";
			if (needDBOperate) {
				for (int m = 0; m < var_count; m++) {
					String sql = "";
					if (currentJCQ == ConstVar.JCQ_0_QU) {
						sql = "select value from sys_register_zero where address="
								+ currentAdress
								+ " and del_flag='0' and device_number='"
								+ _ID
								+ "'";
					} else if (currentJCQ == ConstVar.JCQ_1_QU) {
						sql = "select value from sys_register_one where address="
								+ currentAdress
								+ " and del_flag='0' and device_number='"
								+ _ID
								+ "'";
					} else {
						System.out.println("δ֪�Ĵ�������");
					}
					String value_str = "";
					value_str = db.excuteWithResult(sql);
					if (value_str.equalsIgnoreCase("false"))
						sub_return_str = sub_return_str + "0";
					else
						sub_return_str = sub_return_str + "1";

					// ����չ�һ���ֽ��ˣ��ͽ���ֵ�洢������ַ�����
					if (sub_return_str.length() == 8) {
						returnStr = returnStr
								+ Integer.toHexString(Integer.parseInt(
										sub_return_str, 2));
						sub_return_str = "";
					}
					// ��ַ+1
					currentAdress++;
				}
				// �ж� sub_return_str�ĳ��ȣ����ȴ���0˵�����¼�λ����Ҫ��λ���㣬Ȼ��洢�������ַ�����
				if (sub_return_str.length() > 0) {
					for (int m = 0; m < 8 - sub_return_str.length(); m++) {
						sub_return_str = "0" + sub_return_str;
					}
					// �������Ƶ��ַ�����ת����16�����ַ���
					returnStr = returnStr
							+ Integer.toHexString(Integer.parseInt(
									sub_return_str, 2));
				}
			}
		} else {
			System.out.println("��ʽ�쳣�����Ƿ�������ݡ����ֳ���0��1�����ֵ��");
		}
		return i;
	}
	/**
	 * ���ش������ݵ��ֽ�����λ�ã���������data�ֽ�������±꣩
	 * 
	 * @param byteIndex
	 *            ��ǰ׼���������ֽ��α꣬����������data�ֽ�������±�
	 * @param data
	 *            ��ǰ�������ֽ�����
	 * @return
	 */
	private int Read16_Unsign(int byteIndex, String _ID, byte[] data,
			int containsSensorData, boolean needDBOperate) {
		int i = byteIndex;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		int StartAdress = ((data[i] << 8) & 0xFF00);// ������ʼ��ַ
		i++;// ���ݴ�������ֽ���+1
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		StartAdress |= data[i] & 0xFF;
		i++;
		int currentAdress = StartAdress;// ��ǰ���ݵ�ַ���൱���α꣩
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		// ��ȡ���ݱ������������Ĵ�������������һ���Ĵ�����Ӧһ��������
		int var_count = ((data[i] << 8) & 0xFF00);
		i++;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		var_count |= data[i] & 0xFF;
		i++;
		if (containsSensorData == 0xFF) {// ���������ž���ɼ������ݣ�����Ҫ�����ݽ���Ȼ��������ݿ�
			// ��Ϊ��16λ�޷������ͣ�ÿ������ռ��2���ֽ�
			for (int m = 0; m < var_count; m++) {
				String value = NumberUtile.bytes2HexString(data[i]);
				i++;
				value = value + NumberUtile.bytes2HexString(data[i]);
				i++;
				// 1-23�޸�-���ݱ�����value��double���ͣ�
				if (needDBOperate) {
					String sql = "";
					if (currentJCQ == ConstVar.JCQ_3_QU) {
						sql = "update sys_register_three set value='" + value
								+ "',timestamp=" + timestamp + ",create_date="
								+ "'"
								+ new Timestamp(System.currentTimeMillis())
								+ "'" + " where address=" + currentAdress
								+ " and del_flag='0' and device_number='" + _ID + "'";
					} else if (currentJCQ == ConstVar.JCQ_4_QU) {
						sql = "update sys_register_four set value='" + value
								+ "',timestamp=" + timestamp + ",create_date="
								+ "'"
								+ new Timestamp(System.currentTimeMillis())
								+ "'" + " where address=" + currentAdress
								+ " and del_flag='0' and device_number='" + _ID + "'";
					} else {
						System.out.println("δ֪�Ĵ�������");
					}
					db.excuteNoResult(sql);
				}
				// table.put(currentAdress, value);
				// �޸�-�ѵ�ַת����16�����ַ������
				System.out.println("��ַ��" + Integer.toHexString(currentAdress)
						+ ", ֵ��" + value);
				currentAdress++;
			}
		} else if (containsSensorData == 0) {// ���������ž���ɼ������ݣ�����Ҫ�����ݽ���Ȼ��������ݿ�
			String sub_return_str = "";
			if (needDBOperate) {
				for (int m = 0; m < var_count; m++) {
					String sql = "";
					if (currentJCQ == ConstVar.JCQ_3_QU) {
						sql = "select  value from sys_register_three where address="
								+ currentAdress
								+ " and del_flag='0' and device_number='"
								+ _ID
								+ "'";
					} else if (currentJCQ == ConstVar.JCQ_4_QU) {
						sql = "select  value from sys_register_four where address="
								+ currentAdress
								+ " and del_flag='0' and device_number='"
								+ _ID
								+ "'";
					} else {
						System.out.println("δ֪�Ĵ�������");
					}
					String value_str = db.excuteWithResult(sql);
					System.out.println("16λ�޷��ţ���δ����ݿ�ת��������δ��");
					returnStr = returnStr + value_str;
				}
			}
		} else {
			System.out.println("��ʽ�쳣�����Ƿ�������ݡ����ֳ���0��1�����ֵ��");
		}

		return i;
	}

	/**
	 * ���ش������ݵ��ֽ�����λ�ã���������data�ֽ�������±꣩
	 * 
	 * @param byteIndex
	 *            ��ǰ׼���������ֽ��α꣬����������data�ֽ�������±�
	 * @param data
	 *            ��ǰ�������ֽ�����
	 * @return
	 */
	private int Read16_Signed(int byteIndex, String _ID, byte[] data,
			int containsSensorData, boolean needDBOperate) {
		// �Ȱ��з��������޷�����������
		return Read16_Unsign(byteIndex, _ID, data, containsSensorData,
				needDBOperate);

	}
}