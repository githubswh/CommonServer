package NJUPTDataServer.Mina;

public class DeviceConfig {
	int[] qu=null;
    int[] dizhis=null;
    String[] names=null;
    String[] Table_Names=null;
    String[] Field_Names=null;
	public DeviceConfig(String id) {
		// ����ID��ѯ���ã�������ַ�����ݱ�������֮���ӳ���ϵ���Ա����ݴ洢
	}
public String Get_TableName_byAdress(int adress){
	return "T1";
}
public String Get_FieldName_byAdress(int adress){
	return "F1";
}
}
