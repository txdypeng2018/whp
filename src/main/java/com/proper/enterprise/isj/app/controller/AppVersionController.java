package com.proper.enterprise.isj.app.controller;

import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.isj.app.service.AppVersionService;
import com.proper.enterprise.platform.auth.jwt.annotation.JWTIgnore;
import com.proper.enterprise.platform.core.controller.BaseController;
import com.proper.enterprise.platform.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class AppVersionController extends BaseController {

    @Autowired
    AppVersionService service;

    @JWTIgnore
    @GetMapping("/latest")
    public ResponseEntity<AppVersionDocument> getLatestVersionInfo(@RequestParam(required = false) String current) {
        int latestVersion = service.getLatestVersion();
        int currentVersion = StringUtil.isNull(current) ? -1 : Integer.parseInt(current);
        AppVersionDocument latest = null;
        if (latestVersion > currentVersion) {
            latest = service.getLatestVersionInfo();
        }
        return responseOfGet(latest);
    }

    @PutMapping(path = "/latest")
    public ResponseEntity<AppVersionDocument> updateLatestVersionInfo(@RequestBody AppVersionDocument document) {
        return responseOfPut(service.save(document));
    }

    @JWTIgnore
    @GetMapping(path = "/versions/{version}")
    public ResponseEntity<AppVersionDocument> getCertainVersionInfo(@PathVariable String version) {
        int certainVersion = StringUtil.isNull(version) ? -1 : Integer.parseInt(version);
        AppVersionDocument retVersion = null;
        retVersion = service.getCertainVersion(certainVersion);
        return responseOfGet(retVersion);
    }

}
