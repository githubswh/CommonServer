package NJUPTDataServer.Mina;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import NJUPTDataServer.Utile.ConstVar;
import NJUPTDataServer.Utile.IDBOperation;
import NJUPTDataServer.Utile.NumberUtile;
import NJUPTDataServer.Utile.StringUtile;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class ServerMessageHandler implements IoHandler {
    private IoBuffer backbuffer = IoBuffer.allocate(100);

    private int connectioncount = 0;

    // private Set<IoSession> AllConnectSessions = Collections
    // .synchronizedSet(new HashSet<IoSession>());
    // private Set<IoSession> AllDeviceSessions = new HashSet<IoSession>();
    private ConcurrentHashMap<Long, IoSession> AllDeviceSessions = new ConcurrentHashMap();

    private ConcurrentHashMap<Long, IoSession> AllSpatialSessions = new ConcurrentHashMap();

    private ConcurrentHashMap<Long, IoSession> AllClientSessions = new ConcurrentHashMap();

    private IDBOperation db = null;

    private String messageip = null;

    public ServerMessageHandler() {
        backbuffer.setAutoShrink(true);

    }

    public void SetMessageIP(String _ipaddress) {
        messageip = _ipaddress;
    }

    public void SetDB(IDBOperation _db) {
        db = _db;
    }

    public void CloseConnect() {
        String sql = "update T_WATTHOUR_METER set F_On_Line= 0  ";
        db.excuteNoResult(sql);
        Iterator<Long> it = AllDeviceSessions.keySet().iterator();
        while (it.hasNext())// for (IoSession ioSession : AllDeviceSessions)
        {
            Long key = it.next();
            IoSession ioSession = AllDeviceSessions.get(key);
            if (ioSession == null)
                continue;
            IDevice device = (IDevice) (ioSession
                    .getAttribute(ConstVar.TAG_DEVICE));
            if (device != null) {
                // StringUtile.printout("׼���ر����ӣ�"+device.GetID());
                // device.SetOffline();
                StringUtile.printout("�豸����:" + device.GetID());
                // ioSession.close(false);
                connectioncount--;
                AllDeviceSessions.remove(ioSession.getId());
                device = null;
                // StringUtile.printout("�豸������:" + connectioncount);
            }
        }

    }

    public void exceptionCaught(IoSession session, Throwable arg1)
            throws Exception {
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        System.out.println("������messageReceived");
        byte[] data = (byte[]) message;
        // ���������Ϣ������־��ʾ
        int count = data.length;
        String show = "";
        for (int i = 0; i < count; i++) {
            show = show + NumberUtile.bytes2HexString(data[i]);
        }
        // ��ȡ�������򳤶�,��Ϊ��һλΪ0x68������������0����1��ʼ
        String data_len_hex = NumberUtile.bytes2HexString(data[1])
                + NumberUtile.bytes2HexString(data[2]);
        int data_len = Integer.parseInt(data_len_hex, 16);
        // ��ȡ������
        int function = NumberUtile.byteToInt(data[3]);
        // int
        // function=Integer.parseInt(NumberUtile.bytes2HexString(data[3]),16);
        // ��ȡ��־λ
        // int from = Integer.parseInt(NumberUtile.bytes2HexString(data[4]), 16)
        // ;
        int from = NumberUtile.byteToInt(data[4]);
        // ��ȡ�豸ID
        String ID = "";
        for (int i = 5; i < 11; i++) {
            byte c = data[i];
            ID = ID + NumberUtile.bytes2HexString(c);// ��λ��ǰ��λ�ں�
        }
        // ��ȡ����
        String PWD = "";
        for (int i = 11; i < 15; i++) {
            byte c1 = data[i];
            PWD = PWD + NumberUtile.bytes2HexString(c1);// ��λ��ǰ��λ�ں�
        }
        System.out.println("���룺" + PWD);
        // ��ȡʱ���
        String time = "";
        for (int i = 15; i < 21; i++) {
            byte c2 = data[i];
            time = time + NumberUtile.bytes2HexString(c2);// ��λ��ǰ��λ�ں�
        }
        long timestamp = Integer.parseInt(time, 16);
        // ��ȡ������num�����ֽڣ���ʾ���ݵ��ܶ�����
        // indexAdr�����ֽڣ���ʾ�������������ݵ�����λ��
        int index = data[22];
        int num = data[23];
        System.out.println("���������ܶ�����" + num + "����λ�ã�" + index);
        if (function == ConstVar.F_HEARTBEAT) {
            returnSubString("", ID, time, from, function, session,
                    index, num, PWD);
        }
        else if (function == ConstVar.F_DEVICE_2_SERVER
                || function == ConstVar.F_SERVER_2_DEVICE
                || function == ConstVar.F_REGISTER
                || function == ConstVar.F_PIC_UPLOAD
                || function == ConstVar.F_PIC_QUERY_FROM_DEVICE) {
            // ����Щʱ��ò���ȡ�õ���ֵΪ�ӹ����룬��Щʱ���ǡ�
        int sub_function = NumberUtile.byteToInt(data[24]);
        byte[] subdata = new byte[data_len - 1];
        System.arraycopy(data, 25, subdata, 0, subdata.length);// ����0x68���ӹ�����
            Device device = (Device) session.getAttribute(ConstVar.TAG_DEVICE);
            if (device == null) {
                IoSession old = GetDeviceSession(ID);
                if (old != null) {
                    old.close(true);
                    StringUtile.printout(ID + "�豸�Ѿ�ע��,ɾ���ɵ�ע��!");
                }
                device = new Device(ID, db, messageip);
                // �޸�-�Ȱ����ݿ����ע��
                // device.SetOnline();
                StringUtile.printout("�豸����messageReceived:" + ID);
                session.setAttribute(ConstVar.TAG_DEVICE, device);
                AllDeviceSessions.put(session.getId(), session);// .add(session.getId()+"",session);
                connectioncount++;
                // StringUtile.printout("�豸������:" + connectioncount);
            }
            String returnString = device.dataParser(function, sub_function,
                    subdata, num, index, timestamp, ID);
            // ���ɷ�����
            System.out.println(returnString);
            returnSubString(returnString, ID, time, from, function, session,
                    index, num, PWD);
        } else if (function == ConstVar.F_CLIENT_2_SERVER) {
            int sub_function = NumberUtile.byteToInt(data[24]);
            byte[] subdata = new byte[data_len - 1];
            System.arraycopy(data, 25, subdata, 0, subdata.length);// ����0x68���ӹ�����
            // �Կͻ����γɷ�������������
            Client cls = new Client(ID, db);
            String returnString = cls.dataParser(function, sub_function,
                    subdata, num, index, timestamp, ID);
            returnSubString(returnString, ID, time, from, function, session,
                    index, count, PWD);
            // ���ۿͻ��˷�ʲô����Ҫת������λ����������Ҫ��������ĳ�0x02���������ֲ���
            IoSession old = GetDeviceSession(ID);
            if (old != null) {
                returnString = "";
                int newfunction = ConstVar.F_SERVER_2_DEVICE;
                for (int i = 0; i < subdata.length; i++) {
                    returnString = returnString
                            + NumberUtile.bytes2HexString(subdata[i]);
                }
                returnSubString(returnString, ID, time, from, newfunction, old,
                        index, count, PWD);
            } else {
                System.out.println(ID + "��λ�������ߣ�");
            }
        } else if (function == ConstVar.F_SERVER_2_CLIENT) {
            // ����˷����ն˵�����ͻ��˵� ����
            System.out.println("F_SERVER_2_CLIENT �ù���δʵ��");
        } else if (function == ConstVar.F_PIC_QUERY_FROM_SERVER) {
            // �ͻ��������ˣ�����ͼƬ��ȡ����
            System.out.println("F_PIC_QUERY_FROM_SERVER �ù���δʵ��");
        } else if (function == ConstVar.F_SPTIAL_START) {
            System.out.println("F_SPTIAL_START �ù���δʵ��");
        } else {
            StringUtile.printout("������Դ����ȷ!");
        }

    }

    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("������sessionClosed��");
        IDevice device = (IDevice) (session.getAttribute(ConstVar.TAG_DEVICE));
        if (device != null) {
            // �޸�-�Ȱ����ݿ����ע��
            // device.SetOffline();
            StringUtile.printout("�豸����sessionClosed:" + device.GetID());
            AllDeviceSessions.remove(session.getId());//
            connectioncount--;
        }
        // StringUtile.printout("���ӹر�:" + session.getId());
    }

    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("������sessionCreated��");
        IDevice device = (IDevice) (session.getAttribute(ConstVar.TAG_DEVICE));
        if (device != null) {
            // �޸�-�Ȱ����ݿ����ע��
            // device.SetOnline();
            StringUtile.printout("�豸����sessionCreated:" + device.GetID());
        }

    }

    public void sessionIdle(IoSession session, IdleStatus arg1)
            throws Exception {
        Device device = (Device) session.getAttribute(ConstVar.TAG_DEVICE);
        if (device != null) {
            // �޸�-�Ȱ����ݿ����ע��
            // device.SetOffline();
            StringUtile.printout("�豸����sessionIdle:" + device.GetID());
        }
        session.close(false);
        // session.close();
    }

    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("����sessionOpened��");
        IDevice device = (IDevice) (session.getAttribute(ConstVar.TAG_DEVICE));
        if (device != null) {
            // �޸�-�Ȱ����ݿ����ע��
            // device.SetOnline();
            StringUtile.printout("�豸����sessionOpened:" + device.GetID());
            // StringUtile.printout("�豸������:" + connectioncount);
        }
    }

    private void SendReturnMessageDirect(IoSession session, byte[] data) {
        backbuffer.clear();
        backbuffer.put(data);
        backbuffer.shrink();
        backbuffer.flip();
        WriteFuture future = session.write(backbuffer);
        // future.awaitUninterruptibly();
        future.join();
        if (future.isWritten()) {
            // The message has been written successfully.
            return;
        } else {
            // The messsage couldn't be written out completely for some reason.
            // (e.g. Connection is closed)
        }
    }

    public void messageSent(IoSession arg0, Object arg1) throws Exception {

    }

    private String byte2bits(byte b) {
        int z = b;
        z |= 256;
        String str = Integer.toBinaryString(z);
        int len = str.length();
        return str.substring(len - 8, len);
    }

    private IoSession GetDeviceSession(String id) {
        Iterator<Long> it = AllDeviceSessions.keySet().iterator();
        while (it.hasNext())// for (IoSession ioSession : AllDeviceSessions)
        {
            Long key = it.next();
            IoSession oldsession = AllDeviceSessions.get(key);

            IDevice device = (IDevice) (oldsession
                    .getAttribute(ConstVar.TAG_DEVICE));
            // System.out.println();
            if (device != null) {
                // String s = device.GetID();
                if (device.GetID().equalsIgnoreCase(id)) {
                    return oldsession;
                }
            }

        }

        // }
        return null;
    }

    // ����˵������޸�����
    private void ModifyAllSession(byte[] subData) {
        Iterator<Long> it = AllDeviceSessions.keySet().iterator();
        while (it.hasNext())// for (IoSession ioSession : AllDeviceSessions)
        {
            Long key = it.next();
            IoSession oldsession = AllDeviceSessions.get(key);
            IDevice device = (IDevice) (oldsession
                    .getAttribute(ConstVar.TAG_DEVICE));
            if (device != null) {
                try {

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        // for (IoSession oldsession : AllDeviceSessions) {}
    }

    // ����˵������޸�����,�޸����µĵ��
    private void ModifyRestSession(byte[] subData) {
        Iterator<Long> it = AllDeviceSessions.keySet().iterator();
        while (it.hasNext())// for (IoSession ioSession : AllDeviceSessions)
        {
            Long key = it.next();
            IoSession oldsession = AllDeviceSessions.get(key);
            IDevice device = (IDevice) (oldsession
                    .getAttribute(ConstVar.TAG_DEVICE));
            if (device != null) {
                try {
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        // for (IoSession oldsession : AllDeviceSessions) {}
    }

    private void messageout(String value, int functionid) {
        String function = "δ֪����";
        /*
         * if (functionid == ConstVar.UPLOAD_TIME1_DATA){function="����1";} else
         * if (functionid == ConstVar.UPLOAD_TIME2_DATA){function="����2";} else
         * if (functionid == ConstVar.READ_CURRENT){function="��ȡ��ǰ";} else if
         * (functionid == ConstVar.READ_FROZEN){function="��ȡ����";} else if
         * (functionid == ConstVar.UPLOAD_FROZEN){function="�ϱ�����";} else if
         * (functionid == ConstVar.BEAT_DATA){function="��������";} else if
         * (functionid == ConstVar.REAGISTER){function="���ע��";} else if
         * (functionid == ConstVar.MODIFY){function="�����޸�";} else if(functionid
         * == ConstVar.MODIFY_BAT){function="�����޸�";} else if (functionid ==
         * ConstVar.SETTIME){function="���Уʱ";} else if (functionid ==
         * ConstVar.DEVICE_STATUS){function="״̬�ϱ�";} else{}
         * StringUtile.printout(function+":"+value);
         */
    }

    /***
     * 
     * @param returnString
     * @param ID
     * @param time
     * @param from
     * @param function
     * @param session
     */
    private void returnSubString(String returnString, String ID, String time,
            int from, int function, IoSession session, int index, int count,
            String PWD) {
        // �õ���������ĳ���
        int return_len = returnString.length()/2;
        // ʱ�����Ҫ�ģ�������Ҫ��
        System.out.println("ʱ�����Ҫ�ģ�������Ҫ��!");
        String returnStr = Integer.toHexString(ConstVar.PREID).toUpperCase()
                + NumberUtile.intToHexStr(return_len, 4).toUpperCase()
                + NumberUtile.intToHexStr(function, 2).toUpperCase()
                + NumberUtile.intToHexStr(from, 2).toUpperCase() + ID + PWD
                + time + Integer.toHexString(ConstVar.AFTERID)
                + NumberUtile.intToHexStr(index, 2).toUpperCase()
                + NumberUtile.intToHexStr(count, 2).toUpperCase()
                + returnString + Integer.toHexString(ConstVar.TAILE);
        byte[] returnByte = NumberUtile.hexStringToByte(returnStr);
        // 1-13�޸�-�鿴returnByte
        System.out.println("�鿴returnByte�����ֵ");
        System.out.println(returnStr);
        if (returnByte != null) {
            SendReturnMessageDirect(session, returnByte);
        } else {
            // StringUtile.printout("not need respond");
        }

    }
}