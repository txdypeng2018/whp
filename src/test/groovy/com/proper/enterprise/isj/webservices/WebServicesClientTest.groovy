package com.proper.enterprise.isj.webservices
import com.proper.enterprise.isj.user.utils.CenterFunctionUtils
import com.proper.enterprise.isj.webservices.model.enmus.PayChannel
import com.proper.enterprise.isj.webservices.model.enmus.ReturnCode
import com.proper.enterprise.isj.webservices.model.req.OrderRegReq
import com.proper.enterprise.isj.webservices.model.req.PayListReq
import com.proper.enterprise.isj.webservices.model.req.PayOrderReq
import com.proper.enterprise.isj.webservices.model.res.NetTestResult
import com.proper.enterprise.isj.log.repository.WSLogRepository
import com.proper.enterprise.platform.core.enums.WhetherType
import com.proper.enterprise.platform.core.utils.JSONUtil
import com.proper.enterprise.platform.core.utils.StringUtil
import com.proper.enterprise.platform.test.AbstractTest
import org.junit.After
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class WebServicesClientTest extends AbstractTest {

    @Autowired
    WebServicesClient client

    @Autowired
    WSLogRepository repository

    @After
    public void tearDown() {
        repository.deleteAll()
    }

    @Test
    public void reqNotTransferred() {
        def req = client.envelopReq('1001', ['a':'b', 'c': [['ca1': '1', 'ca2': '2'], ['cb1': '1', 'cb2': '2']]])

        assert req.indexOf('&lt;') < 0
        assert req.indexOf('&gt;') < 0
    }

    @Test
    public void netTest() {
        def resModel = client.netTest('11', '192.168.1.1')
        assert resModel.getReturnCode() != null
        assert StringUtil.isNotNull(resModel.returnMsg)
        assert resModel.getSignType() == 'MD5'
        assert StringUtil.isNotNull(resModel.getSign())
        assert resModel.getResEncrypted() != null
        assert resModel.getRes() != null
        assert resModel.getRes().sysDate != null

        while(repository.count() != 1) {
            println "sleep 100 milliseconds to wait until write log done"
            sleep(100)
        }
        assert repository.count() == 1
    }

    @Test
    public void getRegInfo() {
        def resModel = client.getRegInfo(CenterFunctionUtils.getHosId(), '-1', '011193', new Date(), new Date())
        def regInfo = resModel.getRes()
        println JSONUtil.toJSON(regInfo)
        assert regInfo != null
        assert regInfo.regDoctorList != null
        assert regInfo.regDoctorList[0].regList != null
        assert regInfo.regDoctorList[0].regList[0].regTimeList != null

        def regTime = regInfo.regDoctorList[0].regList[0].regTimeList[0]
        assert regTime.getTimeFlag() != null
        assert regTime.getRegStatus() != null
        assert regTime.getRegLevel() != null
        assert regTime.getIsTime() != null
    }

    @Test
    public void getHosInfo() {
        def resModel = client.getHosInfo(CenterFunctionUtils.getHosId())
        def hosInfo = resModel.getRes()
        assert hosInfo.getLevel() != null
        println hosInfo.getLevel().name()
    }

    @Test
    public void getDeptInfoByParentID() {
        def resModel = client.getDeptInfoByParentID(CenterFunctionUtils.getHosId(), '-1', '')
        def deptInfo = resModel.getRes()
        assert deptInfo.deptList != null

        def dept = deptInfo.deptList[0]
        assert dept.getLevel() != null
        assert dept.getStatus() != null
    }

    @Test
    public void orderReg() {
        def req = new OrderRegReq()
        def resModel = client.orderReg(req)
        def res = resModel.getRes()
        assert res.concessions != null
        assert res.isConcessions == WhetherType.YES || res.isConcessions == WhetherType.NO

        def concession = res.concessions[0]
        assert concession.concessionsFee >= 0
    }

    @Test
    public void toKVStyle() {
        def req = new PayOrderReq()
        req.setPayChannelId(PayChannel.TERMINATE)
        req.setBankNo('123')
        req.setList([
                ['ca1': '1', 'ca2': '2'],
                ['cb1': '1', 'cb2': '2']
        ])
        def map = client.toKVStyle(req)

        expect:
        assert map.get('PAY_CHANNEL_ID') == PayChannel.TERMINATE.getCode().toString()
        assert map.get('BANK_NO') == '123'
        assert map.get('LIST').size == 2
        assert map.get('LIST')[1].get('CB_2') == '2'
    }

    @Test
    public void getPayList() {
        def req = new PayListReq()
        def resModel = client.getPayList(req)
        def res = resModel.getRes()
        assert res.userName != null
        assert res.hospPatientId != null
        assert res.payList[0] != null
    }

    @Test
    public void getDoctorInfo() {
        def resModel = client.getDoctorInfo(CenterFunctionUtils.getHosId(), '-1', '-1')
        def res = resModel.getRes()
        assert res.hosId != null

        def payList = res.doctorList[0]
        assert payList.deptId > ''
    }

    @Test
    public void getPayDetail() {
        def resModel = client.getPayDetail(CenterFunctionUtils.getHosId(), '2', '3')
        def res = resModel.getRes()
        assert res.hospPatientId == 'XX'

        def payList = res.payDetailList[0]
        assert payList.detailId == '74679250'
    }

    @Test
    public void payOrder() {
        def resModel = client.payOrder(new PayOrderReq())
        def res = resModel.getRes()
        assert res.hospOrderId > ''
    }

    @Test
    public void getTimeRegInfo() {
        def resModel = client.getTimeRegInfo(CenterFunctionUtils.getHosId(), "-1", "1", new Date(), -1)
        println resModel.getRes()
        println resModel.getReturnCode()
    }

    @Test
    public void testParseEnvelop() {
        def nullRes = '<?xml version="1.0" encoding="UTF-8"?><ROOT> <RETURN_CODE><![CDATA[-1]]></RETURN_CODE> <RETURN_MSG><![CDATA[该身份证号已经捆绑病历号]]></RETURN_MSG> <SIGN_TYPE><![CDATA[MD5]]></SIGN_TYPE> <SIGN><![CDATA[E7C97F938AE3282A3C7ACC15AAF218E0]]></SIGN> <RES_ENCRYPTED><![CDATA[kjPZRMs96t6gBs1xfBKrlg==]]></RES_ENCRYPTED> </ROOT>'
        def model = client.parseEnvelop(nullRes, NetTestResult.class)
        assert model.getReturnCode() == ReturnCode.ERROR
    }

    @Test
    public void testRefundByHisToAPP() {
        def resModel = client.refundByHisToAPP('1001', new Date(), new Date())
        def res = resModel.getRes()
        assert res.refundlist.size() > 0
    }

}
