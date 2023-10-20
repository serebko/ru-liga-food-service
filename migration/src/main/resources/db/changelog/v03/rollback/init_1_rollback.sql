update customers set coordinates = null
where id = 3;

update customers set coordinates = null
where id = 4;

update restaurants set coordinates = null
where id = 3;

update restaurants set coordinates = null
where id = 4;

update restaurants set name = null
where id = 3;

update restaurants set name = null
where id = 4;

update orders set status = 'active'
where  id = 3;

update orders set status = 'complete'
where  id = 4;

update restaurants set status = 'active'
where  id = 3;

update restaurants set status = 'complete'
where  id = 4;

update couriers set status = 'complete'
where  id = 3;

update couriers set status = 'active'
where  id = 4;