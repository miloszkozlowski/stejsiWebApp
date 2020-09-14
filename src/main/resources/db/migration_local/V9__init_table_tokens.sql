create table tokens (
	id bigint unsigned auto_increment primary key,
	owner bigint unsigned not null,
	token_body varchar(32) not null,
	device_id varchar(255) not null,
	active bit not null default true,
	when_created datetime not null,
	usuniety bit not null default false
);
alter table tokens add foreign key (owner) references uzytkownicy(id);