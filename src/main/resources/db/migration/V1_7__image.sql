create table image_group (image_group_id bigint not null auto_increment, name varchar(100), version integer, primary key (image_group_id));

create table image_store (image_id bigint not null auto_increment, file_name varchar(100), image_group_id bigint, image_binary blob, thumb_image_binary blob, description varchar(400), version integer, primary key (image_id));

alter table image_store add constraint FK_img_group_id foreign key (image_group_id) references image_group (image_group_id);