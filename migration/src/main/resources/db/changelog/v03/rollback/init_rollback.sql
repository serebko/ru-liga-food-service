alter table customers
drop column coordinates;

alter table restaurants
drop column coordinates;

alter table restaurants
drop column name;

alter table orders alter column courier_id
set not null;