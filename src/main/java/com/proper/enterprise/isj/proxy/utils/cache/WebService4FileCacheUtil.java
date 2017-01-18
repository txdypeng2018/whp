package com.proper.enterprise.isj.proxy.utils.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.proper.enterprise.isj.user.utils.CenterFunctionUtils;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.service.DoctorService;
import com.proper.enterprise.isj.proxy.utils.scheduler.TaskSchedulerUtil;
import com.proper.enterprise.platform.core.PEPConstants;
import com.proper.enterprise.platform.core.utils.ConfCenter;
import com.proper.enterprise.platform.core.utils.StringUtil;
import com.proper.enterprise.platform.core.utils.http.HttpClient;

import net.coobird.thumbnailator.Thumbnails;

/**
 * Web Service缓存工具.
 * Created by think on 2016/9/27 0027.
 */
@Component
@CacheConfig(cacheNames = "pep")
public class WebService4FileCacheUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebService4FileCacheUtil.class);

    //private static final String OA2HISDOCINFO_KEY = "'oa2hisdocInfo'";

    @Autowired
    DoctorService doctorService;

    @Autowired
    TaskSchedulerUtil taskSchedulerUtil;

    @Value("${isj.photo.url}")
    String photoUrl;

    @Value("${isj.photo.thumbnail.width}")
    String photoWidth;

    @Value("${isj.photo.thumbnail.height}")
    String photoHeight;

    @Value("${isj.photo.thumbnail.quality}")
    String photoQuality;

    @Value("${isj.report.photo.thumbnail.width}")
    String reportPhotoWidth;

    @Value("${isj.report.photo.thumbnail.height}")
    String reportPhotoHeight;

    @CachePut(value= CenterFunctionUtils.CACHE_NAME_PEP_TEMP_2592000, key = "'doctorPic_'+#p0.id")
    public String cacheDoctorPhoto(DoctorDocument doctor) throws Exception {
        if (StringUtil.isEmpty(doctor.getId())) {
            return "";
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(new URL(photoUrl + doctor.getPersonPic()))
                .size(Integer.parseInt(photoWidth), Integer.parseInt(photoHeight))
                .outputQuality(Float.parseFloat(photoQuality))
                .toOutputStream(os);

        String photoStr = "";
        if (doctor.getPersonPic().endsWith("png")) {
            photoStr = "data:image/png;base64,";
        } else if (doctor.getPersonPic().endsWith("jpg")) {
            photoStr = "data:image/jpg;base64,";
        }
        return photoStr.concat(new Base64().encodeToString(os.toByteArray()));
    }

    @Cacheable(value= CenterFunctionUtils.CACHE_NAME_PEP_TEMP_2592000, key = "'doctorPic_'+#p0.id")
    public String getCacheDoctorPhoto(DoctorDocument doctor) throws Exception {
        return cacheDoctorPhoto(doctor);
    }

    @CacheEvict(value= CenterFunctionUtils.CACHE_NAME_PEP_TEMP_2592000, key = "'doctorPic_'+#p0")
    public void evictCacheDoctorPhoto(String doctorId) throws Exception {
    }

    @CachePut(value= CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900, key = "'reportPic_'+#p0")
    public String cacheReportPhoto(String reportId) throws Exception {
        String photoStr = "";
        try {
            if (StringUtil.isNotEmpty(reportId)) {
                String strReportUrl = ConfCenter.get("isj.report.baseUrl") + ConfCenter.get("isj.report.getSendUrl");
                String sendUrl = new String(HttpClient.get(strReportUrl).getBody(), PEPConstants.DEFAULT_CHARSET);
                LOGGER.debug("sendUrl:" + sendUrl);
                StringBuilder strReportDetaiUrl = new StringBuilder();
                strReportDetaiUrl.append(sendUrl.replaceAll("\"", ""));
                strReportDetaiUrl.append(reportId);
                strReportDetaiUrl.append(ConfCenter.get("isj.report.reportEnd"));
                LOGGER.debug("strReportDetaiUrl:" + strReportDetaiUrl);
                Document reportDoc = Jsoup.connect(strReportDetaiUrl.toString()).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(25000).post();
                Element reportElement = reportDoc.select("script").last();
                // 获取javaScript中的rpturi信息
                Pattern p = Pattern.compile("(?is)rpturi=\"(.+?)\"");
                Matcher m = p.matcher(reportElement.html());
                // 详细信息图片url地址
                String rpturi = null;
                if (m.find()) {
                    // 解析javaScript
                    String retValue = m.group(1);
                    int startPos = retValue.indexOf("http");
                    int endPos = retValue.lastIndexOf("\'");
                    rpturi = retValue.substring(startPos, endPos);
                }
                LOGGER.debug("strReportDetaiUrl:" + strReportDetaiUrl);
                if (StringUtil.isNotEmpty(rpturi)) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    Thumbnails.of(new URL(rpturi))
                            .size(Integer.parseInt(reportPhotoWidth), Integer.parseInt(reportPhotoHeight))
                            .toOutputStream(os);

                    if (rpturi.endsWith("png")) {
                        photoStr = "data:image/png;base64,";
                    } else if (rpturi.endsWith("jpg")) {
                        photoStr = "data:image/jpg;base64,";
                    }
                    return photoStr.concat(new Base64().encodeToString(os.toByteArray()));
                }
            }
        } catch(IOException ie) {
            LOGGER.debug("WebService4FileCacheUtil.cacheReportPhoto[Exception]:", ie);
            throw ie;
        }
        return photoStr;
    }

    @Cacheable(value= CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900, key = "'reportPic_'+#p0")
    public String getCacheReportPhoto(String repordId) throws Exception {
        return cacheReportPhoto(repordId);
    }

    @CacheEvict(value= CenterFunctionUtils.CACHE_NAME_PEP_TEMP_900, key = "'reportPic_'+#p0")
    public void evictCacheReportPhoto(String repordId) throws Exception {
    }
}
