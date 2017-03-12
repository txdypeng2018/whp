package com.proper.enterprise.isj.admin.controller;

import com.proper.enterprise.isj.log.document.WSLogDocument;
import com.proper.enterprise.isj.log.service.WSLogService;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.entity.DataTrunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/logview")
public class AdminWsLogController extends BaseController {

    @Autowired
    private WSLogService wSLogService;

    /**
     * 取得人员信息列表
     */
    @GetMapping(path = "/wsLog")
    public ResponseEntity<DataTrunk<WSLogDocument>> wsLog(int pageNo, int pageSize,
            String search, String startDate, String endDate, String methodName) throws Exception {
        DataTrunk<WSLogDocument> dataTrunk = wSLogService.getWsLogList(pageNo, pageSize, search,
                startDate, endDate, methodName);
        return responseOfGet(dataTrunk);
    }
}
