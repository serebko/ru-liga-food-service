update customers set coordinates='16564564546'
where id = 3;

update customers set coordinates='56451684534'
where id = 4;

update restaurants set coordinates='45548989222'
where id = 3;

update restaurants set coordinates='4566168498'
where id = 4;

update restaurants set name = 'Vertigo'
where id = 3;

update restaurants set name = 'У дяди Юры'
where id = 4;

update orders set status = 'created'
where  id = 3;

update orders set status = 'accepted'
where  id = 4;

update restaurants set status = 'preparing'
where  id = 3;

update restaurants set status = 'denied'
where  id = 4;

update couriers set status = 'pending'
where  id = 3;

update couriers set status = 'picking'
where  id = 4;