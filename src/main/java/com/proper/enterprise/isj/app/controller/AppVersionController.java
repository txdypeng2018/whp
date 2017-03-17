package com.proper.enterprise.isj.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proper.enterprise.isj.app.business.AppVersionGetCertainVersionBusiness;
import com.proper.enterprise.isj.app.document.AppVersionDocument;
import com.proper.enterprise.isj.business.BusinessToolkit;
import com.proper.enterprise.isj.context.VersionContext;
import com.proper.enterprise.isj.controller.IHosBaseController;
import com.proper.enterprise.isj.support.dispatch.DispatcherToolkit;
import com.proper.enterprise.platform.api.auth.annotation.AuthcIgnore;

@RestController
@RequestMapping("/app")
public class AppVersionController extends IHosBaseController {

    @Autowired
    DispatcherToolkit toolkit;

    @Autowired
    @Qualifier("defaultBusinessToolkit")
    BusinessToolkit toolkitx;

    @AuthcIgnore
    @GetMapping("/latest")
    public ResponseEntity<AppVersionDocument> getLatestVersionInfo(@RequestParam(required = false) String current) {
        return responseOfGet((AppVersionDocument) toolkit.dispatch("getLatestVersionInfoEntry", current));
    }

    @PutMapping(path = "/latest")
    public ResponseEntity<AppVersionDocument> updateLatestVersionInfo(@RequestBody AppVersionDocument document) {
        return responseOfGet((AppVersionDocument) toolkit.dispatch("updateLatestVersionInfoEntry", document));
    }

    @SuppressWarnings("unchecked")
    @AuthcIgnore
    @GetMapping(path = "/versions/{version}")
    public ResponseEntity<AppVersionDocument> getCertainVersionInfo(@PathVariable String version) {
        return responseOfGet(toolkitx.execute(AppVersionGetCertainVersionBusiness.class,
                c -> ((VersionContext<AppVersionDocument>) c).setVersion(version)));

    }

}
