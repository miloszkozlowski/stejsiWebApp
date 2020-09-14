create table lokalizacje (
	id bigint unsigned auto_increment primary key,
	nazwa varchar(50) not null,
	adres varchar(100),
	domyslne bit not null default false,
	when_created datetime not null,
	usuniety bit not null default false
);
alter table treningi add column gdzie bigint unsigned;
alter table treningi add foreign key (gdzie) references lokalizacje(id);