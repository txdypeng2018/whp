package com.proper.enterprise.isj.user.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.annotation.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoDatabase;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.CardInfoDocument;
import com.proper.enterprise.isj.user.model.enums.CardTypeEnum;
import com.proper.enterprise.platform.test.AbstractTest;
import com.proper.enterprise.platform.test.utils.JSONUtil;

/**
 * Created by think on 2016/8/12 0012. 测试用户保存
 */
public class UserInfoRepositoryTest extends AbstractTest {

	@Autowired
	UserInfoRepository userInfoRepository;

	@Autowired
	MongoDatabase mongoDatabase;

	@Autowired
	MongoTemplate mongoTemplate;

	@Test
	public void saveUser() {
		// UserInfo userInfo =
		// userInfoRepository.getByUserId(String.valueOf(1));
		UserInfoDocument userInfo = new UserInfoDocument();
		userInfo.setUserId(String.valueOf(1));
		userInfo.setName("测试1");
		userInfo.setIdCard("1222");
		List<CardInfoDocument> cardList = new ArrayList<>();
		CardInfoDocument cardInfo = new CardInfoDocument();
		cardInfo.setCardType(CardTypeEnum.CARD);
		cardInfo.setCardNo("11111111111");
		cardList.add(cardInfo);
		userInfo.setCards(cardList);
		userInfoRepository.save(userInfo);
	}

	@Test
	public void getUserInfoByUserId() throws Exception{
		UserInfoDocument userInfo = userInfoRepository.getByUserId(String.valueOf(1));
		if (userInfo != null) {
			try {
				JSONUtil.toJSON(userInfo);
			} catch (IOException e) {
				throw e;
			}
		}
	}

	@Test
	@After("getUserInfoByUserId()")
	public void deleteData() {
		mongoDatabase.drop();
	}
}