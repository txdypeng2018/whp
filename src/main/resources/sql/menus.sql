INSERT INTO pep_auth_menus (id, create_user_id, create_time, last_modify_user_id, last_modify_time, name, route, icon, parent_id, sequence_number, valid) VALUES ('app-ywtj', 'isj', '2015-08-18 09:38:00', 'isj', '2015-08-18 09:38:00', '业务统计', '/statistics/statisticsView', 'assessment', null, '0', 'Y');
INSERT INTO pep_auth_menus (id, create_user_id, create_time, last_modify_user_id, last_modify_time, name, route, icon, parent_id, sequence_number, valid) VALUES ('app-yjfk', 'isj', '2015-08-18 09:38:00', 'isj', '2015-08-18 09:38:00', '意见反馈', '/feedback/operationView', 'rate_review_black', null, '1', 'Y');
INSERT INTO pep_auth_menus (id, create_user_id, create_time, last_modify_user_id, last_modify_time, name, route, icon, parent_id, sequence_number, valid) VALUES ('app-apppz', 'isj', '2015-08-18 09:38:00', 'isj', '2015-08-18 09:38:00', 'APP 配置', '/appSettings', 'phonelink_setup', null, '2', 'Y');
INSERT INTO pep_auth_menus (id, create_user_id, create_time, last_modify_user_id, last_modify_time, name, route, icon, parent_id, sequence_number, valid) VALUES ('app-ghgz', 'isj', '2015-08-18 09:38:00', 'isj', '2015-08-18 09:38:00', '规则设置', '/registration/rulesn', 'description_black', 'app-apppz', '0', 'Y');
INSERT INTO pep_auth_menus (id, create_user_id, create_time, last_modify_user_id, last_modify_time, name, route, icon, parent_id, sequence_number, valid) VALUES ('app-wxts', 'isj', '2015-08-18 09:38:00', 'isj', '2015-08-18 09:38:00', '温馨提示', '/prompt/tips', 'info_black', 'app-apppz', '1', 'Y');
INSERT INTO pep_auth_menus (id, create_user_id, create_time, last_modify_user_id, last_modify_time, name, route, icon, parent_id, sequence_number, valid) VALUES ('app-yydh', 'isj', '2015-08-18 09:38:00', 'isj', '2015-08-18 09:38:00', '医院导航', '/hospitalnavigation/build', 'place_black', 'app-apppz', '2', 'Y');
INSERT INTO pep_auth_menus (id, create_user_id, create_time, last_modify_user_id, last_modify_time, name, route, icon, parent_id, sequence_number, valid) VALUES ('app-lbtp', 'isj', '2015-08-18 09:38:00', 'isj', '2015-08-18 09:38:00', '轮播图片', '/photo/carousel', 'image_black', 'app-apppz', '3', 'Y');
INSERT INTO pep_auth_menus (id, create_user_id, create_time, last_modify_user_id, last_modify_time, name, route, icon, parent_id, sequence_number, valid) VALUES ('app-tomcat-log', 'isj', '2015-08-18 09:38:00', 'isj', '2015-08-18 09:38:00', 'Tomcat日志', '/logview/tomcatLogs', 'event_note', '', '4', 'Y');

COMMIT;
