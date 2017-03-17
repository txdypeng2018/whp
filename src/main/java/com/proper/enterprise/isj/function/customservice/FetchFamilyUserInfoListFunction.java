package com.proper.enterprise.isj.function.customservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.platform.core.api.IFunction;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.converter.AESStringConverter;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * 旧的com.proper.enterprise.isj.proxy.service.impl.HospitalNavigationServiceImpl.getDisList()
 * 
 * @author 王东石<wangdongshi@propersoft.cn>
 */
@Service
public class FetchFamilyUserInfoListFunction implements IFunction<List<UserInfoDocument>>, ILoggable {

    @Autowired
    MongoTemplate mongoTemplate;

    /*
     * (non-Javadoc)
     * @see
     * com.proper.enterprise.isj.function.Function#execute(java.lang.Object[])
     */
    @Override
    public List<UserInfoDocument> execute(Object... params) throws Exception {
        return getFamilyUserInfoList((String) params[0], (String) params[1], (String) params[2], (String) params[3]);
    }

    public List<UserInfoDocument> getFamilyUserInfoList(String name, String medicalNum, String phone, String idCard) {
        AESStringConverter converter = new AESStringConverter();
        String nameEn = converter.convertToDatabaseColumn(name);
        String phoneEn = converter.convertToDatabaseColumn(phone);
        String idCardEn = converter.convertToDatabaseColumn(idCard);
        Query query = new Query();
        if (StringUtil.isNotEmpty(phone)) {
            query.addCriteria(Criteria.where("familyMemberInfo").elemMatch(
                    Criteria.where("name").is(nameEn).and("medicalNum").is(medicalNum).and("phone").is(phoneEn)));
        } else {
            query.addCriteria(Criteria.where("familyMemberInfo").elemMatch(
                    Criteria.where("name").is(nameEn).and("medicalNum").is(medicalNum).and("idCard").is(idCardEn)));
        }
        return mongoTemplate.find(query, UserInfoDocument.class);
    }

}