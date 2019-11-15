package com.anbang.p2p.cqrs.q.dao;

import com.anbang.p2p.cqrs.q.dbo.UserBaseInfo;

public interface UserBaseInfoDao {

	void save(UserBaseInfo info);

	UserBaseInfo findById(String userId);
}
