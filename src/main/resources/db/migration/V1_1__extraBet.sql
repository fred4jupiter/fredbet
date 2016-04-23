create table EXTRA_BET (extra_bet_id bigint not null auto_increment, user_name varchar(255), FINAL_WINNER varchar(255), SEMI_FINAL_WINNER varchar(255), points integer, primary key (extra_bet_id));
create unique index EXTRA_BET_IDX1 on EXTRA_BET (user_name);
