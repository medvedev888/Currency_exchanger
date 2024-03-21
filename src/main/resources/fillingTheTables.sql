INSERT INTO currencies(code, fullname, sign) VALUES ('USD', 'US Dollar', '$');
INSERT INTO currencies(code, fullname, sign) VALUES ('EUR', 'Euro', '€');
INSERT INTO currencies(code, fullname, sign) VALUES ('RUB', 'Russian Ruble', '₽');
INSERT INTO currencies(code, fullname, sign) VALUES ('UAH', 'Hryvnia', '₴');
INSERT INTO currencies(code, fullname, sign) VALUES ('KZT', 'Tenge', '₸');
INSERT INTO currencies(code, fullname, sign) VALUES ('GBP', 'Pound Sterling', '£');

insert into exchangerates (basecurrencyid, targetcurrencyid, rate) values (1, 2, 0.92);
insert into exchangerates (basecurrencyid, targetcurrencyid, rate) values (1, 3, 92.40);
insert into exchangerates (basecurrencyid, targetcurrencyid, rate) values (1, 4, 38.89);
insert into exchangerates (basecurrencyid, targetcurrencyid, rate) values (1, 5, 450.45);
insert into exchangerates (basecurrencyid, targetcurrencyid, rate) values (1, 6, 0.79);