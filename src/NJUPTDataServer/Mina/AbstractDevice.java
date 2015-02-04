package NJUPTDataServer.Mina;

import java.sql.ResultSet;
import java.sql.SQLException;

import NJUPTDataServer.Utile.ConstVar;
import NJUPTDataServer.Utile.DBOperation;
import NJUPTDataServer.Utile.IDBOperation;
import NJUPTDataServer.Utile.NumberUtile;

public abstract class AbstractDevice implements IDevice {
    protected IDBOperation db = null;

    protected String id = "FFFFFFFFFFFF";// ����Ϊ6���ֽڣ�16���Ʊ��

    public AbstractDevice(String _id, IDBOperation _db) {
        id = _id;
        db = _db;

    }

    @Override
    public String GetID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void SetID(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public String GetPW() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void Ini_Schema() {
        // TODO Auto-generated method stub

    }

    @Override
    public String dataParser(int functionid, int sub_function, byte[] data,
            int count, int index, long timestamp, String ID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void SetOnline() {
        // TODO Auto-generated method stub

    }

    @Override
    public void SetOffline() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean needServerUpdate() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void SetServerUpdate(boolean value) {
        // TODO Auto-generated method stub

    }

    @Override
    public String GetTransfer() {
        // TODO Auto-generated method stub
        return null;
    }

    protected String GetReturnString_common(int functionid, int lowcount,
            int highindex, String ID, String return_subData) {
        String result = "";
        try {
            // 1-16�޸�-����ͨ��Э�鷵��
            if (functionid == ConstVar.F_DEVICE_2_SERVER) {
                // ��������Ϊ1��������������Ϊ��
                result = "";
                return result;
            } else if (functionid == ConstVar.F_SERVER_2_DEVICE)
                result = "";
            else if (functionid == ConstVar.F_HEARTBEAT)
                // �����ķ�����������Ϊ��
                result = "";
            // else if (functionid == ConstVar.F_REGISTER) {
            // // 1-24�޸�-04�����루ע�Ṧ���룩������0����4��������
            // // ����Э��0������1�η��أ�4�����ݷ�5�η���
            // if (lowcount == 5 & highindex == 0) {
            // result = result + "0F" + "FF";
            // String sql = "select config_address from sys_sensor "
            // + "where device_id in (select id from sys_device "
            // + "where number='" + ID
            // + "') and order by config_address ASC";
            // result = result + funcZero(sql);
            // } else if (lowcount == 5 & highindex == 1) {
            // result = result + "10" + "FF";
            // // ���أ��ӹ�����+�Ƿ�����ֵ����+��ʼ��ַ+�Ĵ�������+����ֵ����
            // // sql�����Ҫ�޸ģ���ַ�ĳ�int����
            // String s1 = "select address,value from sys_register_four "
            // + "where address<=" + 0x021F + " and address>="
            // + 0x0100 + " " + "and '" + ID
            // + "'=device_number order by address ASC";
            // result = result + getByteValue(s1);
            // } else if (lowcount == 5 & highindex == 2) {
            // result = result + "10" + "FF";
            // String s2 = "select address,value from sys_register_four "
            // + "where address<=" + 0x033F + " and address>="
            // + 0x0220 + " " + "and '" + ID
            // + "'=device_number order by address ASC";
            // result = result + getByteValue(s2);
            // } else if (lowcount == 5 & highindex == 3) {
            // result = result + "10" + "FF";
            // String s3 = "select address,value from sys_register_four "
            // + "where address<=" + 0x06FF + " and address>="
            // + 0x0580 + " " + "and '" + ID
            // + "'=device_number order by address ASC";
            // result = result + getByteValue(s3);
            // } else if (lowcount == 5 & highindex == 4) {
            // result = result + "10" + "FF";
            // String s4 = "select address,value from sys_register_four "
            // + "where address<=" + 0x087F + " and address>="
            // + 0x0700 + " " + "and '" + ID + "'=device_number";
            // result = result + getByteValue(s4);
            // } else if (lowcount == 5 & highindex == 5) {
            // result = result + "10" + "FF";
            // String s5 = "select address,value from sys_register_four "
            // + "where address<=" + 0x090F + " and address>="
            // + 0x0900 + " " + "and '" + ID + "'=device_number";
            // result = result + getByteValue(s5);
            // }
            // }
            else if (functionid == ConstVar.F_PIC_UPLOAD)
                result = "";
            else if (functionid == ConstVar.F_CLIENT_2_SERVER)
                result = "";
            else if (functionid == ConstVar.F_SERVER_2_CLIENT)
                result = "";
            else if (functionid == ConstVar.F_SPTIAL_START)
                result = "";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    protected String funcZero(String sql) {
        String result0 = "";
        ResultSet rs = null;
        int part_address = -1;
        int part_len = 0;
        // String part_value = "";
        rs = db.excuteWithSetResult(sql);
        // int mm = 0;
        try {
            boolean flag = true;
            int address = -1;
            while (flag) {
                if (rs.next()) {
                    flag = true;
                } else {
                    flag = false;
                }
                if (flag == true) {
                    address = rs.getInt(1);
                }
                if (part_len == 0) {
                    part_address = address;
                    part_len = 1;
                    // part_len++;
                    // part_value = "1";
                } else if (address == part_address + part_len) {
                    part_len++;
                } else {
                    // ��������
                    result0 = result0
                            + NumberUtile.intToHexStr(part_address, 4)
                            + NumberUtile.intToHexStr(part_len, 4);
                    // result0 = result0 + part_address + "" + part_len;
                    String value = "";
                    String Hex_value = "";
                    for (int ss = 0; ss < 8 - (part_len % 8); ss++) {
                        value = value + "0";
                    }
                    for (int ss = 0; ss < part_len; ss++) {
                        value = value + "1";
                    }
                    Hex_value = Hex_value
                            + NumberUtile.BinaryStrToHexString(value, 2);
                    result0 = result0 + Hex_value;
                    part_address = address;
                    part_len = 1;

                    // part_value = "1";
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result0;
    }

    protected String getByteValue(String sql) {
        String result4 = "";
        String Str_value = "";
        String value = null;
        ResultSet rs = null;
        int part_address = 0;
        int part_len = 0;
        int part_value = 0;
        rs = db.excuteWithSetResult(sql);
        try {
            boolean flag = true;
            int address = -1;
            while (flag) {
                if (rs.next()) {
                    flag = true;
                } else {
                    flag = false;
                }
                if (flag == true) {
                    address = rs.getInt(1);
                    part_value = rs.getInt(2);
                    value = NumberUtile.intToHexStr(part_value, 4);
                }
                if (part_len == 0) {
                    part_address = address;
                    part_len = 1;
                    Str_value = Str_value + value;
                } else if (address == part_address + 1) {
                    part_len++;
                    Str_value = Str_value + value;
                } else {
                    result4 = result4
                            + NumberUtile.intToHexStr(part_address, 4)
                            + NumberUtile.intToHexStr(part_len, 4) + Str_value;
                    part_address = address;
                    part_len = 1;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result4;
    }

    // public String GetReturnString_register(int functionid, int lowcount,
    // int highindex, String ID, String return_subData) {
    // �������ֻƴע�ᷴ����
    public String GetReturnString_register( int lowcount,
          int highindex, String ID, String return_subData) {
        String result = "";
        try {
//            // 1-16�޸�-����ͨ��Э�鷵��
//            if (functionid == ConstVar.F_DEVICE_2_SERVER) {
//                // ��������Ϊ1��������������Ϊ��
//                result = "";
//                return result;
//            } else if (functionid == ConstVar.F_SERVER_2_DEVICE)
//                result = "";
//            else if (functionid == ConstVar.F_HEARTBEAT)
//                // �����ķ�����������Ϊ��
//                result = "";
//            else if (functionid == ConstVar.F_REGISTER) {
                // 1-24�޸�-04�����루ע�Ṧ���룩������0����4��������
                // ����Э��0������1�η��أ�4�����ݷ�5�η���
                if (lowcount == 5 & highindex == 0) {
                    result = result + "0F" + "FF";
                    String sql = "select config_address from sys_sensor "
                            + "where device_id in (select id from sys_device "
                            + "where number='" + ID
                            + "') and del_flag = '0' order by config_address ASC";
                    result = result + funcZero(sql);
                } else if (lowcount == 5 & highindex == 1) {
                    result = result + "10" + "FF";
                    // ���أ��ӹ�����+�Ƿ�����ֵ����+��ʼ��ַ+�Ĵ�������+����ֵ����
                    // sql�����Ҫ�޸ģ���ַ�ĳ�int����
                    String s1 = "select address,value from sys_register_four "
                            + "where address<=" + 0x021F + " and address>="
                            + 0x0100 + " " + "and '" + ID
                            + "'=device_number and del_flag = '0' " 
                            + "order by address ASC";
                    result = result + getByteValue(s1);
                } else if (lowcount == 5 & highindex == 2) {
                    result = result + "10" + "FF";
                    String s2 = "select address,value from sys_register_four "
                            + "where address<=" + 0x033F + " and address>="
                            + 0x0220 + " " + "and '" + ID
                            + "'=device_number and del_flag = '0' " 
                            + "order by address ASC";
                    result = result + getByteValue(s2);
                } else if (lowcount == 5 & highindex == 3) {
                    result = result + "10" + "FF";
                    String s3 = "select address,value from sys_register_four "
                            + "where address<=" + 0x06FF + " and address>="
                            + 0x0580 + " " + "and '" + ID
                            + "'=device_number and del_flag = '0'" 
                            + "order by address ASC";
                    result = result + getByteValue(s3);
                } else if (lowcount == 5 & highindex == 4) {
                    result = result + "10" + "FF";
                    String s4 = "select address,value from sys_register_four "
                            + "where address<=" + 0x087F + " and address>="
                            + 0x0700 + " " + "and '" + ID + "'=device_number " 
                            + "and del_flag = '0' order by address ASC";
                    result = result + getByteValue(s4);
                } else if (lowcount == 5 & highindex == 5) {
                    result = result + "10" + "FF";
                    //4��������IP,�˿ڣ���׼ʱ�䣬�ϱ�ʱ�䣬����ʱ�䣬ID�����붼��sys_device��������
                    String s5 = "select server_ip,server_port,standard_time,report_period,valid_time,number,PWD" +
                    		" from sys_device where number = '"+ID+"' and del_flag = '0'";
                    ResultSet rs = db.excuteWithSetResult(s5);
                    while(rs.next()) {
                        String ip = rs.getString(1);
                        int port = rs.getInt(2);
                        String standard_time = rs.getString(3);
                        String report_period = rs.getString(4);
                        int valid_time = rs.getInt(5);
                        String number = rs.getString(6);
                        String PWD = rs.getString(7);
                        long date = System.currentTimeMillis();
                        //ipҪд��16�����ַ���ռ4���ֽڣ���ôд======================
                        //portռ2���ֽڣ���Ҫ�޸�==========================
                        //��׼ʱ��д��16�����ַ���ռ4���ֽڣ���ôд====================
                        //�ϱ�����ռ4���ֽ�============================
                        //��������ռ2���ֽ�=============================
                        //IDռ6���ֽڣ�����Ҫ�޸�
                        //����ռ4���ֽڣ������ݿ�ȡ���ľ���16�����ַ���������Ҫ�޸�
                        //Уʱ====================================
                        result = result + ip + Integer.toHexString(port) + standard_time
                                + report_period + Integer.toHexString(valid_time) + number + PWD + NumberUtile.longToHexStr(date, 4);
                    }
                   
                    
                    
                    
//                    String s5 = "select address,value from sys_register_four "
//                            + "where address<=" + 0x090F + " and address>="
//                            + 0x0900 + " " + "and '" + ID + "'=device_number";
//                    result = result + getByteValue(s5);
                }
//            } else if (functionid == ConstVar.F_PIC_UPLOAD)
//                result = "";
//            else if (functionid == ConstVar.F_CLIENT_2_SERVER)
//                result = "";
//            else if (functionid == ConstVar.F_SERVER_2_CLIENT)
//                result = "";
//            else if (functionid == ConstVar.F_SPTIAL_START)
//                result = "";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
