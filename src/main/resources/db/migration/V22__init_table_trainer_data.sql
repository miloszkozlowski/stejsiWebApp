create table trainer_data (
	id bigint unsigned auto_increment primary key,
	email varchar(100),
	when_created datetime not null,
	usuniety bit not null default false
);
