package NJUPTDataServer.Mina;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import NJUPTDataServer.Utile.DBOperation;
import NJUPTDataServer.Utile.StringUtile;

public class MinaServer {
	private int PORT = 8001;
	private SocketAcceptor acceptor;
	private ExecutorService executor;
	private boolean _state = false;
	private ServerMessageHandler shand = null;
	private String messageip = "";

	public MinaServer() {
		executor = Executors.newFixedThreadPool(5);// ������5���߳�
		// ������������server�˵�Socket����
		// acceptor = new NioSocketAcceptor();
		acceptor = new NioSocketAcceptor(Runtime.getRuntime()
				.availableProcessors() + 1);
	}

	public boolean start() {
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		// ��ӱ�������� ����������ݵ�ճ�����ϰ����⣬�Լ��������ݵļ�ͷ��β������
		filterChain.addLast("codec", new ProtocolCodecFilter(
				new ServerCodeFactory()));
		// ��Ӷ��߳�
		filterChain.addLast("executor", new ExecutorFilter(executor));
		// �����־������
		LoggingFilter loggingFilter = new LoggingFilter();
		// loggingFilter.
		loggingFilter.setMessageReceivedLogLevel(LogLevel.INFO);
		loggingFilter.setMessageSentLogLevel(LogLevel.INFO);
		filterChain.addLast("loger", loggingFilter);

		DBOperation db=new DBOperation();
		// ���ú�����Ϣҵ������
		shand = new ServerMessageHandler();
		shand.SetDB(db);
		//�޸�-�Ȱ����ݿ����ע��
//		if (!OpenDB()) {
//			_state = false;
//			return false;
//		}
		// ���ö��ŷ����ַ�����ڷ�����
		shand.SetMessageIP(messageip);
		acceptor.setHandler(shand);
		// ����session���ã�����ֻ����������С
		// acceptor.getSessionConfig().setReadBufferSize(2048);
		// ����session���ã�30�������޲����������״̬
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 18000);// 1800

		try {
			// �󶨶˿�3456
			// InetSocketAddress socketAddres = new
			// InetSocketAddress(InetAddress.getLocalHost(),PORT);
			acceptor.bind(new InetSocketAddress(PORT));
		} catch (IOException e) {
			e.printStackTrace();
			CloseDB();
			// db=null;
			return false;
		}
		_state = true;
		return true;
	}

	private boolean OpenDB() {
		// �������ݿ�����
		try {
			InputStream is = new FileInputStream("dbconfig.xml");
			Properties p = new Properties();
			p.loadFromXML(is);
			is.close();
			String IP = p.getProperty("IP");
			String Port = p.getProperty("Port");
			String SID = p.getProperty("SID");
			String User = p.getProperty("User");
			String PW = p.getProperty("PassWord");
			messageip = p.getProperty("MessageIP");
			if ((IP == null) || (Port == null) || (SID == null)
					|| (User == null) || (PW == null) || (messageip == null)) {
				StringUtile.printout("���������ݿ�!");
				return false;
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			StringUtile.printout("�����ļ�������!");
			return false;
		}
		return true;
	}
	private void CloseDB() {

	}
	public boolean Getstate() {
		return _state;
	}

	public void stop() {
		CloseDB();
	}

	public void CloseConnection() {
		if (shand != null) {
			shand.CloseConnect();
			acceptor.unbind();
			shand = null;
		}
	}


}
