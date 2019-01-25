#!/usr/bin/env bash
flyway -user=$1 -password=$2 -url=jdbc:postgresql:smough -locations=filesystem:src/main/resources/sql/ $3
