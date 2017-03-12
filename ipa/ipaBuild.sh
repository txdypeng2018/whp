export targetPath=/Users/licx/Documents/temp
export ipaType=AdHocExportOptions.plist

xcodebuild archive -archivePath "$targetPath/掌上盛京医院.xcarchive" -workspace 掌上盛京医院.xcworkspace -sdk iphoneos -scheme "掌上盛京医院" -configuration Release PRODUCT_BUNDLE_IDENTIFIER="com.neusoft.hcb.shengjing"
xcodebuild -exportArchive -archivePath "$targetPath/掌上盛京医院.xcarchive" -exportPath "$targetPath/" -exportOptionsPlist $targetPath/$ipaType
