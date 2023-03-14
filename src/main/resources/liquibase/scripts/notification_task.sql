-- liquibase formatted sql

-- changeset alexust:1
CREATE TABLE notification_task(
                                  id SERIAL,
                                  chat_id BIGINT,
                                  text varchar,
                                  date_time timestamp
)