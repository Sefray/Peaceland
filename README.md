# Peaceland

The goal of the project is to setup a full infrastructure handling the needs of
the country of Peaceland. Drones, called peacewatcher, patrol the country to
find people that are not at peace. These drones send reports that may trigger
an alert. When alerts are triggered peacemakers will quickly intervene. Finally
statistics on the reports can be computed.

## Infrastructure

The drones are for now simulated with a simple program in `./peacewatcher`.
They send reports using [Kafka](https://kafka.apache.org/). Reports are handled
by `office` programs that create alerts when necessary, pushing the alerts in
another Kafka topic. The alerts are then dispatched using webhooks to the
service of your choosing (discord, slack, teams ...) by
`./peacemaker-connector`.

Secondly a connector takes the messages from Kafka and sends them in batch to
[HDFS](https://hadoop.apache.org/docs/r1.2.1/hdfs_design.html). The `./stats`
program can then be run, it uses [Spark](https://spark.apache.org/) to create
statistics from the reports stored in HDFS.

## Run the project

Everything in the infrastructure is contained in dockers. To run all you can
use
```bash
42sh$ docker-compose up -d
```

For a more slow start you can use
```bash
42sh$ docker-compose up -d hdfs # for HDFS services only
42sh$ docker-compose up -d spark # for spark
42sh$ docker-compose up -d kafka # for kafka
```
And then when those 3 main components are setup run
```bash
42sh$ docker-compose up -d peacland # for the drones and the offices and the connectors
42sh$ docker-compose up stats # for the stats
```
