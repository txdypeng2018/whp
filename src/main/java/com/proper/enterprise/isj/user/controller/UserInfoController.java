package com.proper.enterprise.isj.user.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.document.RegistrationDocument;
import com.proper.enterprise.isj.proxy.service.RegistrationService;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.user.document.UserInfoDocument;
import com.proper.enterprise.isj.user.document.info.BasicInfoDocument;
import com.proper.enterprise.isj.user.document.info.FamilyMemberInfoDocument;
import com.proper.enterprise.isj.user.model.enums.MemberRelationEnum;
import com.proper.enterprise.isj.user.model.enums.SexTypeEnum;
import com.proper.enterprise.isj.user.service.UserInfoService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.user.utils.IdcardUtils;
import com.proper.enterprise.isj.user.utils.MobileNoUtils;
import com.proper.enterprise.platform.api.auth.model.User;
import com.proper.enterprise.platform.api.auth.service.UserService;
import com.proper.enterprise.platform.auth.jwt.model.JWTHeader;
import com.proper.enterprise.platform.auth.jwt.service.JWTService;
import com.proper.enterprise.platform.core.api.ILoggable;
import com.proper.enterprise.platform.core.converter.AESStringConverter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.sort.CNStrComparator;

/**
 * 用户登录相关信息.
 * Created by think on 2016/8/23 0023.
 */

@RestController
@RequestMapping(path = "/user")
public class UserInfoController extends IHosBaseController implements ILoggable {

    @Autowired
    UserInfoService userInfoServiceImpl;

    @Autowired
    UserService userService;

    @Autowired
    JWTService jwtService;

    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;

    @Autowired
    RegistrationService registrationService;

    @RequestMapping(path = "/tokenVal", method = RequestMethod.GET)
    public ResponseEntity<String> tokenVal(HttpServletRequest request) {
        String token = jwtService.getTokenFromHeader(request);
        try {
            boolean flag = this.isTokenVerify(token);
            if (flag) {
                return new ResponseEntity<>("", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOGGER.debug("获得token异常", e);
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }

    }

    /**
     * 获取用户首页信息
     *
     * @param request
     *            用户token
     * @return 用户首页信息
     */
    @RequestMapping(path = "/userInfo", method = RequestMethod.GET)
    public ResponseEntity<UserInfoDocument> getUserInfo(HttpServletRequest request) throws Exception {
        UserInfoDocument userInfo = getLoginUserInfoDocument(request);
        if (userInfo == null) {
            return new ResponseEntity<>(new UserInfoDocument(), HttpStatus.UNAUTHORIZED);
        }
        return responseOfGet(userInfo);
    }

    @RequestMapping(path = "/familyMembers", method = RequestMethod.GET)
    public ResponseEntity<List<FamilyMemberInfoDocument>> getFamilyMembers(HttpServletRequest request)
            throws Exception {
        UserInfoDocument userInfo = this.getLoginUserInfoDocument(request);
        List<FamilyMemberInfoDocument> memberList = new ArrayList<FamilyMemberInfoDocument>();
        if (userInfo != null) {
            FamilyMemberInfoDocument member = new FamilyMemberInfoDocument();
            BeanUtils.copyProperties(userInfo, member);
            member.setMemberRelation(MemberRelationEnum.ME);
            member.setMemberCode(MemberRelationEnum.ME.getValue());
            member.setMember(CenterFunctionUtils.MEMBER_ME);
            member.setAge(IdcardUtils.getAgeByIdCard(member.getIdCard()));
            memberList.add(member);
            List<FamilyMemberInfoDocument> userMemberList = userInfo.getFamilyMemberInfo();
            if (userMemberList != null) {
                for (FamilyMemberInfoDocument familyMemberInfoDocument : userMemberList) {
                    if (StringUtil.isEmpty(familyMemberInfoDocument.getDeleteStatus())) {
                        familyMemberInfoDocument.setAge(IdcardUtils.getAgeByIdCard(familyMemberInfoDocument.getIdCard()));
                        memberList.add(familyMemberInfoDocument);
                    }
                }
            }
        } else {
            return new ResponseEntity<>(memberList, HttpStatus.UNAUTHORIZED);
        }
        return responseOfGet(memberList);
    }

    @RequestMapping(path = "/familyMembers/familyMember", method = RequestMethod.GET)
    public ResponseEntity<FamilyMemberInfoDocument> getFamilyMemberById(HttpServletRequest request) throws Exception {
        String memberId = request.getParameter("memberId");
        UserInfoDocument userInfo = this.getLoginUserInfoDocument(request);
        FamilyMemberInfoDocument tempMember = null;
        if (userInfo != null) {
            List<FamilyMemberInfoDocument> userMemberList = userInfo.getFamilyMemberInfo();
            FamilyMemberInfoDocument member = new FamilyMemberInfoDocument();
            BeanUtils.copyProperties(userInfo, member);
            member.setMemberRelation(MemberRelationEnum.ME);
            member.setMemberCode(MemberRelationEnum.ME.getValue());
            member.setMember(CenterFunctionUtils.MEMBER_ME);
            member.setAge(IdcardUtils.getAgeByIdCard(member.getIdCard()));
            userMemberList.add(member);
            if (StringUtil.isEmpty(memberId)) {
                for (FamilyMemberInfoDocument familyMemberInfoDocument : userMemberList) {
                    if (familyMemberInfoDocument.getPatientVisits().equals(String.valueOf(1))) {
                        tempMember = familyMemberInfoDocument;
                    }
                }
            } else {
                for (FamilyMemberInfoDocument familyMemberInfoDocument : userMemberList) {
                    if (familyMemberInfoDocument.getId().equals(memberId)) {
                        tempMember = familyMemberInfoDocument;
                    }
                }
            }
        } else {
            //noinspection ConstantConditions
            return new ResponseEntity<>(tempMember, HttpStatus.UNAUTHORIZED);
        }
        return responseOfGet(tempMember);
    }

    /**
     * 添加家庭成员
     */
    @RequestMapping(path = "/familyMembers/familyMember", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> addFamilyMember(@RequestBody Map<String, String> userMap, HttpServletRequest request)
            throws Exception {
        String name = userMap.get("name");
        // String sex = userMap.get("sex");
        String sexCode = userMap.get("sexCode");
        String idCard = userMap.get("idCard");
        String phone = userMap.get("phone");
        String memberCode = userMap.get("memberCode");
        String member = userMap.get("member");
        String patientVisits = userMap.get("patientVisits");
        if (StringUtil.isEmpty(name) || StringUtil.isEmpty(sexCode) || StringUtil.isEmpty(idCard)
                || StringUtil.isEmpty(phone) || StringUtil.isEmpty(memberCode) || StringUtil.isEmpty(patientVisits)) {
            return new ResponseEntity<>(CenterFunctionUtils.FORM_DATA_ERR, HttpStatus.BAD_REQUEST);
        }
        idCard = idCard.toUpperCase();
        if (!IdcardUtils.validateCard(idCard)) {
            return new ResponseEntity<>(CenterFunctionUtils.IDCARD_ERROR, HttpStatus.BAD_REQUEST);
        }
        if (IdcardUtils.getGenderByIdCard(idCard).equals("M")) {
            if (!sexCode.equals(String.valueOf(SexTypeEnum.MALE.getCode()))) {
                return new ResponseEntity<>(CenterFunctionUtils.IDCARD_SEX_ERROR, HttpStatus.BAD_REQUEST);
            }

        } else if (IdcardUtils.getGenderByIdCard(idCard).equals("F")) {
            if (!sexCode.equals(String.valueOf(SexTypeEnum.FEMALE.getCode()))) {
                return new ResponseEntity<>(CenterFunctionUtils.IDCARD_SEX_ERROR, HttpStatus.BAD_REQUEST);
            }
        }

        if (!MobileNoUtils.isMobileNo(phone)) {
            return new ResponseEntity<>(CenterFunctionUtils.PHONE_ERROR, HttpStatus.BAD_REQUEST);
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>(CenterFunctionUtils.LOGIN_INVALID_ERR, HttpStatus.BAD_REQUEST);
        }

        UserInfoDocument userInfo = userInfoServiceImpl.getUserInfoByUserId(user.getId());

        List<FamilyMemberInfoDocument> familyList = userInfo.getFamilyMemberInfo();

        int familyMemberSize = 1;
        String lastCreateTime = userInfo.getCreateTime();
        if (familyList != null) {
            familyMemberSize += familyList.size();
            for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                if (StringUtil.isNotEmpty(familyMemberInfoDocument.getCreateTime())) {
                    if (StringUtil.isEmpty(lastCreateTime)
                            || familyMemberInfoDocument.getCreateTime().compareTo(lastCreateTime) > 0) {
                        lastCreateTime = familyMemberInfoDocument.getCreateTime();
                    }
                }
            }
        }
        if (StringUtil.isNotEmpty(lastCreateTime)) {
            int leftIntervalDays = userInfoServiceImpl.getFamilyAddLeftIntervalDays(familyMemberSize, lastCreateTime);
            if (leftIntervalDays > 0) {
                return new ResponseEntity<String>("家庭成员添加次数过多，请" + leftIntervalDays + "天后再添加", HttpStatus.BAD_REQUEST);
            }
        }

        FamilyMemberInfoDocument familyMemberInfoDocument = new FamilyMemberInfoDocument();
        familyMemberInfoDocument.setPhone(phone);
        familyMemberInfoDocument.setPatientVisits(patientVisits);
        familyMemberInfoDocument.setName(name);

        if (String.valueOf(SexTypeEnum.MALE.getCode()).equals(sexCode)) {
            familyMemberInfoDocument.setSex(CenterFunctionUtils.SEX_MALE);
        }
        if (String.valueOf(SexTypeEnum.FEMALE.getCode()).equals(sexCode)) {
            familyMemberInfoDocument.setSex(CenterFunctionUtils.SEX_FEMALE);
        }
        familyMemberInfoDocument.setSexCode(sexCode);
        familyMemberInfoDocument.setMemberCode(memberCode);
        familyMemberInfoDocument.setMember(member);
        familyMemberInfoDocument.setIdCard(idCard);
        List<RegistrationDocument> regList = registrationService.findRegistrationDocumentByCreateUserIdAndPatientIdCard(
                user.getId(), new AESStringConverter().convertToDatabaseColumn(idCard));
        if (regList.size() > 0) {
            familyMemberInfoDocument.setId(regList.get(0).getPatientId());
        } else {
            familyMemberInfoDocument.setId(UUID.randomUUID().toString().substring(0, 32));
        }

        if (familyList == null) {
            familyList = new ArrayList<>();
        }
        if (patientVisits.equals(String.valueOf(1))) {
            userInfo.setPatientVisits(String.valueOf(0));
            for (FamilyMemberInfoDocument memberInfoDocument : familyList) {
                memberInfoDocument.setPatientVisits(String.valueOf(0));
            }
        }
        boolean hasIdCard = false;
        if (userInfo.getIdCard().equals(idCard)) {
            hasIdCard = true;
        } else {
            for (FamilyMemberInfoDocument memberInfoDocument : familyList) {
                if (StringUtil.isEmpty(memberInfoDocument.getDeleteStatus()) && memberInfoDocument.getIdCard().equals(idCard)) {
                    hasIdCard = true;
                }

            }
        }
        if (hasIdCard) {
            return new ResponseEntity<>(CenterFunctionUtils.IDCARD_EXIST, HttpStatus.BAD_REQUEST);
        }
        familyList.add(familyMemberInfoDocument);
        userInfo.setFamilyMemberInfo(familyList);
        userInfoServiceImpl.saveOrUpdateUserInfo(userInfo);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    /**
     * 修改家庭成员
     */
    @RequestMapping(path = "/familyMembers/familyMember", method = RequestMethod.PUT)
    public ResponseEntity<String> modifyFamilyMember(
            @RequestBody Map<String, String> userMap) throws Exception {
        String id = userMap.get("id");
        String name = userMap.get("name");
        String phone = userMap.get("phone");
        String patientVisits = userMap.get("patientVisits");
        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(name) || StringUtil.isEmpty(phone)
                || StringUtil.isEmpty(patientVisits)) {
            return new ResponseEntity<>(CenterFunctionUtils.FORM_DATA_ERR, HttpStatus.BAD_REQUEST);
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>(CenterFunctionUtils.LOGIN_INVALID_ERR, HttpStatus.BAD_REQUEST);
        }
        UserInfoDocument userInfo = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        List<FamilyMemberInfoDocument> familyList = userInfo.getFamilyMemberInfo();
        if (familyList == null) {
            familyList = new ArrayList<>();
        }
        //boolean clearMedicalNumFlag = true;
        if (userInfo.getId().equals(id)) {
            /*
            if (userInfo.getName().equals(name)&&userInfo.getPhone().equals(phone)) {
                clearMedicalNumFlag = false;
            }
            */
            userInfo.setName(name);
            userInfo.setPhone(phone);
            userInfo.setPatientVisits(patientVisits);
            if (patientVisits.equals(String.valueOf(1))) {
                for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                    familyMemberInfoDocument.setPatientVisits(String.valueOf(0));
                }
            }
//            if (clearMedicalNumFlag && StringUtil.isNotEmpty(userInfo.getMedicalNum())) {
//                Map<String, String> hisMedMap = userInfo.getMedicalNumMap();
//                hisMedMap.put(userInfo.getMedicalNum(), DateUtil.toTimestamp(new Date()));
//                userInfo.setMedicalNumMap(hisMedMap);
//                userInfo.setMedicalNum("");
//            }
        } else {
            for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                if (familyMemberInfoDocument.getId().equals(id)) {
                    /*
                    if (familyMemberInfoDocument.getName().equals(name)&&familyMemberInfoDocument.getPhone().equals(phone)) {
                        clearMedicalNumFlag = false;
                    }
                    */
                    familyMemberInfoDocument.setPhone(phone);
                    familyMemberInfoDocument.setName(name);
                    familyMemberInfoDocument.setPatientVisits(patientVisits);
//                    if (clearMedicalNumFlag && StringUtil.isNotEmpty(familyMemberInfoDocument.getMedicalNum())) {
//                        Map<String, String> hisMedMap = familyMemberInfoDocument.getMedicalNumMap();
//                        hisMedMap.put(familyMemberInfoDocument.getMedicalNum(), DateUtil.toTimestamp(new Date()));
//                        familyMemberInfoDocument.setMedicalNumMap(hisMedMap);
//                        familyMemberInfoDocument.setMedicalNum("");
//                    }
                } else {
                    if (patientVisits.equals(String.valueOf(1))) {
                        familyMemberInfoDocument.setPatientVisits(String.valueOf(0));
                    }
                }
            }
            if (patientVisits.equals(String.valueOf(1))) {
                userInfo.setPatientVisits(String.valueOf(0));
            }
        }
        userInfo.setFamilyMemberInfo(familyList);
        userInfoServiceImpl.saveOrUpdateUserInfo(userInfo);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(path = "/familyMembers/familyMember", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFamilyMember(@RequestParam(required = true) String memberId) throws Exception {
        User user = userService.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>(CenterFunctionUtils.LOGIN_INVALID_ERR, HttpStatus.BAD_REQUEST);
        }
        UserInfoDocument userInfo = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        List<FamilyMemberInfoDocument> familyList = userInfo.getFamilyMemberInfo();
        if (familyList != null && familyList.size() > 0) {
            for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                if (familyMemberInfoDocument.getId().equals(memberId)) {
                    familyMemberInfoDocument.setId(UUID.randomUUID().toString().substring(0, 32));
                    familyMemberInfoDocument.setDeleteStatus(String.valueOf(1));
                    if (familyMemberInfoDocument.getPatientVisits().equals(String.valueOf(1))) {
                        userInfo.setPatientVisits(String.valueOf(1));
                    }
                }
            }
            userInfoServiceImpl.saveOrUpdateUserInfo(userInfo);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(path = "/familyMembers/count", method = RequestMethod.GET)
    public ResponseEntity<Integer> getFamilyMemberCount(HttpServletRequest request) throws Exception {
        UserInfoDocument userInfo = this.getLoginUserInfoDocument(request);
        Integer familyMemberCount = 1;
        if (userInfo != null) {
            List<FamilyMemberInfoDocument> familyList = userInfo.getFamilyMemberInfo();
            if (familyList != null && familyList.size() > 0) {
                for (FamilyMemberInfoDocument familyMemberInfoDocument : familyList) {
                    if (StringUtil.isEmpty(familyMemberInfoDocument.getDeleteStatus())) {
                        familyMemberCount++;
                    }
                }
            }
        } else {
            return new ResponseEntity<>(familyMemberCount, HttpStatus.UNAUTHORIZED);
        }
        return responseOfGet(familyMemberCount);
    }

    /**
     * 修改用户信息
     */
    @RequestMapping(path = "/modifyUserInfo", method = RequestMethod.PUT)
    public ResponseEntity<UserInfoDocument> modifyUserInfo(@RequestBody Map<String, String> phoneMap) {
        String userId = phoneMap.get("userId");
        String name = phoneMap.get("name");
        String phoneNo = phoneMap.get("phoneNo");
        UserInfoDocument info = null;
        if (StringUtil.isNotEmpty(userId) && StringUtil.isNotEmpty(name) && StringUtil.isNotEmpty(phoneNo)) {
            info = userInfoServiceImpl.getUserInfoByUserId(userId);
            if (info != null) {
                info.setName(name);
                info.setPhone(phoneNo);
                info = userInfoServiceImpl.saveOrUpdateUserInfo(info);
            }
        }
        return responseOfPut(info);
    }

    @RequestMapping(path = "/collectionDoctors/count", method = RequestMethod.GET)
    public ResponseEntity<Integer> getCollectionDoctorsCount(HttpServletRequest request) throws Exception {
        UserInfoDocument userInfo = this.getLoginUserInfoDocument(request);
        Integer collectionDoctorsCount = 0;
        if (userInfo != null) {
            collectionDoctorsCount = userInfo.getDoctors().size();
        } else {
            return new ResponseEntity<>(collectionDoctorsCount, HttpStatus.UNAUTHORIZED);
        }
        return responseOfGet(collectionDoctorsCount);
    }

    @RequestMapping(path = "/collectionDoctors", method = RequestMethod.GET)
    public ResponseEntity<List<DoctorDocument>> getCollectionDoctors(HttpServletRequest request) throws Exception {
        String searchName = request.getParameter("searchName");
        UserInfoDocument userInfo = this.getLoginUserInfoDocument(request);
        List<DoctorDocument> docList = new ArrayList<>();
        if (userInfo != null) {
            List<String> docIdList = userInfo.getDoctors();
            if (docIdList.size() > 0) {
                Map<String, DoctorDocument> docMap = webServiceCacheUtil.getCacheDoctorDocument();
                Set<String> docIdSet = null;
                if (StringUtil.isNotEmpty(searchName)) {
                    docIdSet = new HashSet<>();
                    Map<String, Set<String>> likeMap = webServiceCacheUtil.getCacheDoctorInfoLike();
                    for (Map.Entry<String, Set<String>> entry : likeMap.entrySet()) {
                        if (entry.getKey().contains(searchName)) {
                            docIdSet.addAll(entry.getValue());
                        }
                    }
                    likeMap.get(searchName);
                }
                for (String docId : docIdList) {
                    if (docMap.containsKey(docId)) {
                        if (docIdSet == null || docIdSet.contains(docId)) {
                            docList.add(docMap.get(docId));
                        }
                    }
                }
            }
        }
        Collections.sort(docList, new Comparator<DoctorDocument>() {
            @Override
            public int compare(DoctorDocument doc1, DoctorDocument doc2) {
                return new CNStrComparator().compare(doc1.getName(), doc2.getName());
            }
        });
        return responseOfGet(docList);
    }

    @RequestMapping(path = "/collectionDoctors/{doctorId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> validateCollectDoctor(@PathVariable String doctorId) throws Exception {
        User user = userService.getCurrentUser();
        UserInfoDocument info = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        String res = "";
        if (info.getDoctors() != null) {
            for (String docId : info.getDoctors()) {
                if (doctorId.equals(docId)) {
                    res = doctorId;
                }
            }
        }
        Map<String, String> docMap = new HashMap<>();
        docMap.put("doctorId", res);
        return responseOfGet(docMap);
    }

    @RequestMapping(path = "/collectionDoctors", method = RequestMethod.POST)
    public void saveCollectDoctor(@RequestBody Map<String, String> doctorIdMap) throws Exception {
        String doctorId = doctorIdMap.get("doctorId");
        User user = userService.getCurrentUser();
        UserInfoDocument info = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        List<String> docList = info.getDoctors();
        if (docList == null) {
            docList = new ArrayList<>();
        }
        boolean flag = false;
        for (String s : docList) {
            if (s.equals(doctorId)) {
                flag = true;
            }
        }
        if (!flag) {
            docList.add(doctorId);
        }
        info.setDoctors(docList);
        userInfoServiceImpl.saveOrUpdateUserInfo(info);
    }

    @RequestMapping(path = "/collectionDoctors/{doctorId}", method = RequestMethod.DELETE)
    public void deleteCollectDoctor(@PathVariable String doctorId) throws Exception {
        User user = userService.getCurrentUser();
        UserInfoDocument info = userInfoServiceImpl.getUserInfoByUserId(user.getId());
        List<String> docList = info.getDoctors();
        if (docList != null) {
            List<String> newList = new ArrayList<>();
            for (String s : docList) {
                if (!s.equals(doctorId)) {
                    newList.add(s);
                }
            }
            info.setDoctors(newList);
            userInfoServiceImpl.saveOrUpdateUserInfo(info);
        }
    }

    /**
     * 获得登录用户.
     *
     * @param request http请求.
     * @return 用户信息.
     * @throws IOException 异常.
     */
    private UserInfoDocument getLoginUserInfoDocument(HttpServletRequest request) throws Exception {
        String token = jwtService.getTokenFromHeader(request);
        boolean tokenVerify = isTokenVerify(token);
        UserInfoDocument userInfo;
        if (tokenVerify) {
            JWTHeader header = jwtService.getHeader(token);
            String userId = header.getId();
            userInfo = userInfoServiceImpl.getUserInfoByUserId(userId);
        } else {
            throw new Exception("登录失效");
        }
        return userInfo;
    }

    /**
     * 检验token.
     *
     * @param token token.
     * @return 检验结果.
     * @throws IOException 异常.
     */
    private boolean isTokenVerify(String token) throws IOException {
        boolean tokenVerify = true;
        try {
            tokenVerify = jwtService.verify(token);
        } catch (IOException e) {
            LOGGER.debug("校验token异常", e);
            throw e;
        }
        return tokenVerify;
    }

    /**
     * 在线建档生成病历号
     * 
     * @param memberIdMap 会员信息.
     * @return 病历号.
     * @throws Exception 异常.
     */
    @RequestMapping(path = "/medicalNum", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> addMedicalNum(@RequestBody Map<String, String> memberIdMap) throws Exception {
        String memberId = memberIdMap.get("memberId");
        if (StringUtil.isEmpty(memberId)) {
            return new ResponseEntity<>(CenterFunctionUtils.FORM_DATA_ERR, HttpStatus.BAD_REQUEST);
        }
        User user = this.userService.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }
        BasicInfoDocument basicInfo = userInfoServiceImpl.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
        if (basicInfo == null) {
            return new ResponseEntity<>(CenterFunctionUtils.PATIENTINFO_GET_ERR, HttpStatus.BAD_REQUEST);
        }
        userInfoServiceImpl.saveOrUpdatePatientMedicalNum(user.getId(), memberId, null);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    /**
     * 更新病历号
     * 
     * @param memberIdMap 会员信息.
     * @return 病历号.
     * @throws Exception 异常.
     */
    @RequestMapping(path = "/medicalNum", method = RequestMethod.PUT, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateMedicalNum(@RequestBody Map<String, String> memberIdMap) throws Exception {
        String memberId = memberIdMap.get("memberId");
        String medicalNum = memberIdMap.get("medicalNum");
        if (StringUtil.isEmpty(memberId) || StringUtil.isEmpty(medicalNum)) {
            return new ResponseEntity<>(CenterFunctionUtils.FORM_DATA_ERR, HttpStatus.BAD_REQUEST);
        }
        User user = this.userService.getCurrentUser();
        if (user == null) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }
        BasicInfoDocument basicInfo = userInfoServiceImpl.getFamilyMemberByUserIdAndMemberId(user.getId(), memberId);
        if (basicInfo == null) {
            return new ResponseEntity<>(CenterFunctionUtils.PATIENTINFO_GET_ERR, HttpStatus.BAD_REQUEST);
        }
        userInfoServiceImpl.saveOrUpdatePatientMedicalNum(user.getId(), memberId, medicalNum);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}