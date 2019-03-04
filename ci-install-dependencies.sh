#!/usr/bin/env bash

curl -L https://codeclimate.com/downloads/test-reporter/test-reporter-latest-linux-amd64 > ./cc-test-reporter &&
chmod +x ./cc-test-reporter &&
wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/5.2.4/flyway-commandline-5.2.4-linux-x64.tar.gz | tar xvz && sudo ln -s `pwd`/flyway-5.2.4/flyway /usr/local/bin || true &&
psql -U postgres < src/main/resources/sql/test-bootstrap.sql &&
# TODO: change credentials to travis tokens or something
./migrate-db.sh smough_test smough_test smough_test migrate