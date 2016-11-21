package com.proper.enterprise.isj.proxy.controller;

import com.proper.enterprise.platform.core.controller.BaseController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * web端主页面
 */
@RestController
@RequestMapping(path = "/main")
public class WebMainController  extends BaseController {
    /**
     * 取得服务电话号码
     */
    @RequestMapping(value="/resources", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getMainMenu() throws Exception {
        String retValue = "[\n"
                + "  {\n"
                + "    \"code\": \"000002\",\n"
                + "    \"name\": \"意见反馈\",\n"
                + "    \"level\": 1,\n"
                + "    \"isLeaf\": \"1\",\n"
                + "    \"icon\": \"rate_review_black\",\n"
                + "    \"url\": \"/feedback/operation\"\n"
                + "  },\n"
                + "  {\n"
                + "    \"code\": \"000003\",\n"
                + "    \"name\": \"温馨提示\",\n"
                + "    \"level\": 1,\n"
                + "    \"isLeaf\": \"1\",\n"
                + "    \"icon\": \"info_black\",\n"
                + "    \"url\": \"/prompt/tips\"\n"
                + "  },\n"
                + "  {\n"
                + "    \"code\": \"000004\",\n"
                + "    \"name\": \"医院导航\",\n"
                + "    \"level\": 1,\n"
                + "    \"isLeaf\": \"1\",\n"
                + "    \"icon\": \"place_black\",\n"
                + "    \"url\": \"/hospitalnavigation/build\"\n"
                + "  },\n"
                + "  {\n"
                + "    \"code\": \"000005\",\n"
                + "    \"name\": \"轮播图片\",\n"
                + "    \"level\": 1,\n"
                + "    \"isLeaf\": \"1\",\n"
                + "    \"icon\": \"image_black\",\n"
                + "    \"url\": \"/photo/carousel\"\n"
                + "  },\n"
                + "  {\n"
                + "    \"code\": \"000006\",\n"
                + "    \"name\": \"挂号规则\",\n"
                + "    \"level\": 1,\n"
                + "    \"isLeaf\": \"1\",\n"
                + "    \"icon\": \"description_black\",\n"
                + "    \"url\": \"/registration/rulesn\"\n"
                + "  }\n"
                + "]";
        return responseOfGet(retValue);
    }
}
