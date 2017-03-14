package com.proper.enterprise.isj.proxy.controller
import com.proper.enterprise.isj.proxy.utils.cache.mock.FakeCacheOffice
import com.proper.enterprise.isj.user.document.UserInfoDocument
import com.proper.enterprise.isj.user.repository.UserInfoRepository
import com.proper.enterprise.isj.user.service.UserInfoService
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class MedicalReportsControllerTest extends AbstractTest {

    @Autowired
    FakeCacheOffice office

    @Autowired
    UserInfoService userInfoService

    @Autowired
    UserInfoRepository userInfoRepository

    @Test
    public void getReportDetailInfo() {
        assert resOfGet('/medicalReports/ / ', HttpStatus.BAD_REQUEST) == CenterFunctionUtils.HIS_DATALINK_ERR
        assert resOfGet('/medicalReports/1/1', HttpStatus.BAD_REQUEST) == CenterFunctionUtils.APP_SYSTEM_ERR
        assert resOfGet('/medicalReports/2/2', HttpStatus.BAD_REQUEST) == CenterFunctionUtils.APP_PACS_REPORT_ERR
        assert resOfGet('/medicalReports/3/3', HttpStatus.BAD_REQUEST) == CenterFunctionUtils.HIS_DATALINK_ERR

        def rId = '234'
        office.putTestReport(rId)
        def res = get("/medicalReports/2/$rId", HttpStatus.OK).getResponse()
        assert res.getHeader(HttpHeaders.CONTENT_TYPE) == MediaType.TEXT_PLAIN_VALUE
    }

    @Test
    public void getReportsList() {
        userInfoRepository.deleteAll()
        assert resOfGet('/medicalReports', HttpStatus.BAD_REQUEST) == CenterFunctionUtils.APP_SYSTEM_ERR

        def userId = 'user-without-medical-num'
        mockUser(userId)
        UserInfoDocument user = new UserInfoDocument()
        user.setPatientVisits('1')
        user.setUserId(userId)
        user.setIdCard('510115196806189103')
        user.setName('name')
        user.setPhone('123456')
        userInfoService.saveOrUpdateUserInfo(user)
        assert resOfGet('/medicalReports', HttpStatus.BAD_REQUEST) == CenterFunctionUtils.PATIENTINFO_MEDICALNUM_NULL_ERR

        user.setMedicalNum("123")
        userInfoService.saveOrUpdateUserInfo(user)
        assert resOfGet('/medicalReports?category=3', HttpStatus.BAD_REQUEST) == CenterFunctionUtils.HIS_DATALINK_ERR

        assert resOfGet('/medicalReports?category=2', HttpStatus.BAD_REQUEST) == CenterFunctionUtils.APP_PACS_REPORT_ERR
    }

}
