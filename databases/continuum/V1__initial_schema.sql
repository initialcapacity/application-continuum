create table users (
  id bigint(20) not null auto_increment,
  name varchar(256),

  primary key (id),
  unique key name (name)
)
engine = innodb
default charset = utf8;

create table accounts (
  id bigint(20) not null auto_increment,
  owner_id bigint(20),
  name varchar(256),

  primary key (id),
  unique key name (name),
  constraint foreign key (owner_id) references users (id)
)
engine = innodb
default charset = utf8;

create table projects (
  id bigint(20) not null auto_increment,
  account_id bigint(20),
  name varchar(256),

  primary key (id),
  unique key name (name),
  constraint foreign key (account_id) references accounts (id)
)
engine = innodb
default charset = utf8;

create table allocations (
  id bigint(20) not null auto_increment,
  project_id bigint(20),
  user_id bigint(20),
  first_day datetime,
  last_day datetime,

  primary key (id),
  constraint foreign key (project_id) references projects (id),
  constraint foreign key (user_id) references users (id)
)
engine = innodb
default charset = utf8;

create table stories (
  id bigint(20) not null auto_increment,
  project_id bigint(20),
  name varchar(256),

  primary key (id),
  constraint foreign key (project_id) references projects (id)
)
engine = innodb
default charset = utf8;

create table time_entries (
  id bigint(20) not null auto_increment,
  project_id bigint(20),
  user_id bigint(20),
  date datetime,
  hours int,

  primary key (id),
  constraint foreign key (project_id) references projects (id),
  constraint foreign key (user_id) references users (id)
)
engine = innodb
default charset = utf8;