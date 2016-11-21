INSERT INTO isj_rules
(id, create_user_id, create_time, last_modify_user_id, last_modify_time, catalogue, rule_name, rule)
VALUES
('sdf1', 'pep', '2016-10-11 09:38:00', 'pep', '2016-10-11 09:38:00', 'SAME_DAY_FILTER', '当日挂号学科及挂号级别约束', '!#isAppointment && (@sameDayRule.underDepts(#doctor, ''921'', ''1265'', ''1924'', ''1925'', ''1959'') || @sameDayRule.regLvIn(#doctor, "M", "N"))');


