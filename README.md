# postgres2S3

#### create jar to run on spark cluster:
      $ sbt clean assembly

#### upload jar on s3:
      $ aws s3 cp target/scala-2.13/postgres2S3-02-07-2023_04_51.jar s3://bucket-name/app/

#### Run job on Spark EKS cluster:
      $ kubectl exec -ti -n spark spark-cluster-worker-0 -- spark-submit --master spark://spark-cluster-master-svc:7077 \
    --conf 'spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem' \
    --conf 'spark.hadoop.fs.s3a.bucket.all.committer.magic.enabled=true' \
    --conf 'spark.hadoop.fs.s3a.connection.maximum=1000' \
    --conf 'spark.driver.memory=8g' \
    --conf 'spark.executor.memory=20g' \
    --conf 'spark.executor.cores=6' \
    --conf 'spark.sql.shuffle.partitions=400' \
    --supervise \
    --deploy-mode cluster \
    --jars s3a://bucket-name/dependencies/spark-hadoop-cloud_2.12-3.3.2.jar \
    --class com.techmonad.main.Postgres2S3App \
      s3a://bucket-name/app/Postgres2S3App-30-06-2023_06_13.jar

