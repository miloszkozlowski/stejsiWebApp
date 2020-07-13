create table tip_read_status (
	user_id bigint unsigned not null,
	tip_id bigint unsigned not null,
	foreign key (user_id) references uzytkownicy(id),
	foreign key (tip_id) references tips(id)
);