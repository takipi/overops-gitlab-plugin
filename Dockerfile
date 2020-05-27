FROM openjdk:8-slim

RUN apt update && apt install -y wget

ARG JAR_FILE
ARG SCRIPT_FILE

COPY ${JAR_FILE} /opt
COPY ${SCRIPT_FILE} /opt

WORKDIR /opt
ENTRYPOINT /bin/bash