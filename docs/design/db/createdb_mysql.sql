create database sakai default character set utf8;
create user sakai_user@'localhost' identified by 'your_password';
grant all on sakai.* to sakai_user@localhost;
