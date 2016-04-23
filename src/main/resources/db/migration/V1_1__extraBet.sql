create table extra_bet (extra_bet_id bigint not null auto_increment, user_name varchar(255), final_winner varchar(255), semi_final_winner varchar(255), points integer, primary key (extra_bet_id));
create unique index extra_bet_idx1 on extra_bet (user_name);
