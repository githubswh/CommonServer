package NJUPTDataServer.Mina;



import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import NJUPTDataServer.Utile.ConstVar;
import NJUPTDataServer.Utile.NumberUtile;

public class ServerDecode extends CumulativeProtocolDecoder {
	// ��Ҫ����ճ�����ϰ���δ����Ҫ�����ͷβ���������
	public ServerDecode() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
	    System.out.println("������doDecode��");
		int Currentlength = in.remaining();// ��ǰ���ݵĳ���
		if (Currentlength >= 25) {// ��������������Ҫ�ĳ��ȣ����Э��
			in.mark();// ��ǵ�ǰλ�ã��Ա�reset		
			int preID=NumberUtile.byteToInt(in.get(0));
			//��ȡ������Ϣ
			String len_hex=NumberUtile.bytes2HexString(in.get(1))+NumberUtile.bytes2HexString(in.get(2));
			int data_len=Integer.parseInt(len_hex, 16);
			int afterID=NumberUtile.byteToInt(in.get(21));
			int expectLen = data_len + 25;// 
			// �����Ϣ���ݵĳ��Ȳ�����ֱ�ӷ���true
			if (expectLen > Currentlength) {// �����Ϣ���ݲ����������ã��൱�ڲ���ȡsize
				in.reset();
				return false;// ���������ݣ���ƴ�ճ���������
			}

			if (preID==ConstVar.PREID && afterID==ConstVar.AFTERID) {
				byte[] bytes = new byte[expectLen];
				in.reset();
				in.get(bytes);							
				out.write(bytes); // ���е������Ѿ��Ƕ���ͷβ��������
				if (in.remaining() > 0) {// �����ȡ���ݺ�ճ�˰������ø����ٸ��� һ�Σ�������һ�ν���
					return true;
				}
			} else {
				//���ǽ�ȡ�������õģ��ҵ�68H,Ȼ���ӵ�ǰ��ġ�				
				in.reset();
				int temp_int=NumberUtile.byteToInt(in.get());
				while(temp_int!=ConstVar.PREID){
					temp_int=NumberUtile.byteToInt(in.get());
				}
				in.mark();
				return false;// ���������ݣ���ƴ�ճ���������
			}

		}
		return false;// �����ɹ����ø�����н����¸���
	}

}