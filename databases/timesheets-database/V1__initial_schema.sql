create table time_entries (
  id bigint(20) not null auto_increment,
  project_id bigint(20),
  user_id bigint(20),
  date datetime,
  hours int,

  primary key (id)
)
engine = innodb
default charset = utf8;