package com.proper.enterprise.isj.proxy.service.impl

import com.proper.enterprise.isj.proxy.document.DoctorScheduleDocument
import com.proper.enterprise.isj.proxy.document.doctor.ScheDoctorDocument
import com.proper.enterprise.isj.proxy.service.ScheduleService
import com.proper.enterprise.isj.proxy.utils.cache.WebService4HisInterfaceCacheUtil
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceDataSecondCacheUtil
import com.proper.enterprise.isj.proxy.utils.cache.data.scheduleserviceimpltest.*
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils
import com.proper.enterprise.isj.webservices.WebServicesClient
import com.proper.enterprise.platform.core.utils.DateUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.test.context.jdbc.Sql

class ScheduleServiceImplTest extends AbstractTest {

    @Autowired
    ScheduleService service

    @Autowired
    WebService4HisInterfaceCacheUtil webService4HisInterfaceCacheUtil;

    @Autowired
    WebServiceDataSecondCacheUtil webServiceDataSecondCacheUtil;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    WebServicesClient webServicesClient;

    @Autowired
    TestGetScheduleDoctors2Data testGetScheduleDoctors2Data;

    @Autowired
    TestGetScheduleDoctors3Data testGetScheduleDoctors3Data;

    @Autowired
    TestGetScheduleDoctors4Data testGetScheduleDoctors4Data;

    @Autowired
    TestGetScheduleDoctors5Data testGetScheduleDoctors5Data;

    @Autowired
    TestGetScheduleDoctors6Data testGetScheduleDoctors6Data;

    @Test
    @Sql
    public void checkSameDayRule() {
        def list = service.getScheduleDoctors('1207', null, '金', DateUtil.toDate('2016-10-12'), DateUtil.toDate('2016-10-12'), false, 10)
        assert list.size() == 0

        list = service.getScheduleDoctors('1207', null, '金', DateUtil.toDate('2016-10-13'), DateUtil.toDate('2016-11-12'), true, 10)
        assert list.size() == 3
        this.testGetScheduleDoctors2()
        this.testGetScheduleDoctors3()
        this.testGetScheduleDoctors4()
        this.testGetScheduleDoctors5()
        this.testGetScheduleDoctors6()
    }

    /**
     * 准备数据为1条
     * 测试正常数据
     */
    public void testGetScheduleDoctors2() {
        Date startDate = DateUtil.toDate("2016-10-01");
        Date endDate = DateUtil.toDate("2016-10-30");
        testGetScheduleDoctors2Data.cacheData(startDate, endDate);
        List<ScheDoctorDocument> scheList = service.getScheduleDoctors("1207", null, null, startDate, endDate, true, 10);
        assert scheList.size() == 1

        //将出诊院区和查询院区不相符的记录过滤掉
        List<ScheDoctorDocument> scheList2 = service.getScheduleDoctors("1221", "0010", null, startDate, endDate, true, 10);
        assert scheList2.size() == 0
        List<DoctorScheduleDocument> docScheList =  service.findDoctorScheduleByDate("011193", "1207", null, "2016-10-30")
        assert docScheList.size() == 1
        clearCache()
    }


    private void clearCache() {
        Cache cache = cacheManager.getCache("pep-temp");
        Cache cache120 = cacheManager.getCache(CenterFunctionUtils.CACHE_NAME_PEP_TEMP_120);
        cache.clear();
        cache120.clear();
    }

    /**
     * 准备数据为1条
     * 同一天出诊级别多个将过滤掉
     */
    public void testGetScheduleDoctors3() {
        Date startDate = DateUtil.toDate("2016-10-01");
        Date endDate = DateUtil.toDate("2016-10-30");
        testGetScheduleDoctors3Data.cacheData(startDate, endDate);
        List<ScheDoctorDocument> scheList = service.getScheduleDoctors("1207", null, null, startDate, endDate, true, 10);
        assert scheList.size() == 0
        clearCache()
    }

    /**
     * 准备数据为1条
     * 返回的医生排班列表,只返回了出诊科室,对应的院区没有返回,将过滤
     */
    public void testGetScheduleDoctors4() {
        Date startDate = DateUtil.toDate("2016-10-01");
        Date endDate = DateUtil.toDate("2016-10-30");
        testGetScheduleDoctors4Data.cacheData(startDate, endDate);
        List<ScheDoctorDocument> scheList = service.getScheduleDoctors("1207", null, null, startDate, endDate, true, 10);
        assert scheList.size() == 0
        clearCache()
    }

    /**
     * 准备数据为1条
     * 查询的数据排班分时记录的集合为空(regTimeList.size=0),将此部分数据赋值暂停挂号
     * 预约挂号院区为空,当日挂号院区不能为空
     */
    public void testGetScheduleDoctors5() {
        Date startDate = DateUtil.toDate("2016-10-01");
        Date endDate = DateUtil.toDate("2016-10-30");
        testGetScheduleDoctors5Data.cacheData(startDate, endDate);
        List<ScheDoctorDocument> scheList = service.getScheduleDoctors("1207", null, null, startDate, endDate, true, 10);
        assert scheList.size() == 1
        assert scheList.get(0).getUnallowed() == CenterFunctionUtils.REG_TEMP_STOP
        assert scheList.get(0).getDistrictId().equals("")

        //当日挂号,院区不能为空
        List<ScheDoctorDocument> scheList2 = service.getScheduleDoctors("1207", null, null, startDate, endDate, false, 10);
        assert scheList2.size() == 1
        assert scheList2.get(0).getUnallowed() == CenterFunctionUtils.REG_TEMP_STOP
        assert !scheList2.get(0).getDistrictId().equals("")
        clearCache()
    }

    /**
     * 准备数据为1条
     * 没有号点信息集合的将过滤掉(TimeRegInfo的TimeReg集合为0)
     */
    public void testGetScheduleDoctors6() {
        Date startDate = DateUtil.toDate("2016-10-01");
        Date endDate = DateUtil.toDate("2016-10-30");
        testGetScheduleDoctors6Data.cacheData(startDate, endDate);
        List<ScheDoctorDocument> scheList = service.getScheduleDoctors("1207", null, null, startDate, endDate, true, 10);
        assert scheList.size() == 0
        clearCache()
    }


}
