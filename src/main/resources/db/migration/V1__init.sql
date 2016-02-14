-- The migrations are scripts in the form V<VERSION>__<NAME>.sql (with <VERSION> an underscore-separated version, e.g. ‘1’ or ‘2_1’)

create table app_user (user_id bigint not null auto_increment, created_at datetime, deletable bit not null, password varchar(255), user_name varchar(255), primary key (user_id));
create table bet (bet_id bigint not null auto_increment, goals_team_one integer, goals_team_two integer, points integer, user_name varchar(255), match_id bigint, primary key (bet_id));
create table matches (match_id bigint not null auto_increment, country_one integer, country_two integer, goals_team_one integer, goals_team_two integer, match_group varchar(255), kick_off_date datetime, stadium varchar(255), team_name_one varchar(255), team_name_two varchar(255), primary key (match_id));
create table rememberme_token (id bigint not null auto_increment, last_used datetime, series varchar(255), token_value varchar(255), username varchar(255), primary key (id));
create table user_roles (user_id bigint not null, roles varchar(255));
alter table app_user add constraint UK_user_name unique (user_name);
alter table bet add constraint FK_match_id foreign key (match_id) references matches (match_id);
alter table user_roles add constraint FK_user_id foreign key (user_id) references app_user (user_id);
