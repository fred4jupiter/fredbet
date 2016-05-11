create table info_content (info_name varchar(255) not null, locale_string varchar(10) not null, content LONGTEXT, primary key (info_name, locale_string));
