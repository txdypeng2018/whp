'use strict';

var soap = require('soap');
var parseString = require('xml2js').parseString;
var md5 = require('md5');
var crypto = require('crypto');

var handler = module.exports = {};

var algorithm = 'aes-128-ecb';
var aesKey = '2098D32C4D1399EC';
var iv = '';
var clearEncoding = 'utf8';
var cipherEncoding = 'base64';

var aesEnc = function(data) {
  var cipher = crypto.createCipheriv(algorithm, aesKey, iv);
  var cipherChunks = [];
  cipherChunks.push(cipher.update(data, clearEncoding, cipherEncoding));
  cipherChunks.push(cipher.final(cipherEncoding));
  return cipherChunks.join('');
};

var aesDec = function (data) {
  var decipher = crypto.createDecipheriv(algorithm, aesKey, iv);
  var plainChunks = [];
  for (var i = 0; i < data.length; i++) {
    plainChunks.push(decipher.update(data[i], cipherEncoding, clearEncoding));
  }
  plainChunks.push(decipher.final(clearEncoding));
  return plainChunks.join('');
};

var sign = function(resEnc, retCode, retMsg, key) {
  return md5('RES_ENCRYPTED=' + resEnc + '&RETURN_CODE=' + retCode + '&RETURN_MSG=' + retMsg + '&KEY=' + key);
};

var resXml = function(data, retCode, retMsg) {
  var resEnc = aesEnc(data);
  retCode = retCode ? retCode : '0';
  retMsg = retMsg ? retMsg : '交易成功';
  return '<![CDATA[<?xml version="1.0" encoding="UTF-8"?>'
    + '<ROOT>'
    + '<RETURN_CODE><![CDATA[0]]]]>><![CDATA[</RETURN_CODE>'
    + '<RETURN_MSG><![CDATA[交易成功]]]]>><![CDATA[</RETURN_MSG>'
    + '<SIGN_TYPE><![CDATA[MD5]]]]>><![CDATA[</SIGN_TYPE>'
    + '<SIGN><![CDATA[' + sign(resEnc, retCode, retMsg, aesKey) + ']]]]>><![CDATA[</SIGN>'
    + '<RES_ENCRYPTED><![CDATA[' + resEnc + ']]]]>><![CDATA[</RES_ENCRYPTED>'
    + '</ROOT>]]>';
};

var commonStep = function(args) {
  console.log('xml:');
  console.log(args.xml);
  parseString(args.xml, function(err, result) {
    console.log(aesDec(result.ROOT.REQ_ENCRYPTED));
  });
};

handler.service = {
  RegSjWebService: {
    RegSjWebServiceSoap: {
      NetTest: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<SYSDATE>2014-10-01 20:22:10</SYSDATE>'
                + '</RES>';
        return {
          NetTestResult: resXml(res)
        };
      },
      GetRegInfo: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<HOS_ID></HOS_ID>'
                +   '<DEPT_ID>100101</DEPT_ID>'
                +   '<REG_DOCTOR_LIST>'
                +     '<DOCTOR_ID></DOCTOR_ID>'
                +     '<NAME>张为</NAME>'
                +     '<JOB_TITLE>主治医师</JOB_TITLE>'
                +     '<REG_LIST>'
                +       '<REG_DATE>2014-10-12</REG_DATE>'
                +       '<REG_WEEKDAY>星期日</REG_WEEKDAY>'
                +       '<REG_TIME_LIST>'
                +         '<REG_ID>1005</REG_ID>'
                +         '<TIME_FLAG>1</TIME_FLAG>'
                +         '<REG_STATUS>1</REG_STATUS>'
                +         '<TOTAL>15</TOTAL>'
                +         '<OVER_COUNT>5</OVER_COUNT>'
                +         '<REG_LEVEL>1</REG_LEVEL>'
                +         '<REG_FEE>100</REG_FEE>'
                +         '<TREAT_FEE>1000</TREAT_FEE>'
                +         '<ISTIME>1</ISTIME>'
                +       '</REG_TIME_LIST>'
                +       '<REG_TIME_LIST>'
                +         '<REG_ID>1005</REG_ID>'
                +         '<TIME_FLAG>2</TIME_FLAG>'
                +         '<REG_STATUS>1</REG_STATUS>'
                +         '<TOTAL>15</TOTAL>'
                +         '<OVER_COUNT>5</OVER_COUNT>'
                +         '<REG_LEVEL>1</REG_LEVEL>'
                +         '<REG_FEE>100</REG_FEE>'
                +         '<TREAT_FEE>1000</TREAT_FEE>'
                +         '<ISTIME>1</ISTIME>'
                +       '</REG_TIME_LIST>'
                +     '</REG_LIST>'
                +   '</REG_DOCTOR_LIST>'
                + '</RES>';
        return {
          GetRegInfoResult: resXml(res)
        };
      },
      GetHosInfo: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<HOS_ID>1001</HOS_ID>'
                +   '<NAME>广东省人民医院</NAME>'
                +   '<SHORT_NAME>省人医</SHORT_NAME>'
                +   '<ADDRESS>地址</ADDRESS>'
                +   '<TEL>020-11231112</TEL>'
                +   '<WEBSITE>http://www.xxx.com</WEBSITE>'
                +   '<WEIBO></WEIBO>'
                +   '<LEVEL>3</LEVEL>'
                +   '<AREA></AREA>'
                +   '<DESC></DESC>'
                +   '<SPECIAL></SPECIAL>'
                +   '<LONGITUDE></LONGITUDE>'
                +   '<LATITUDE></LATITUDE>'
                +   '<MAX_REG_DAYS>0</MAX_REG_DAYS>'
                +   '<START_REG_TIME></START_REG_TIME>'
                +   '<END_REG_TIME></END_REG_TIME>'
                +   '<STOP_BOOK_TIMEA></STOP_BOOK_TIMEA>'
                +   '<STOP_BOOK_TIMEP></STOP_BOOK_TIMEP>'
                + '</RES>';
        return {
          GetHosInfoResult: resXml(res)
        }
      },
      GetDeptInfoByParentID: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<HOS_ID>1001</HOS_ID>'
                +   '<DEPT_LIST>'
                +     '<DEPT_ID>1207</DEPT_ID>'
                +     '<DEPT_NAME>南湖院区</DEPT_NAME>'
                +     '<PARENT_ID>0</PARENT_ID>'
                +     '<DESC></DESC>'
                +     '<LEVEL>0</LEVEL>'
                +     '<STATUS>1</STATUS>'
                +   '</DEPT_LIST>'
                + '</RES>';
        return {
          GetDeptInfoByParentIDResult: resXml(res)
        }
      },
      OrderReg: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<HOSP_PATIENT_ID>A001</HOSP_PATIENT_ID>'
                +   '<HOSP_ORDER_ID>123</HOSP_ORDER_ID>'
                +   '<HOSP_SERIAL_NUM></HOSP_SERIAL_NUM>'
                +   '<HOSP_MEDICAL_NUM></HOSP_MEDICAL_NUM>'
                +   '<HOSP_GETREG_DATE></HOSP_GETREG_DATE>'
                +   '<HOSP_SEE_DOCT_ADDR></HOSP_SEE_DOCT_ADDR>'
                +   '<HOSP_REMARK></HOSP_REMARK>'
                +   '<HOSP_CARD_NO></HOSP_CARD_NO>'
                +   '<IS_CONCESSIONS></IS_CONCESSIONS>'
                +   '<CONCESSIONS>'
                +     '<CONCESSIONS_FEE></CONCESSIONS_FEE>'
                +     '<REAL_REG_FEE></REAL_REG_FEE>'
                +     '<REAL_TREAT_FEE></REAL_TREAT_FEE>'
                +     '<REAL_TOTAL_FEE></REAL_TOTAL_FEE>'
                +     '<CONCESSIONS_TYPE></CONCESSIONS_TYPE>'
                +   '</CONCESSIONS>'
                + '</RES>';
        return {
          OrderRegResult: resXml(res)
        }
      },
      GetPayList: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<USER_NAME>汪礼</USER_NAME>'
                +   '<HOSP_PATIENT_ID></HOSP_PATIENT_ID>'
                +   '<CARD_TYPE>1</CARD_TYPE>'
                +   '<CARD_NO>4401000007963120</CARD_NO>'
                +   '<IDCARD_TYPE>1</IDCARD_TYPE>'
                +   '<IDCARD_NO>4401000007963120</IDCARD_NO>'
                +   '<PAY_LIST>'
                +     '<HOSP_SEQUENCE>1111111111</HOSP_SEQUENCE>'
                +     '<DEPT_NAME>内科</DEPT_NAME>'
                +     '<DOCTOR_NAME>张大仙</DOCTOR_NAME>'
                +     '<PAY_AMOUT>10000</PAY_AMOUT>'
                +     '<PAY_REMARK>2012-3-1/123</PAY_REMARK>'
                +     '<RECEIPT_DATE>2015-4-21 18:00:45</RECEIPT_DATE>'
                +   '</PAY_LIST>'
                + '</RES>';
        return {
          GetPayListResult: resXml(res)
        }
      },
      GetDoctorInfo: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<HOS_ID></HOS_ID>'
                +   '<DOCTOR_LIST>'
                +     '<DEPT_ID>100101</DEPT_ID>'
                +     '<DOCTOR_ID>201</DOCTOR_ID>'
                +     '<NAME>张为</NAME>'
                +     '<IDCARD></IDCARD>'
                +     '<DESC></DESC>'
                +     '<SPECIAL></SPECIAL>'
                +     '<JOB_TITLE></JOB_TITLE>'
                +     '<REG_FEE>100</REG_FEE>'
                +     '<STATUS>1</STATUS>'
                +     '<BIRTHDAY>1978-10-21</BIRTHDAY>'
                +     '<MOBILE>18912345680</MOBILE>'
                +     '<TEL>020-22222222</TEL>'
                +   '</DOCTOR_LIST>'
                + '</RES>';
        return {
          GetDoctorInfoResult: resXml(res)
        }
      },
      GetPayDetail: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<USER_NAME>郭乐</USER_NAME>'
                +   '<HOSP_PATIENT_ID>XX</HOSP_PATIENT_ID>'
                +   '<MEDICAL_INSURANNCE_TYPE>医保</MEDICAL_INSURANNCE_TYPE>'
                +   '<PAY_TOTAL_FEE>10000</PAY_TOTAL_FEE>'
                +   '<PAY_BEHOOVE_FEE>7000</PAY_BEHOOVE_FEE>'
                +   '<PAY_ACTUAL_FEE>7000</PAY_ACTUAL_FEE>'
                +   '<PAY_MI_FEE>3000</PAY_MI_FEE>'
                +   '<RECEIPT_ID></RECEIPT_ID>'
                +   '<PAY_DETAIL_LIST>'
                +     '<DETAIL_TYPE>卫生材料</DETAIL_TYPE>'
                +     '<DETAIL_NAME>一次性注射器(20ml)</DETAIL_NAME>'
                +     '<DETAIL_ID>74679250</DETAIL_ID>'
                +     '<DETAIL_UNIT>支</DETAIL_UNIT>'
                +     '<DETAIL_COUNT>1</DETAIL_COUNT>'
                +     '<DETAIL_PRICE>4000</DETAIL_PRICE>'
                +     '<DETAIL_SPEC>【甲类】</DETAIL_SPEC>'
                +     '<DETAIL_AMOUT>4000</DETAIL_AMOUT>'
                +     '<DETAIL_MI>0</DETAIL_MI>'
                +   '</PAY_DETAIL_LIST>'
                + '</RES>';
        return {
          GetPayDetailResult: resXml(res)
        }
      },
      PayOrderAPP: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<HOSP_ORDER_ID>777777777</HOSP_ORDER_ID>'
                +   '<RECEIPT_ID>2013-12-12/01/22235</RECEIPT_ID>'
                +   '<HOSP_REMARK></HOSP_REMARK>'
                + '</RES>';
        return {
          PayOrderAPPResult: resXml(res)
        }
      },
      GetTimeRegInfo: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<TIME_REG_LIST>'
                +     '<TIME_FLAG>1</TIME_FLAG>'
                +     '<BEGIN_TIME>11:10</BEGIN_TIME>'
                +     '<END_TIME>12:10</END_TIME>'
                +     '<TOTAL>12</TOTAL>'
                +     '<OVER_COUNT>0</OVER_COUNT>'
                +     '<REG_ID>XX</REG_ID>'
                +   '</TIME_REG_LIST>'
                + '</RES>';
        return {
          GetTimeRegInfoResult: resXml(res)
        }
      },
      CancelReg: function(args) {
        commonStep(args);
        return {
          CancelRegResult: resXml('')
        }
      },
      Refund: function(args) {
        commonStep(args);
        var res = '<RES>'
                +   '<REFUND_FLAG>1</REFUND_FLAG>'
                +   '<HOSP_REFUND_ID>201411111101010123</HOSP_REFUND_ID>'
                + '</RES>';
        return {
          RefundResult: resXml(res)
        }
      },
      GetPayDetailAPP: function (args) {
        commonStep(args);
        var res = "<RES>" +
            "  <USER_NAME></USER_NAME>" +
            "  <HOSP_PATIENT_ID>M003040057</HOSP_PATIENT_ID>" +
            "  <IDCARD_TYPE>1</IDCARD_TYPE>" +
            "  <IDCARD_NO>22032219890815482X</IDCARD_NO>" +
            "  <CARD_TYPE>8</CARD_TYPE>" +
            "  <CARD_NO></CARD_NO>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829910</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>F00000057647</ITEM_CODE>" +
            "    <ITEM_NAME>一次性使用真空静脉血样采集容器</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>115</UNIT_PRICE>" +
            "    <QTY>3</QTY>" +
            "    <PRICE_UNIT>支</PRICE_UNIT>" +
            "    <EXEC_DPNM>急诊科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829910</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>F00000000103</ITEM_CODE>" +
            "    <ITEM_NAME>静脉注射【静脉采血】</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>330</UNIT_PRICE>" +
            "    <QTY>1</QTY>" +
            "    <PRICE_UNIT>次</PRICE_UNIT>" +
            "    <EXEC_DPNM>急诊科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829910</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>F00000043881</ITEM_CODE>" +
            "    <ITEM_NAME>一次性使用采血针</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>69</UNIT_PRICE>" +
            "    <QTY>1</QTY>" +
            "    <PRICE_UNIT>个</PRICE_UNIT>" +
            "    <EXEC_DPNM>急诊科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829907</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>701</ITEM_CODE>" +
            "    <ITEM_NAME>血清脂肪酶（急诊）</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>1650</UNIT_PRICE>" +
            "    <QTY>1</QTY>" +
            "    <PRICE_UNIT>次</PRICE_UNIT>" +
            "    <EXEC_DPNM>检验科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829907</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>477</ITEM_CODE>" +
            "    <ITEM_NAME>血淀粉酶(急诊)</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>1650</UNIT_PRICE>" +
            "    <QTY>1</QTY>" +
            "    <PRICE_UNIT>次</PRICE_UNIT>" +
            "    <EXEC_DPNM>检验科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829907</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>479</ITEM_CODE>" +
            "    <ITEM_NAME>肾功(急诊)</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>1650</UNIT_PRICE>" +
            "    <QTY>1</QTY>" +
            "    <PRICE_UNIT>次</PRICE_UNIT>" +
            "    <EXEC_DPNM>检验科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829908</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>596</ITEM_CODE>" +
            "    <ITEM_NAME>尿常规(急诊)</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>2970</UNIT_PRICE>" +
            "    <QTY>1</QTY>" +
            "    <PRICE_UNIT>次</PRICE_UNIT>" +
            "    <EXEC_DPNM>检验科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829909</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>598</ITEM_CODE>" +
            "    <ITEM_NAME>血常规(急诊)</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>2750</UNIT_PRICE>" +
            "    <QTY>1</QTY>" +
            "    <PRICE_UNIT>次</PRICE_UNIT>" +
            "    <EXEC_DPNM>检验科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "  <PAY_LIST>" +
            "    <CLINIC_CODE>21476234</CLINIC_CODE>" +
            "    <RECIPE_NO>65829910</RECIPE_NO>" +
            "    <CARD_NO>M003040057</CARD_NO>" +
            "    <REG_DATE>2014-9-30 4:02:54</REG_DATE>" +
            "    <ITEM_CODE>F00000009003</ITEM_CODE>" +
            "    <ITEM_NAME>X线计算机体层(CT)平扫(64层扫描)</ITEM_NAME>" +
            "    <DRUG_FLAG>0</DRUG_FLAG>" +
            "    <UNIT_PRICE>33000</UNIT_PRICE>" +
            "    <QTY>2</QTY>" +
            "    <PRICE_UNIT>每个部位</PRICE_UNIT>" +
            "    <EXEC_DPNM>放射科</EXEC_DPNM>" +
            "    <PAY_FLAG>0</PAY_FLAG>" +
            "    <CANCEL_FLAG>1</CANCEL_FLAG>" +
            "  </PAY_LIST>" +
            "</RES>";
        return {
            GetPayDetailAPPResult: resXml(res)
        }
      }
    }
  }
};