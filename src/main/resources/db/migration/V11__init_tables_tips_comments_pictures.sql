create table tips (
	id bigint unsigned auto_increment primary key,
	body varchar(1000) not null,
	pic_id bigint unsigned,
	url varchar(1000),
	when_created datetime not null,
	usuniety bit not null default false
);
create table tip_pictures (
	id bigint unsigned auto_increment primary key,
	picture mediumblob,
	when_created datetime not null,
	usuniety bit not null default false
);
create table tip_comments (
	id bigint unsigned auto_increment primary key,
	author bigint unsigned null,
	body varchar(1000) not null,
	tip_id bigint unsigned,
	when_created datetime not null,
	usuniety bit not null default false
);
alter table tips add foreign key (pic_id) references tip_pictures(id);
alter table tip_comments add foreign key (tip_id) references tips(id);
