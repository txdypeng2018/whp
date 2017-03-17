package com.proper.enterprise.isj.proxy.business.schedule;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proper.enterprise.isj.context.DoctorIdContext;
import com.proper.enterprise.isj.proxy.document.DoctorDocument;
import com.proper.enterprise.isj.proxy.utils.cache.WebService4FileCacheUtil;
import com.proper.enterprise.isj.proxy.utils.cache.WebServiceCacheUtil;
import com.proper.enterprise.platform.core.api.IBusiness;
import com.proper.enterprise.platform.core.api.ModifiedResultBusinessContext;
import com.proper.enterprise.platform.core.utils.StringUtil;

@Service
public class FetchDoctorPhotoBusiness<M extends DoctorIdContext<String> & ModifiedResultBusinessContext<String>>
        implements IBusiness<String, M> {
    @Autowired
    WebServiceCacheUtil webServiceCacheUtil;

    @Autowired
    WebService4FileCacheUtil webService4FileCacheUtil;

    @Override
    public void process(M ctx) throws Exception {
        String doctorId = ctx.getDoctorId();

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
        ctx.setResult(photoStr);
    }
}
