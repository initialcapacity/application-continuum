create table allocations (
  id bigint not null auto_increment,
  project_id bigint,
  user_id bigint,
  first_day datetime,
  last_day datetime,

  primary key (id)
)
engine = innodb
default charset = utf8mb4;