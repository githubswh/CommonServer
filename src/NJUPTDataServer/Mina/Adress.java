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
	// 返回解析的结果
	private String returnStr;
	// 当前的寄存器
	private int currentJCQ = ConstVar.JCQ_UNKNOWN;

	/*
	 * =============0 区=============================
	 * 0x0000+(i-1)*8+j-1:表示第i块I/O板子的第j路的输出值，0<i<9;0<j<9 最大值为 63
	 * 
	 * 0x0040~0041 主控板上的2个继电器的 I/O输出值 最大值为 0x0041
	 * 
	 * 0x0048+(i-1)*8+j-1:表示第i块板子的第j路的I/O输出的配置（使用）情况，1表示使用，0表示未用，0<i<9;0<j<9
	 * ;最大值为0x0048+63
	 * 
	 * 0x0088+(i-1)*8+j-1:表示第i块板子的第j路的I/O输入的配置（使用）情况，1表示使用，0表示未用，0<i<9;0<j<9
	 * 
	 * 0x00C8~00C9 主控板两继电器配置(使用)情况，1表示使用，0表示未用，
	 * 
	 * 0x00D0+(i-1)*12+j-1:表示第i块温度采集板的第j路的配置（使用）情况，1表示使用，0表示未用，0<i<9;0<j<13
	 * 
	 * 0x0150+(i-1)*12+j-1:表示第i块位移采集板的第j路的配置（使用）情况，1表示使用，0表示未用，0<i<9;0<j<13
	 * 
	 * =============1 区=============================
	 * 0x0000+(i-1)*8+j-1:表示第i块I/O板子的第j路的输入值，0<i<9;0<j<9
	 * 
	 * =============3 区=============================
	 * 0x0000+(i-1)*24+(j-1)*2:表示第i块温度采集板的第j路的输入值，0<i<9;0<j<13 最大值
	 * 0x0000+190=0xBE
	 * 
	 * 0x0100+(i-1)*16+j-1:表示第i块位移采集板的第j路的输入值，0<i<9;0<j<17 最大值 0x0100+127=0x017F
	 * 
	 * =============4 区============================= <=0x007F的地址预留
	 * 
	 * 0x0100+(i-1)*120+(j-1)*10 +z 表示第i块温度采集板的第j路的阈值，0<i<9;0<j<13;z=0
	 * 或2或4或6或8或10 分别表示“一级上限值”、“一级下限值”、“二级上限值”、“二级下限值”、“三级上限值”、“三级下限值”。最大值0x057E
	 * 
	 * 0x0580+(i-1)*96+(j-1)*6 +z 表示第i块位移采集板的第j路的阈值，0<i<9;0<j<17;z=0 或1或2或3或4或5
	 * 分别表示“一级上限值”、“一级下限值”、“二级上限值”、“二级下限值”、“三级上限值”、“三级下限值” 最大值0x087F
	 * 
	 * 0900~0901 IP地址
	 * 
	 * 0902 端口地址
	 */
	public String Parser(int sub_function, byte[] data, long _timestamp,
			String _ID, boolean needDBOperate) {
		timestamp = _timestamp;
		ID = _ID;
		// 首先 初始化需要返回的字符串
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
		// 如果发送来的数据是不包含采集数据的数据片段，返回时需要只返回起始地址、数据长度及对应的数据，标志部分设为FF；
		if (containsContext == 0)
			returnStr = returnStr + "FF";
		// 如果本操作不需要连接数据库，无论发送来的数据是否包含采集数据的数据片段，都不需要返回数据
		if (!needDBOperate)
			returnStr = returnStr + "00";
		// 如果发送来的数据是包含采集数据的数据片段，返回时需要只返回起始地址和数据长度，标志部分设为00；
		if (containsContext == 0xFF)
			returnStr = returnStr + "00";
		int i = 1;
		int len = data.length;
		if (currentJCQ == ConstVar.JCQ_0_QU) {
			while (i < len) {
				i = ReadBitType(i, ID, data, containsContext, needDBOperate);
			}
			System.out.println("0区解析完成！");
		} else if (currentJCQ == ConstVar.JCQ_1_QU) {
			while (i < len) {
				i = ReadBitType(i, ID, data, containsContext, needDBOperate);
			}
			System.out.println("1区解析完成");
		} else if (currentJCQ == ConstVar.JCQ_3_QU) {
			while (i < len) {
				int StartAdress = (data[i] << 8) & 0xFF00;// 数据起始地址
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
			System.out.println("3区解析完成！");
		} else if (currentJCQ == ConstVar.JCQ_4_QU) {
			while (i < len) {
				int StartAdress = ((data[i] << 8) & 0xFF00);// 数据起始地址
				StartAdress |= (data[i + 1] & 0xFF);
				if (StartAdress <= 0x007F) {
					System.out.println("预留");
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
					// 基准时间
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x0906) {
					// 上报周期
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress == 0x0907) {
					// 心跳周期
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x090A) {
					// ID
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else if (StartAdress <= 0x090C) {
					// 密码
					i = Read16_Unsign(i, ID, data, containsContext,
							needDBOperate);
				} else {
				}
			}
			System.out.println("4区解析完成！");
		} else {
		}

		// Integer.parseInt(NumberUtile.bytes2HexString(data[4]), 16);
		return returnStr;
	}

	/**
	 * 返回处理数据的字节索引位置（将处理的data字节数组的下标），此外该函数边处理数据便构建返回字符串
	 * 
	 * @param byteIndex
	 *            当前准备处理的字节游标，即将处理的data字节数组的下标
	 * @param data
	 *            当前处理的字节数组
	 * @return
	 */
	private int ReadBitType(int byteIndex, String _ID, byte[] data,
			int containsSensorData, boolean needDBOperate) {
		int i = byteIndex;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		int StartAdress = ((data[i]) << 8) & 0xFF00;// 数据起始地址
		i++;// 数据处理完后字节数+1
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		StartAdress |= data[i] & 0xFF;
		i++;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		int currentAdress = StartAdress;// 当前数据地址（相当于游标）
		// 获取传递变量的数量（寄存器的数据量，一个寄存器对应一个变量）
		int var_count = ((data[i]) << 8) & 0xFF00;
		i++;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		var_count |= data[i] & 0xFF;
		i++;
		if (containsSensorData == 0xFF) {// 如果后面跟着具体采集的数据，则需要将数据解析然后存入数据库
			// 因为是比特类型，所以需要计算变量的数量占满了几个字节
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
							System.out.println("未知寄存器区域");
						}
						db.excuteNoResult(sql);
					}
					System.out.println("地址："
							+ Integer.toHexString(currentAdress) + ", 值："
							+ value);

					currentAdress++;
				}
				i++;
			}
			// 如果有余数说明还有一个未占满的字节
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
							System.out.println("未知寄存器区域");
						}
						db.excuteNoResult(sql);
					}
					// 修改-把地址转换成16进制字符串输出
					System.out.println("地址："
							+ Integer.toHexString(currentAdress) + ", 值："
							+ value);

					currentAdress++;
				}
				i++;
			}
		} else if (containsSensorData == 0) {// 如果后面没跟着具体采集的数据，则需要从数据库取出数据，然后放在返回字符串
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
						System.out.println("未知寄存器区域");
					}
					String value_str = "";
					value_str = db.excuteWithResult(sql);
					if (value_str.equalsIgnoreCase("false"))
						sub_return_str = sub_return_str + "0";
					else
						sub_return_str = sub_return_str + "1";

					// 如果凑够一个字节了，就将数值存储到结果字符串中
					if (sub_return_str.length() == 8) {
						returnStr = returnStr
								+ Integer.toHexString(Integer.parseInt(
										sub_return_str, 2));
						sub_return_str = "";
					}
					// 地址+1
					currentAdress++;
				}
				// 判断 sub_return_str的长度，长度大于0说明余下几位，需要高位补零，然后存储到返回字符串中
				if (sub_return_str.length() > 0) {
					for (int m = 0; m < 8 - sub_return_str.length(); m++) {
						sub_return_str = "0" + sub_return_str;
					}
					// 将二进制的字符串，转化成16进制字符串
					returnStr = returnStr
							+ Integer.toHexString(Integer.parseInt(
									sub_return_str, 2));
				}
			}
		} else {
			System.out.println("格式异常，“是否包含数据”部分出现0、1以外的值！");
		}
		return i;
	}
	/**
	 * 返回处理数据的字节索引位置（将处理的data字节数组的下标）
	 * 
	 * @param byteIndex
	 *            当前准备处理的字节游标，即将处理的data字节数组的下标
	 * @param data
	 *            当前处理的字节数组
	 * @return
	 */
	private int Read16_Unsign(int byteIndex, String _ID, byte[] data,
			int containsSensorData, boolean needDBOperate) {
		int i = byteIndex;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		int StartAdress = ((data[i] << 8) & 0xFF00);// 数据起始地址
		i++;// 数据处理完后字节数+1
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		StartAdress |= data[i] & 0xFF;
		i++;
		int currentAdress = StartAdress;// 当前数据地址（相当于游标）
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		// 获取传递变量的数量（寄存器的数据量，一个寄存器对应一个变量）
		int var_count = ((data[i] << 8) & 0xFF00);
		i++;
		returnStr = returnStr + NumberUtile.bytes2HexString(data[i]);
		var_count |= data[i] & 0xFF;
		i++;
		if (containsSensorData == 0xFF) {// 如果后面跟着具体采集的数据，则需要将数据解析然后存入数据库
			// 因为是16位无符号类型，每个数据占用2个字节
			for (int m = 0; m < var_count; m++) {
				String value = NumberUtile.bytes2HexString(data[i]);
				i++;
				value = value + NumberUtile.bytes2HexString(data[i]);
				i++;
				// 1-23修改-数据表里面value是double类型，
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
						System.out.println("未知寄存器区域");
					}
					db.excuteNoResult(sql);
				}
				// table.put(currentAdress, value);
				// 修改-把地址转换成16进制字符串输出
				System.out.println("地址：" + Integer.toHexString(currentAdress)
						+ ", 值：" + value);
				currentAdress++;
			}
		} else if (containsSensorData == 0) {// 如果后面跟着具体采集的数据，则需要将数据解析然后存入数据库
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
						System.out.println("未知寄存器区域");
					}
					String value_str = db.excuteWithResult(sql);
					System.out.println("16位无符号，如何从数据库转换过来？未定");
					returnStr = returnStr + value_str;
				}
			}
		} else {
			System.out.println("格式异常，“是否包含数据”部分出现0、1以外的值！");
		}

		return i;
	}

	/**
	 * 返回处理数据的字节索引位置（将处理的data字节数组的下标）
	 * 
	 * @param byteIndex
	 *            当前准备处理的字节游标，即将处理的data字节数组的下标
	 * @param data
	 *            当前处理的字节数组
	 * @return
	 */
	private int Read16_Signed(int byteIndex, String _ID, byte[] data,
			int containsSensorData, boolean needDBOperate) {
		// 先把有符号数按无符号数来处理
		return Read16_Unsign(byteIndex, _ID, data, containsSensorData,
				needDBOperate);

	}
}
