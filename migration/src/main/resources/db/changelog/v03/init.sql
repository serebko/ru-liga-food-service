alter table customers
add column coordinates varchar(30);

alter table restaurants
add column coordinates varchar(30);

alter table restaurants
add column name varchar(255);

alter table orders alter column courier_id drop not null;