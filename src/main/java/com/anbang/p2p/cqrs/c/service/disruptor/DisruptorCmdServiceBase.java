package com.anbang.p2p.cqrs.c.service.disruptor;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import com.highto.framework.concurrent.DeferredResult;
import com.highto.framework.ddd.CommonCommand;
import com.highto.framework.disruptor.event.CommandEvent;
import com.lmax.disruptor.dsl.Disruptor;

public abstract class DisruptorCmdServiceBase {

	@Autowired
	protected DisruptorFactory disruptorFactory;

	//发布事件
	protected <T> DeferredResult<T> publishEvent(Disruptor<CommandEvent> disruptor, CommonCommand cmd,
			Callable<T> callable) {
		//递延结果
		DeferredResult<T> deferredResult = new DeferredResult<>();
		//发布事件
		disruptor.publishEvent((event, sequence) -> {
			event.setCmd(cmd);

			//ulwfcyvi
			//event.setSnapshot(true);

			//设置事件处理器
			event.setHandler(() -> {
				T returnObj = null;
				try {
					returnObj = callable.call();
					deferredResult.setResult(returnObj);
				} catch (Exception e) {
					e.printStackTrace();
					deferredResult.setExceptionResult(e);
				}
				return null;
			});

		});
		// T result = getResult(deferredResult);
		return deferredResult;
	}

	// private <T> T getResult(DeferredResult<T> deferredResult) {
	// try {
	// return deferredResult.getResult();
	// } catch (Exception e) {
	// return null;
	// }
	// }

}
