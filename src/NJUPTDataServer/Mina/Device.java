package NJUPTDataServer.Mina;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import NJUPTDataServer.Utile.ConstVar;
import NJUPTDataServer.Utile.IDBOperation;
import NJUPTDataServer.Utile.NumberUtile;

public class Device extends AbstractDevice {
    // ����״̬��¼ͼƬ�ϴ���������Ϣ
    int datacount = 0;

    int dataindex = 0;

    byte[] pic = new byte[0];// ��Ҫ��̬

    // ===============
    private String ServerIP = "202.119.236.11";

    // private String
    // ��¼������Ϣ����Ҫ�ǵ�ַ�͵�ַ���塢�����ݿ��ֶεĶ�Ӧ��ϵ��ÿ������ΨһID���豸����һ�����ã�Ҳ��󲿷���ͬ��
    private DeviceConfig config = null;

    public Device(String _id, IDBOperation _db, String _mip) {
        super(_id, _db);
        Ini_Schema();
        config = new DeviceConfig(id);
    }

    public String GetID() {
        return id;
    }

    public void Ini_Schema() {
    }

    public void SetID(String id) {
        this.id = id;
    }

    public void SetOnline() {
        String sql = "";

        db.excuteNoResult(sql);
    }

    public void SetOffline() {
        String sql = "";
        db.excuteNoResult(sql);
    }

    /***
     * 
     * @param functionid
     *            ������
     * @param sub_function
     *            �ӹ�����
     * @param data
     *            ����
     * @param count
     *            �ϴ����������Ϊ�ֶ����ݵĻ���count��ʾ�ܶ���(��0��ʼ��0��ʾ��һ��)
     * @param index
     *            �ϴ����������Ϊ�ֶ����ݵĻ���index��ʾ�ڼ��Σ���0��ʼ��0��ʾ��һ�Σ�
     * @return
     */
    public String dataParser(int functionid, int sub_function, byte[] data,
            int count, int index, long timestamp, String ID) {
        // ���������ܶ����͵�ǰ����ֵ���������ݴ洢
        // 1.�ڽ���ͼƬ����ʱ���õ���2.ע���뷴�������õ�
        datacount = count;
        dataindex = index;
        Adress adres = new Adress();
        String result = "";
        // �洢���������ݽ������ַ+ֵ��
        // Hashtable values = new Hashtable();
        if (functionid == ConstVar.F_DEVICE_2_SERVER
                || functionid == ConstVar.F_CLIENT_2_SERVER
                || functionid == ConstVar.F_SERVER_2_CLIENT
                || functionid == ConstVar.F_SERVER_2_DEVICE) {
            String sub_result = adres.Parser(sub_function, data, timestamp, ID,
                    true);
            // ��������ӳ����Ϣ������д�����ݿ�
            // Save_Data(config, values);
            // ������ȷ���յķ���
            result = GetReturnString_common(functionid, count, index, ID,
                    sub_result);
            return result;
        } else if (functionid == ConstVar.F_HEARTBEAT) {
            result = GetReturnString_common(functionid, count, index, ID, "");
            return result;
        } else if (functionid == ConstVar.F_REGISTER) {
            //1-30�޸�-����Ҫ����functionid��
            result = GetReturnString_register( count, index, ID, "");
//            result = GetReturnString_register(functionid, count, index, ID, "");
            return result;// ��ȷ����;
        } else if (functionid == ConstVar.F_PIC_UPLOAD) {

            return null;
        } else if (functionid == ConstVar.F_SPTIAL_START) {

            return null;
        } else {
            return null;
        }

    }

    /**
     * 
     * @param functionid
     * @return ��Ϊע������ݷ��أ��������ڲ�ͬ���������Է����������ݶΣ����ػỰ�������η��͸���λ��
     */
    public String GetReturnRegisterBytes(int functionid) {
        String result = null;
        try {

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public byte[] GetReturnSetTimeBytes(int functionid) {
        byte[] result = null;
        try {

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public boolean needServerUpdate() {
        return false;
    }

    public void SetServerUpdate(boolean value) {

    }

    @Override
    public String GetTransfer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String GetPW() {
        // TODO Auto-generated method stub
        return null;
    }

}
