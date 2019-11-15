package com.anbang.p2p.cqrs.c.service.disruptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreSnapshotService {

	@Autowired
	private DisruptorFactory disruptorFactory;

	public void makeSnapshot() {
		//发布事件
		disruptorFactory.getCoreCmdDisruptor().publishEvent((event, sequence) -> event.setSnapshot(true));
	}

}