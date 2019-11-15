package com.anbang.p2p.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 前缀为 filepath
 * @Author zenghuikang
 * @Description
 * @Date 2019/11/14 16:38
 * @return
 * @throws
 **/
@Component
@ConfigurationProperties(prefix = "filepath")
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class FilePathConfig {

	private String snapshotFileBasePath;

	private String jFileBasePath;

	public String getSnapshotFileBasePath() {
		return snapshotFileBasePath;
	}

	public void setSnapshotFileBasePath(String snapshotFileBasePath) {
		this.snapshotFileBasePath = snapshotFileBasePath;
	}

	public String getjFileBasePath() {
		return jFileBasePath;
	}

	public void setjFileBasePath(String jFileBasePath) {
		this.jFileBasePath = jFileBasePath;
	}

}
