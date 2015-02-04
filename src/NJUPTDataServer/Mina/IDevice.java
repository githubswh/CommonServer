package NJUPTDataServer.Mina;

import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public interface IDevice {
    public String GetID();
    public void SetID(String id);
    public String GetPW();
    public void Ini_Schema();
    public String dataParser(int  functionid,int sub_function, byte[] data,
            int count,int index, long timestamp, String ID);
    public void SetOnline();
    public void SetOffline();
    public boolean needServerUpdate();
    public void SetServerUpdate(boolean value);
    /**
     * 
     * @return
     */
    public String GetTransfer();
}
