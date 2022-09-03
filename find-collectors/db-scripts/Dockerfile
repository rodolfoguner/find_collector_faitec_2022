FROM python:3.10 as migration

COPY . /app
RUN pip install psycopg2
WORKDIR /app
CMD python dump.py
