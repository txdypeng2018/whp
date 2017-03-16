package com.proper.enterprise.isj.proxy.utils.cache.mock

import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode
import com.proper.enterprise.isj.webservices.model.res.RegInfo
import com.proper.enterprise.isj.webservices.model.res.ResModel
import com.proper.enterprise.isj.webservices.model.res.TimeRegInfo
import com.proper.enterprise.isj.webservices.model.res.reginfo.Reg
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegDoctor
import com.proper.enterprise.isj.webservices.model.res.reginfo.RegTime
import com.proper.enterprise.isj.webservices.model.res.timereglist.TimeReg
import org.springframework.cache.annotation.CacheConfig
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
@CacheConfig(cacheNames = 'pep-temp')
class MockWebService4HisInterfaceCacheUtil extends WebService4HisInterfaceCacheUtil {

    public ResModel<RegInfo> getCacheDoctorScheInfoRes(String doctorId, String startDate, String endDate) throws Exception {
        def model = new ResModel<RegInfo>()
        model.setReturnCode(ReturnCode.SUCCESS.getCode())

        def d1 = new RegDoctor('011193', '金冶', '主治医师')
        d1.setRegList(arrangeReg('M', new Date()))

        def d2 = new RegDoctor('012193', '冶金', '主任医师')
        d2.setRegList(arrangeReg('7', new Date()))

        def d3 = new RegDoctor('013193', '炼金', '副主任医师')
        d3.setRegList(arrangeReg('9', new Date()))

        def regInfo = new RegInfo()
        regInfo.setDistrict('1')
        regInfo.setRegDoctorList([d1, d2, d3].findAll {
            it.doctorId == doctorId
        })
        model.setRes(regInfo)
        model
    }



    private def arrangeReg(String regLv, Date date) {
        def reg = new Reg()
        reg.setRegDistrict("1")
        def regTime = new RegTime()
        regTime.setRegLevel(regLv)
        regTime.setTimeFlag(1)
        reg.setRegTimeList([regTime])
        reg.setRegDate(date.format('yyyy-MM-dd'))
        [reg]
    }

    public  ResModel<TimeRegInfo> getCacheDoctorTimeRegInfoRes(String doctorId, String regDate){
        ResModel<TimeRegInfo> infoRes = new ResModel<TimeRegInfo>();
        List<TimeReg> timeRegList = new ArrayList<>();
        TimeReg timeReg = new TimeReg();
        timeReg.setBeginTime("08:00");
        timeReg.setTotal(1);
        timeReg.setOverCount(1);
        timeReg.setTimeFlag("1");
        timeRegList.add(timeReg);
        TimeRegInfo info = new TimeRegInfo();
        info.setTimeRegList(timeRegList);
        infoRes.setRes(info);
        return infoRes;
    }
}
