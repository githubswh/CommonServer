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
		executor = Executors.newFixedThreadPool(5);// 设置有5个线程
		// 创建非阻塞的server端的Socket连接
		// acceptor = new NioSocketAcceptor();
		acceptor = new NioSocketAcceptor(Runtime.getRuntime()
				.availableProcessors() + 1);
	}

	public boolean start() {
		DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
		// 添加编码过滤器 处理接收数据的粘包、断包问题，以及发送数据的加头和尾的问题
		filterChain.addLast("codec", new ProtocolCodecFilter(
				new ServerCodeFactory()));
		// 添加多线程
		filterChain.addLast("executor", new ExecutorFilter(executor));
		// 添加日志过滤器
		LoggingFilter loggingFilter = new LoggingFilter();
		// loggingFilter.
		loggingFilter.setMessageReceivedLogLevel(LogLevel.INFO);
		loggingFilter.setMessageSentLogLevel(LogLevel.INFO);
		filterChain.addLast("loger", loggingFilter);

		DBOperation db=new DBOperation();
		// 设置核心消息业务处理器
		shand = new ServerMessageHandler();
		shand.SetDB(db);
		//修改-先把数据库操作注释
//		if (!OpenDB()) {
//			_state = false;
//			return false;
//		}
		// 设置短信服务地址，用于发短信
		shand.SetMessageIP(messageip);
		acceptor.setHandler(shand);
		// 设置session配置，设置只读缓冲区大小
		// acceptor.getSessionConfig().setReadBufferSize(2048);
		// 设置session配置，30分钟内无操作进入空闲状态
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 18000);// 1800

		try {
			// 绑定端口3456
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
		// 创建数据库连接
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
				StringUtile.printout("请配置数据库!");
				return false;
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			StringUtile.printout("配置文件不存在!");
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
