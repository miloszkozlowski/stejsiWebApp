create table uzytkownicy (
	id bigint unsigned auto_increment primary key,
	imie varchar(30) not null,
	nazwisko varchar(50) not null,
	email varchar(200) not null unique,
	activation_key varchar(16),
	aktywny bit not null default false,
	usuniety bit not null default false,
	when_created datetime not null
);
create table rodzaje_pakietow (
	id bigint unsigned auto_increment primary key,
	nazwa varchar(30) not null,
	opis varchar(1000),
	ilosc_treningow int not null,
	dlugosc_minuty int not null,
	days_valid int unsigned not null,
	cena decimal unsigned,
	aktywny bit not null default false,
	usuniety bit not null default false,
	when_created datetime not null
);

create table pakiety (
	id bigint unsigned auto_increment primary key,
	owner bigint unsigned not null,
	rodzaj_id bigint unsigned,
	usuniety bit not null default false,
	when_created datetime not null
);
alter table pakiety add foreign key (owner) references uzytkownicy(id);
alter table pakiety add foreign key (rodzaj_id) references rodzaje_pakietow(id);
create table treningi (
	id bigint unsigned auto_increment primary key,
	termin datetime not null,
	kiedy_odbyty datetime,
	pakiet_id bigint unsigned,
	usuniety bit not null default false,
	when_created datetime not null
);
alter table treningi add foreign key (pakiet_id) references pakiety(id);

