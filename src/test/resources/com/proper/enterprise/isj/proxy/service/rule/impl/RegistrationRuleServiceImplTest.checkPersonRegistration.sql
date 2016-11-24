INSERT INTO isj_rules
(id, create_user_id, create_time, last_modify_user_id, last_modify_time, catalogue, rule)
VALUES
('r1', 'pep', '2015-08-18 09:38:00', 'pep', '2015-08-18 09:38:00', 'REG_RES', '@personRule.isChild(#idCard) && @commonRule.in(#deptId, "0013","0014","0018","0019","0020","0021","0022","0023","0024","0036","0047","0049","0054","0055","0086","0088","0094","0095","0096","0098","0099","0101","0156","0157","0158","0159","0170","0171","0172","0184","0187","0189","0190","0193","0194","0197","0201","0202","0204","0214","0219","0220","0221","0222","0223","0224","0228","0237","0249","0251","0252","0258","0282","0283","0290","0291","0294","0342","0397","0414","0417","0419","0420","0421","0427","M149") ? "当前科室不能选择儿童做为就诊人" : ""');

INSERT INTO isj_rules
(id, create_user_id, create_time, last_modify_user_id, last_modify_time, catalogue, rule)
VALUES
('r2', 'pep', '2015-08-18 09:38:00', 'pep', '2015-08-18 09:38:00', 'REG_RES', '@personRule.isAdult(#idCard) && @commonRule.in(#deptId, "0011","0090","0091","0092","0093","0102","0175","0176","0177","0178","0179","0180","0181","0182","0183","0213","0227","0229","0231","0246","0285","0286") ? "当前科室不能选择成人做为就诊人" : ""');

INSERT INTO isj_rules
(id, create_user_id, create_time, last_modify_user_id, last_modify_time, catalogue, rule)
VALUES
('r3', 'pep', '2015-08-18 09:38:00', 'pep', '2015-08-18 09:38:00', 'REG_RES', '@personRule.isMale(#idCard) && @commonRule.in(#deptId, "0101","0167","0168","0205") ? "当前科室不能选择男性就诊人" : ""');

INSERT INTO isj_rules
(id, create_user_id, create_time, last_modify_user_id, last_modify_time, catalogue, rule)
VALUES
('r4', 'pep', '2015-08-18 09:38:00', 'pep', '2015-08-18 09:38:00', 'REG_RES', '@personRule.isFemale(#idCard) && @commonRule.in(#deptId, "0204") ? "当前科室不能选择女性就诊人" : ""');
