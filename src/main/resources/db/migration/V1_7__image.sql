create table image_group (image_group_id bigint not null auto_increment, name varchar(100), version integer, primary key (image_group_id));
alter table image_group add constraint UK_name unique (name);

create table image_store (image_id bigint not null auto_increment, image_group_id bigint, image_binary blob, thumb_image_binary blob, description varchar(400), version integer, primary key (image_id));

alter table image_store add constraint FK_img_group_id foreign key (image_group_id) references image_group (image_group_id);