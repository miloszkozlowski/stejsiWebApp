alter table uzytkownicy drop column activation_key;
alter table tokens add column token_FCM varchar(255);
alter table treningi add column user_notified datetime;
alter table tips add column users_notified datetime;
