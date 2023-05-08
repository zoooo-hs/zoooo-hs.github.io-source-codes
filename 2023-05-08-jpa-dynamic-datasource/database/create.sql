-- MariaDB DB CREATE SQL

create database if not exists bank;
use bank;

create table if not exists CUSTOMER(
    ID int(10) not null auto_increment primary key,
    NAME varchar(10) not null,
    RNN varchar(13) not null
);


alter table CUSTOMER add unique uk_customer_rnn (rnn);