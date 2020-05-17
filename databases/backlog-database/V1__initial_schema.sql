create table stories (
  id bigint not null auto_increment,
  project_id bigint,
  name varchar(256),

  primary key (id)
)
engine = innodb
default charset = utf8mb4;