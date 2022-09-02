FROM postgres:14 as db

WORKDIR /db-scripts

COPY ./init-database.sh ./
COPY ./dump.sql ./
