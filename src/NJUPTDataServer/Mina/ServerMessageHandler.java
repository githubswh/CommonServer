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
                // StringUtile.printout("准备关闭连接："+device.GetID());
                // device.SetOffline();
                StringUtile.printout("设备离线:" + device.GetID());
                // ioSession.close(false);
                connectioncount--;
                AllDeviceSessions.remove(ioSession.getId());
                device = null;
                // StringUtile.printout("设备连接数:" + connectioncount);
            }
        }

    }

    public void exceptionCaught(IoSession session, Throwable arg1)
            throws Exception {
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        System.out.println("调用了messageReceived");
        byte[] data = (byte[]) message;
        // 输出本条信息用于日志显示
        int count = data.length;
        String show = "";
        for (int i = 0; i < count; i++) {
            show = show + NumberUtile.bytes2HexString(data[i]);
        }
        // 获取数据区域长度,因为第一位为0x68所以跳过索引0，从1开始
        String data_len_hex = NumberUtile.bytes2HexString(data[1])
                + NumberUtile.bytes2HexString(data[2]);
        int data_len = Integer.parseInt(data_len_hex, 16);
        // 获取功能码
        int function = NumberUtile.byteToInt(data[3]);
        // int
        // function=Integer.parseInt(NumberUtile.bytes2HexString(data[3]),16);
        // 获取标志位
        // int from = Integer.parseInt(NumberUtile.bytes2HexString(data[4]), 16)
        // ;
        int from = NumberUtile.byteToInt(data[4]);
        // 获取设备ID
        String ID = "";
        for (int i = 5; i < 11; i++) {
            byte c = data[i];
            ID = ID + NumberUtile.bytes2HexString(c);// 高位在前低位在后
        }
        // 获取密码
        String PWD = "";
        for (int i = 11; i < 15; i++) {
            byte c1 = data[i];
            PWD = PWD + NumberUtile.bytes2HexString(c1);// 高位在前低位在后
        }
        System.out.println("密码：" + PWD);
        // 获取时间戳
        String time = "";
        for (int i = 15; i < 21; i++) {
            byte c2 = data[i];
            time = time + NumberUtile.bytes2HexString(c2);// 高位在前低位在后
        }
        long timestamp = Integer.parseInt(time, 16);
        // 获取索引，num（低字节）表示数据的总段数，
        // indexAdr（高字节）表示数据在所有数据的索引位置
        int index = data[22];
        int num = data[23];
        System.out.println("数据索引总段数：" + num + "索引位置：" + index);
        if (function == ConstVar.F_HEARTBEAT) {
            returnSubString("", ID, time, from, function, session,
                    index, num, PWD);
        }
        else if (function == ConstVar.F_DEVICE_2_SERVER
                || function == ConstVar.F_SERVER_2_DEVICE
                || function == ConstVar.F_REGISTER
                || function == ConstVar.F_PIC_UPLOAD
                || function == ConstVar.F_PIC_QUERY_FROM_DEVICE) {
            // 在有些时候该步骤取得的数值为子功能码，有些时候不是。
        int sub_function = NumberUtile.byteToInt(data[24]);
        byte[] subdata = new byte[data_len - 1];
        System.arraycopy(data, 25, subdata, 0, subdata.length);// 跳过0x68和子功能码
            Device device = (Device) session.getAttribute(ConstVar.TAG_DEVICE);
            if (device == null) {
                IoSession old = GetDeviceSession(ID);
                if (old != null) {
                    old.close(true);
                    StringUtile.printout(ID + "设备已经注册,删除旧的注册!");
                }
                device = new Device(ID, db, messageip);
                // 修改-先把数据库操作注释
                // device.SetOnline();
                StringUtile.printout("设备在线messageReceived:" + ID);
                session.setAttribute(ConstVar.TAG_DEVICE, device);
                AllDeviceSessions.put(session.getId(), session);// .add(session.getId()+"",session);
                connectioncount++;
                // StringUtile.printout("设备连接数:" + connectioncount);
            }
            String returnString = device.dataParser(function, sub_function,
                    subdata, num, index, timestamp, ID);
            // 生成反馈包
            System.out.println(returnString);
            returnSubString(returnString, ID, time, from, function, session,
                    index, num, PWD);
        } else if (function == ConstVar.F_CLIENT_2_SERVER) {
            int sub_function = NumberUtile.byteToInt(data[24]);
            byte[] subdata = new byte[data_len - 1];
            System.arraycopy(data, 25, subdata, 0, subdata.length);// 跳过0x68和子功能码
            // 对客户端形成反馈包，并返回
            Client cls = new Client(ID, db);
            String returnString = cls.dataParser(function, sub_function,
                    subdata, num, index, timestamp, ID);
            returnSubString(returnString, ID, time, from, function, session,
                    index, count, PWD);
            // 无论客户端发什么，都要转发给下位机，不过需要将大功能码改成0x02，其他部分不变
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
                System.out.println(ID + "下位机不在线！");
            }
        } else if (function == ConstVar.F_SERVER_2_CLIENT) {
            // 服务端发给终端的命令，客户端的 返回
            System.out.println("F_SERVER_2_CLIENT 该功能未实现");
        } else if (function == ConstVar.F_PIC_QUERY_FROM_SERVER) {
            // 客户端向服务端，发送图片获取命令
            System.out.println("F_PIC_QUERY_FROM_SERVER 该功能未实现");
        } else if (function == ConstVar.F_SPTIAL_START) {
            System.out.println("F_SPTIAL_START 该功能未实现");
        } else {
            StringUtile.printout("请求来源不明确!");
        }

    }

    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("调用了sessionClosed！");
        IDevice device = (IDevice) (session.getAttribute(ConstVar.TAG_DEVICE));
        if (device != null) {
            // 修改-先把数据库操作注释
            // device.SetOffline();
            StringUtile.printout("设备离线sessionClosed:" + device.GetID());
            AllDeviceSessions.remove(session.getId());//
            connectioncount--;
        }
        // StringUtile.printout("连接关闭:" + session.getId());
    }

    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("调用了sessionCreated！");
        IDevice device = (IDevice) (session.getAttribute(ConstVar.TAG_DEVICE));
        if (device != null) {
            // 修改-先把数据库操作注释
            // device.SetOnline();
            StringUtile.printout("设备在线sessionCreated:" + device.GetID());
        }

    }

    public void sessionIdle(IoSession session, IdleStatus arg1)
            throws Exception {
        Device device = (Device) session.getAttribute(ConstVar.TAG_DEVICE);
        if (device != null) {
            // 修改-先把数据库操作注释
            // device.SetOffline();
            StringUtile.printout("设备离线sessionIdle:" + device.GetID());
        }
        session.close(false);
        // session.close();
    }

    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("调用sessionOpened！");
        IDevice device = (IDevice) (session.getAttribute(ConstVar.TAG_DEVICE));
        if (device != null) {
            // 修改-先把数据库操作注释
            // device.SetOnline();
            StringUtile.printout("设备在线sessionOpened:" + device.GetID());
            // StringUtile.printout("设备连接数:" + connectioncount);
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

    // 服务端的批量修改命令
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

    // 服务端的批量修改命令,修改余下的电表
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
        String function = "未知类型";
        /*
         * if (functionid == ConstVar.UPLOAD_TIME1_DATA){function="周期1";} else
         * if (functionid == ConstVar.UPLOAD_TIME2_DATA){function="周期2";} else
         * if (functionid == ConstVar.READ_CURRENT){function="读取当前";} else if
         * (functionid == ConstVar.READ_FROZEN){function="读取冻结";} else if
         * (functionid == ConstVar.UPLOAD_FROZEN){function="上报冻结";} else if
         * (functionid == ConstVar.BEAT_DATA){function="心跳数据";} else if
         * (functionid == ConstVar.REAGISTER){function="电表注册";} else if
         * (functionid == ConstVar.MODIFY){function="单独修改";} else if(functionid
         * == ConstVar.MODIFY_BAT){function="批量修改";} else if (functionid ==
         * ConstVar.SETTIME){function="电表校时";} else if (functionid ==
         * ConstVar.DEVICE_STATUS){function="状态上报";} else{}
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
        // 得到数据区域的长度
        int return_len = returnString.length()/2;
        // 时间戳需要改，索引需要改
        System.out.println("时间戳需要改，索引需要改!");
        String returnStr = Integer.toHexString(ConstVar.PREID).toUpperCase()
                + NumberUtile.intToHexStr(return_len, 4).toUpperCase()
                + NumberUtile.intToHexStr(function, 2).toUpperCase()
                + NumberUtile.intToHexStr(from, 2).toUpperCase() + ID + PWD
                + time + Integer.toHexString(ConstVar.AFTERID)
                + NumberUtile.intToHexStr(index, 2).toUpperCase()
                + NumberUtile.intToHexStr(count, 2).toUpperCase()
                + returnString + Integer.toHexString(ConstVar.TAILE);
        byte[] returnByte = NumberUtile.hexStringToByte(returnStr);
        // 1-13修改-查看returnByte
        System.out.println("查看returnByte的输出值");
        System.out.println(returnStr);
        if (returnByte != null) {
            SendReturnMessageDirect(session, returnByte);
        } else {
            // StringUtile.printout("not need respond");
        }

    }
}
