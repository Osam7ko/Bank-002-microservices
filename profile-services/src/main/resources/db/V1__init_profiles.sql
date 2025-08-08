create table if not exists profiles
(
  id                 bigserial primary key,
  user_id            varchar(64)  not null unique,
  first_name         varchar(60)  not null,
  last_name          varchar(60)  not null,
  other_name         varchar(60),
  gender             varchar(20),
  address            varchar(120),
  state_of_origin    varchar(60),
  phone_number       varchar(32),
  alt_phone_number   varchar(32),
  status             varchar(20),
  created_at         timestamp    not null default now(),
  modified_at        timestamp    not null default now(),
  version            bigint
);

create index if not exists idx_profiles_last_first on profiles(last_name, first_name);
create index if not exists idx_profiles_phone on profiles(phone_number);