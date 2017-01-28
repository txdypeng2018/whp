package com.proper.enterprise.isj.user.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.model.enums.CredentialsEnum;

/**
 * 用户扩展信息.
 * Created by think on 2016/8/12 0012.
 */
@Document(collection = "user_info")
public class UserInfoDocument extends BasicInfoDocument {

	/**
	 * 自定义登录名
	 */
	private String loginName;

	/**
	 * 用户Id
	 */
	@Indexed(unique = true)
	private String userId;

	/**
	 * 证件类型
	 */
	private CredentialsEnum credentialsType;

	/**
	 * 证件号码
	 */
	private String credentialsNo;

	/**
	 * 家庭成员
	 */

	private List<FamilyMemberInfoDocument> familyMemberInfo = new ArrayList<>();

	/**
	 * 收藏的医生Id
	 */
	private List<String> doctors = new ArrayList<>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<FamilyMemberInfoDocument> getFamilyMemberInfo() {
		return familyMemberInfo;
	}

	public void setFamilyMemberInfo(List<FamilyMemberInfoDocument> familyMemberInfo) {
		this.familyMemberInfo = familyMemberInfo;
	}

	public List<String> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<String> doctors) {
		this.doctors = doctors;
	}

	public CredentialsEnum getCredentialsType() {
		return credentialsType;
	}

	public void setCredentialsType(CredentialsEnum credentialsType) {
		this.credentialsType = credentialsType;
	}

	public String getCredentialsNo() {
		return credentialsNo;
	}

	public void setCredentialsNo(String credentialsNo) {
		this.credentialsNo = credentialsNo;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
}
