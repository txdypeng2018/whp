package com.proper.enterprise.isj.proxy.controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.exception.HisReturnException;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.DoctorService;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4FileCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.isj.webservices.model.enmus.DeptLevel;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.StringUtil;

/**
 * Created by think on 2016/8/16 0016. 查询全院医生列表
 */
@RestController
@RequestMapping(path = "/doctors")
@JWTIgnore
public class DoctorController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorController.class);

    @Autowired
    DoctorService doctorService;

    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    WebService4FileCacheUtil webService4FileCacheUtil;

    /**
     * 查询全院医生列表
     *
     * @param searchName
     *            全文检索条件
     * @param subjectId
     *            学科ID
     * @return 医生列表
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, String>>> getDoctors(Integer pageNo, String searchName, String subjectId) {
        List<DoctorDocument> docList = null;
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> resultDocMap = null;
        try {
            if (pageNo == null) {
                pageNo = 1;
            }
            int numForPage = CenterFunctionUtils.DEFAULT_PAGE_NUM;
            int fromIndex = (pageNo - 1) * numForPage;
            int toIndex = fromIndex + numForPage;
            docList = new ArrayList<>();
            Map<String, DoctorDocument> doctorMap = webServiceCacheUtil.getCacheDoctorDocument();
            Set<String> subSet = null;
            if (StringUtil.isNotEmpty(subjectId)) {
                Map<String, List<SubjectDocument>> allDocMap = webServiceDataSecondCacheUtil
                        .getCacheSubjectAndDoctorDocument().get(String.valueOf(DeptLevel.DOCTOR.getCode()));
                List<SubjectDocument> subList = allDocMap.get(subjectId);
                subSet = new HashSet<>();
                for (SubjectDocument subjectDocument : subList) {
                    subSet.add(subjectDocument.getId());
                }
            }
            if (StringUtil.isNotEmpty(searchName)) {
                Map<String, Set<String>> mapSet = webServiceCacheUtil.getCacheDoctorInfoLike();
                Iterator<Map.Entry<String, Set<String>>> mapIter = mapSet.entrySet().iterator();
                String key = "";
                Set<String> docIdSet = null;
                Set<String> resultSet = new HashSet<>();
                while (mapIter.hasNext()) {
                    Map.Entry<String, Set<String>> entry = mapIter.next();
                    key = entry.getKey();
                    docIdSet = entry.getValue();
                    if (key.contains(searchName)) {
                        resultSet.addAll(docIdSet);
                    }
                }
                Iterator<Map.Entry<String, DoctorDocument>> iter = doctorMap.entrySet().iterator();
                Map.Entry<String, DoctorDocument> entry = null;
                while (iter.hasNext()) {
                    entry = iter.next();
                    if (subSet != null && !subSet.contains(entry.getKey())) {
                        continue;
                    }
                    if (resultSet.contains(entry.getKey())) {
                        docList.add(entry.getValue());
                    }
                }
            } else {
                if (subSet != null) {
                    for (String docId : subSet) {
                        doctorMap.remove(docId);
                    }
                }
                docList.addAll(doctorMap.values());
            }
            if (toIndex > docList.size()) {
                toIndex = docList.size();
            }
            if (fromIndex <= docList.size()) {
                docList = docList.subList(fromIndex, toIndex);
                for (DoctorDocument doctorDocument : docList) {
                    resultDocMap = new HashMap<>();
                    resultDocMap.put("id", doctorDocument.getId());
                    resultDocMap.put("name", doctorDocument.getName());
                    resultDocMap.put("sexCode", doctorDocument.getSexCode());
                    resultDocMap.put("title", doctorDocument.getTitle());
                    resultDocMap.put("districtId", doctorDocument.getDistrictId());
                    resultDocMap.put("district", doctorDocument.getDistrict());
                    resultDocMap.put("deptId", doctorDocument.getDeptId());
                    resultDocMap.put("dept", doctorDocument.getDept());
                    if(StringUtil.isEmpty(doctorDocument.getSkill())){
                        resultDocMap.put("skill", "");
                    }else if(doctorDocument.getSkill().length()<30){
                        resultDocMap.put("skill", doctorDocument.getSkill());
                    }else{
                        resultDocMap.put("skill", StringUtil.abbreviate(doctorDocument.getSkill(), 30));
                    }
                    result.add(resultDocMap);
                }
            } else {
                result = new ArrayList<>();
            }
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS接口返回参数错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HisReturnException e) {
            LOGGER.debug("HIS接口返回错误", e);
            return CenterFunctionUtils.setTextResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.debug("系统错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(result);
    }

    /**
     * 指定医生个人信息
     *
     * @param id
     *            医生Id
     * @return 医生个人信息
     */
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<DoctorDocument> getDoctorById(@PathVariable String id) {
        DoctorDocument doc = null;
        try {
            doc = webServiceCacheUtil.getCacheDoctorDocument().get(id);
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS接口返回参数错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.HIS_DATALINK_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HisReturnException e) {
            LOGGER.debug("HIS接口返回错误", e);
            return CenterFunctionUtils.setTextResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.debug("系统错误", e);
            return CenterFunctionUtils.setTextResponseEntity(CenterFunctionUtils.APP_SYSTEM_ERR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseOfGet(doc);
    }

    @RequestMapping(path = "/photo", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getDoctorPhoto(String doctorId, String index) throws Exception {
        Map<String, DoctorDocument> docMap = webServiceCacheUtil.getCacheDoctorDocument();
        DoctorDocument doc = docMap.get(doctorId);
        String photoStr = "";
        if (doc != null && StringUtil.isNotEmpty(doc.getPersonPic())) {
            photoStr = webService4FileCacheUtil.getCacheDoctorPhoto(doc);
        }
        // if (doc != null && StringUtil.isNotEmpty(doc.getPersonPic())) {
        // Base64 base64 = new Base64();
        // ResponseEntity<byte[]> pic = HttpClient
        // .get("http://172.28.30.10/data/public/public_rsgl/" +
        // doc.getPersonPic());
        // String photoBase64 = base64.encodeToString(pic.getBody());
        // if (doc.getPersonPic().endsWith("png")) {
        // photoStr = "data:image/png;base64,".concat(photoBase64);
        // } else if (doc.getPersonPic().endsWith("jpg")) {
        // photoStr = "data:image/jpg;base64,".concat(photoBase64);
        // }
        // }
        // String photoStr =
        // "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAH4AAAB1CAIAAAAHhsIcAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAKYSURBVHhe7djbdaMwFEDR6dxVUAVVuAmacBEZCelKyAnG88rRWnPOT3hc+NgiOM6Pm0FJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjyU9lvRY0mNJjzUH/XJ/fGxr3XlZnky14XX7eNyXuvOpdDaXJ14PEs1AX4FS29q3Dx3MBvo2fLZsQb9t+1XDnfAmoF+W5UvwXgIrE9v69NTX3TPTuG8e37cf6Q71HN407/r6zgnierSCndKn0pl0flnulfmieZ772T5mB+Igf0kflYnLpB9KamH5+/RfVaffHf/eePrgKY/jL9CXQ6Uz2zoj/UnNMAkNxH+NvizrZM3xru9PfvmwfIv+eOHzUpTa36rSv2rddp+BWPrvLIgH8vfojwfC+tP5iZqFPtP2R7fSpwPS/+uaWH3Xb1X6T+nrUvb9iZrrYzYEC9jR65o+fZ1N88/0x/18j7ZGfHPQj89m40q/AmF4Qf94pJ959pk+br2vzXAp3hT0g3zTjH83DlYn9KXuW0t3rAfaVl8Uuhnogyt71lUoQnHiwFUPBX2Mn5QvbOsaXxnKlXwT0Hf52OrWDb/9x30vzrcLsui4DH25+lBqHvk5Xjg7TrFKmx0tlw7sXAfAYSCBX3K2ayeCn+Vj9r9MeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaTHkh5LeizpsaSHut1+AguN7LkNfXjpAAAAAElFTkSuQmCC";
        return responseOfGet(photoStr);
    }
}
