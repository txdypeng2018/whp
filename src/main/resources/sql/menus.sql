INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-ywtj', '业务统计', '/statistics/statisticsView', 'assessment', null, '0');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-yjfk', '意见反馈', '/feedback/operationView', 'rate_review_black', null, '1');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-apppz', 'APP 配置', '/appSettings', 'phonelink_setup', null, '2');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-ghgz', '规则设置', '/registration/rulesn', 'description_black', 'app-apppz', '0');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-wxts', '温馨提示', '/prompt/tips', 'info_black', 'app-apppz', '1');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-yydh', '医院导航', '/hospitalnavigation/build', 'place_black', 'app-apppz', '2');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-lbtp', '轮播图片', '/photo/carousel', 'image_black', 'app-apppz', '3');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-tomcat-log', 'Tomcat日志', '/logview/tomcatLogs', 'event_note', 'app-logs', '0');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-logs', '日志集中营', '/logview', 'assignment', null, '3');
INSERT INTO pep_auth_menus (id, name, route, icon, parent_id, sequence_number) VALUES ('app-kfcx', '客服查询', '/customerQuery/customer', 'search_black', null, '5');

COMMIT;
