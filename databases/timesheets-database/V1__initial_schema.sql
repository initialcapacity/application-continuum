create table time_entries (
  id bigint not null auto_increment,
  project_id bigint,
  user_id bigint,
  date datetime,
  hours int,

  primary key (id)
)
engine = innodb
default charset = utf8mb4;