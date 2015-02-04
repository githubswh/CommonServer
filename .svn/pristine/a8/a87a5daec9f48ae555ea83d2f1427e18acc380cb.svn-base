package NJUPTDataServer.Mina;

import java.sql.ResultSet;
import java.sql.SQLException;

import NJUPTDataServer.Utile.ConstVar;
import NJUPTDataServer.Utile.IDBOperation;
import NJUPTDataServer.Utile.NumberUtile;

public class Client extends AbstractDevice {

    public Client(String _id, IDBOperation _db) {
        super(_id,_db);
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
        String result="";
        Adress adres = new Adress();
        if(data[0]==0xFF) {
            //如果带数据，同时将数据去掉返回给客户端,不能存储在数据库，必须等到下位机修改后返回，将下位机返回的数据存入数据库
            //这样做的目的是防止下位机修改不成功
            String sub_result = adres.Parser(sub_function, data, timestamp, ID,false);
            result=GetReturnString_common(functionid,count,index,ID,sub_result); 
        }
        else {
            //如果不带数据，需要从数据库获取一份数据返回客户端
            String sub_result = adres.Parser(sub_function, data, timestamp, ID,true);
            result=GetReturnString_common(functionid,count,index,ID,sub_result); 
        }
              
        return result=GetReturnString_common(functionid,count,index,ID,result);
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

}
