package com.techmonad.main

import org.apache.spark.sql.{SaveMode, SparkSession}

object Postgres2S3App {

  def main(args: Array[String]) = {

    implicit val spark: SparkSession = SparkSession
      .builder()
      .appName("Postgres2S3App")
      .master("local[*]")
      .getOrCreate()

    val metaTable = s"(select * from analysis_meta where id >= 1220572171319017472 and id < 1620572171319017473) as meta"


    val metaDf =
      spark
        .read
        .format("jdbc")
        .option("numPartitions", "2000")
        .option("lowerBound", "1220572171319017472")
        .option("upperBound", "1620572171319017473")
        .option("partitionColumn", "id")
        .option("dbtable", metaTable)
        .option("driver", "org.postgresql.Driver")
        .option("url", "jdbc:postgresql://db_url/analysis_db?user=postgres&password=postgres")
        .load()

    val textTable = s"(select id , text from analysis_data where id >= 1220572171319017472 and id < 1620572171319017473) as data"

    val textDf =
      spark
        .read
        .format("jdbc")
        .option("numPartitions", "2000")
        .option("lowerBound", "1220572171319017472")
        .option("upperBound", "1620572171319017473")
        .option("partitionColumn", "id")
        .option("dbtable", textTable)
        .option("driver", "org.postgresql.Driver")
        .option("url", "jdbc:postgresql://db_url/analysis_db?user=postgres&password=postgres")
        .load()


    val df = metaDf.join(textDf, "id")

    df
      .write
      .partitionBy("lang", "month") // partition by language and month
      .mode(SaveMode.Append)
      .parquet(s"s3a://bucket-name/analysis-data")
  }

}

