CREATE TABLE IF NOT EXISTS Currencies (
    ID       SERIAL PRIMARY KEY,
    Code     VARCHAR UNIQUE NOT NULL,
    FullName VARCHAR        NOT NULL,
    Sign     VARCHAR        NOT NULL
);

CREATE TABLE IF NOT EXISTS ExchangeRates (
    ID               SERIAL PRIMARY KEY,
    BaseCurrencyId   INTEGER REFERENCES currencies (id),
    TargetCurrencyId INTEGER REFERENCES currencies (id),
    Rate             DECIMAL NOT NULL
);