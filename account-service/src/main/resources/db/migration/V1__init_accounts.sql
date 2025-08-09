create table if not exists bank_accounts
(
  id              bigserial primary key,
  account_number  varchar(20) not null unique,
  profile_id      varchar(64) not null,      -- or user_id, your call
  display_name    varchar(180),              -- snapshot of holder name
  balance         numeric(19,2) not null default 0,
  status          varchar(20) not null default 'ACTIVE',
  created_at      timestamp not null default now(),
  modified_at     timestamp not null default now(),
  version         bigint
);

create index if not exists idx_accounts_profile on bank_accounts(profile_id);