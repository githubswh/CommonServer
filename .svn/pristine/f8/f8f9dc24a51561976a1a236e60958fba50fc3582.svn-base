package NJUPTDataServer.Mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class ServerCodeFactory implements ProtocolCodecFactory {

	public ServerCodeFactory() {
		// TODO Auto-generated constructor stub
	}


	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return new ServerDecode();
	}


	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		return new ServerEncode();
	}

}
