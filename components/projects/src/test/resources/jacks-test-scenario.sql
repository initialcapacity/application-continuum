insert into users (id, name) values (4765, 'Jack'), (4766, 'Fred');

insert into accounts (id, owner_id, name) values (1673, 4765, 'Jack''s account'), (1674, 4766, 'Fred''s account');

insert into projects (id, account_id, name, active) values (55432, 1673, 'Flagship', true), (55431, 1673, 'Hovercraft', false);