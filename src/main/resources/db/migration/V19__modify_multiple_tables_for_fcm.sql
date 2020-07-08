alter table uzytkownicy drop column activation_key;
alter table tokens add column token_FCM varchar(255) null;
alter table treningi add column user_notified datetime null;
alter table tips add column users_notified datetime null;
