package com.anbang.p2p.init;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.jetty.client.HttpClient;
import org.springframework.context.ApplicationContext;

import com.anbang.p2p.cqrs.c.service.disruptor.CoreSnapshot;
import com.anbang.p2p.cqrs.c.service.disruptor.FileUtil;
import com.anbang.p2p.cqrs.c.service.disruptor.ProcessCoreCommandEventHandler;
import com.anbang.p2p.cqrs.c.service.disruptor.SnapshotJsonUtil;
import com.highto.framework.ddd.Command;
import com.highto.framework.ddd.CommonCommand;
import com.highto.framework.ddd.SingletonEntityRepository;

public class InitProcessor {
	private HttpClient httpClient;

	private HttpClient sslHttpClient;

	private SnapshotJsonUtil snapshotJsonUtil;

	private ProcessCoreCommandEventHandler coreCommandEventHandler;

	private SingletonEntityRepository singletonEntityRepository;

	// @Autowired
	// private MemberDiamondAccountRepository memberDiamondAccountRepository;
	//
	// @Autowired
	// private MemberCashAccountRepository memberCashAccountRepository;

	private ApplicationContext applicationContext;

	FileUtil fileUtil = new FileUtil();

	public InitProcessor(HttpClient httpClient, HttpClient sslHttpClient, SnapshotJsonUtil snapshotJsonUtil,
			ProcessCoreCommandEventHandler coreCommandEventHandler, SingletonEntityRepository singletonEntityRepository,
			ApplicationContext applicationContext) {
		this.httpClient = httpClient;
		this.sslHttpClient = sslHttpClient;
		this.snapshotJsonUtil = snapshotJsonUtil;
		this.coreCommandEventHandler = coreCommandEventHandler;
		this.singletonEntityRepository = singletonEntityRepository;
		this.applicationContext = applicationContext;
	}

	public void init() {

		httpClient.setFollowRedirects(false);
		try {
			httpClient.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		sslHttpClient.setFollowRedirects(false);
		try {
			sslHttpClient.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			//恢复
			recover();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * 恢复
	 * @Author zenghuikang
	 * @Description
	 * @Date 2019/11/14 16:33
	  * @param
	 * @return void
	 * @throws
	 **/
	private void recover() throws Throwable {
		// snapshot 恢复
		// core member snapshot 恢复
		CoreSnapshot memberSnapshot = null;
		try {
			memberSnapshot = (CoreSnapshot) snapshotJsonUtil.recovery(coreCommandEventHandler.getSnapshotFileBasePath(),
					CoreSnapshot.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//会员快照
		if (memberSnapshot != null) {
			//单例实体存储库 填
			singletonEntityRepository.fill(
					(SingletonEntityRepository) memberSnapshot.getContentMap().get(SingletonEntityRepository.class));

			//会员钻石帐户存储库
			// memberDiamondAccountRepository.fill((MemberDiamondAccountRepository)
			// memberSnapshot.getContentMap()
			// .getMap(MemberDiamondAccountRepository.class), null);

			//会员现金帐户存储库
			// memberCashAccountRepository.fill(
			// (MemberCashAccountRepository)
			// memberSnapshot.getContentMap().getMap(MemberCashAccountRepository.class),
			// null);
		}

		// core 命令

		List<Command> commands = fileUtil.read(coreCommandEventHandler.getjFileBasePath());
		invokeCommands(commands);

	}

	/**
	 * 调用命令
	 * @Author zenghuikang
	 * @Description
	 * @Date 2019/11/14 16:41
	  * @param commands
	 * @return void
	 * @throws
	 **/
	private void invokeCommands(List<Command> commands)
			throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (Command command : commands) {
			if (command instanceof CommonCommand) {
				CommonCommand cmd = (CommonCommand) command;
				// List<Object> objects =
				// applicationContext.getBeansOfType(Class.forName(cmd.getType()));
				Class clazz = Class.forName(cmd.getType());
				Object service = applicationContext.getBean(clazz);
				if (cmd.getParameters() != null && cmd.getParameters().length > 0) {
					try {
						service.getClass().getMethod(cmd.getMethod(), cmd.getParameterTypes()).invoke(service,
								cmd.getParameters());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					service.getClass().getMethod(cmd.getMethod()).invoke(service);
				}
			}
		}
	}

}
