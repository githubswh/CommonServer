package NJUPTDataServer.Mina;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import NJUPTDataServer.Utile.ConstVar;
import NJUPTDataServer.Utile.IDBOperation;
import NJUPTDataServer.Utile.NumberUtile;

public class Device extends AbstractDevice {
    // 增加状态记录图片上传的索引信息
    int datacount = 0;

    int dataindex = 0;

    byte[] pic = new byte[0];// 需要动态

    // ===============
    private String ServerIP = "202.119.236.11";

    // private String
    // 记录配置信息，主要是地址和地址含义、及数据库字段的对应关系，每个具有唯一ID的设备都有一个配置，也许大部分相同。
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
     *            大功能码
     * @param sub_function
     *            子功能码
     * @param data
     *            数据
     * @param count
     *            上传的数据如果为分段数据的话，count表示总段数(从0开始，0表示共一段)
     * @param index
     *            上传的数据如果为分段数据的话，index表示第几段（从0开始，0表示第一段）
     * @return
     */
    public String dataParser(int functionid, int sub_function, byte[] data,
            int count, int index, long timestamp, String ID) {
        // 根据数据总段数和当前索引值，进行数据存储
        // 1.在接收图片数据时会用到。2.注册码反馈包会用到
        datacount = count;
        dataindex = index;
        Adress adres = new Adress();
        String result = "";
        // 存储解析的数据结果（地址+值）
        // Hashtable values = new Hashtable();
        if (functionid == ConstVar.F_DEVICE_2_SERVER
                || functionid == ConstVar.F_CLIENT_2_SERVER
                || functionid == ConstVar.F_SERVER_2_CLIENT
                || functionid == ConstVar.F_SERVER_2_DEVICE) {
            String sub_result = adres.Parser(sub_function, data, timestamp, ID,
                    true);
            // 根据配置映射信息将数据写到数据库
            // Save_Data(config, values);
            // 返回正确接收的反馈
            result = GetReturnString_common(functionid, count, index, ID,
                    sub_result);
            return result;
        } else if (functionid == ConstVar.F_HEARTBEAT) {
            result = GetReturnString_common(functionid, count, index, ID, "");
            return result;
        } else if (functionid == ConstVar.F_REGISTER) {
            //1-30修改-不需要传递functionid了
            result = GetReturnString_register( count, index, ID, "");
//            result = GetReturnString_register(functionid, count, index, ID, "");
            return result;// 正确反馈;
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
     * @return 因为注册的数据返回，数据属于不同的区域，所以返回两个数据段，返回会话将用两次发送给下位机
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
