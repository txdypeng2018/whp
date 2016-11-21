package com.proper.enterprise.isj.app.service;

import com.proper.enterprise.isj.app.document.AppVersionDocument;

public interface AppVersionService {

    AppVersionDocument save(AppVersionDocument appVersion);

    AppVersionDocument getLatestVersionInfo();

    int getLatestVersion();

    AppVersionDocument getCertainVersion(int version);

}
