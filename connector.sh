curl -X POST \
http://localhost:8083/connectors \
-H 'Content-Type: application/json' \
-d '{
"name": "hdfs-sink",
"config": {
"connector.class": "io.confluent.connect.hdfs.HdfsSinkConnector",
"tasks.max": "1",
"topics": "reports",
"hdfs.url": "hdfs://namenode:9000",
"hadoop.conf.dir": "/usr/local/hadoop/etc/hadoop",
"hadoop.home": "/usr/local/hadoop",
"flush.size": "3",
"rotate.interval.ms": "1000"
}
}'
