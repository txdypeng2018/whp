package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.isj.proxy.document.SubjectDocument;
import com.proper.enterprise.isj.proxy.service.SubjectService;
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by think on 2016/8/16 0016. 学科列表
 */
@RestController
@RequestMapping(path = "/subjects")
public class SubjectController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubjectController.class);

    @Autowired
    SubjectService subjectService;

    /**
     * 取得学科列表
     * 
     * @param districtId
     *            院区ID（没有时学科合并显示）
     * @param type
     *            挂号类别，2 为预约挂号，1 为当日挂号
     * @return 学科列表
     */
    @JWTIgnore
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<SubjectDocument>> getSubject(String districtId, @RequestParam String type) {
        List<SubjectDocument> list;
        try {
            list = subjectService.findSubjectDocumentListFromHis(districtId, "2".equals(type));
        } catch (UnmarshallingFailureException e) {
            LOGGER.debug("解析HIS接口返回参数错误", e);
            throw new RuntimeException(CenterFunctionUtils.HIS_DATALINK_ERR);
        } catch (Exception e) {
            LOGGER.debug("系统错误", e);
            throw new RuntimeException(e.getMessage());
        }
        return responseOfGet(list);
    }
}
