insert into users (id, name) values (4765, 'Jack'), (4766, 'Fred');

insert into accounts (id, owner_id, name) values (1673, 4765, 'Jack''s account'), (1674, 4766, 'Fred''s account');

insert into projects (id, account_id, name, active) values (55432, 1673, 'Flagship', true), (55431, 1673, 'Hovercraft', false);

insert into allocations (id, project_id, user_id, first_day, last_day) values (754, 55432, 4765, '2015-05-17', '2015-05-18'), (755, 55432, 4766, '2015-05-17', '2015-05-18');

insert into stories (id, project_id, name) values (876, 55432, 'An epic story'), (877, 55432, 'Another epic story');

insert into time_entries (id, project_id, user_id, date, hours) values (1534, 55432, 4765, '2015-05-17', 5), (2534, 55432, 4765, '2015-05-18', 3)