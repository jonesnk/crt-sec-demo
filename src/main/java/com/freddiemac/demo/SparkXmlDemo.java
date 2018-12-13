package com.freddiemac.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/*
 * Created by bgi056 on 12/13/16.
 * 350k in 10-15 mins
 * */

public class SparkXmlDemo implements Serializable {

    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PWD = "password";
    private static final String MYSQL_CONNECTION_URL = "jdbc:mysql://localhost/dwdr01s";

    private transient SparkConf conf;

    private SparkXmlDemo() {}

    private SparkXmlDemo(SparkConf conf) { this.conf = conf; }

    private void run() {
        JavaSparkContext sc = new JavaSparkContext(conf);
        //testMySql(sc);
        testXfdf(sc);
        //testDeal(sc);
        sc.stop();
        sc.close( );
    }

    private void testXfdf(JavaSparkContext sc){

        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);

        StructType schema = getSchema();

        //reads XML file into a dataset and writes contents to MySql table
        Dataset ds = sqlContext.read()
                .format("com.databricks.spark.xml")
                .schema(schema)
                .option("rootTag", "fields")
                .option("rowTag", "field")
                .option("mode", "DROPMALFORMED")
                .load("transformed-testing-13bf9f4d-d976-4b66-a49f-360e860e02b1.xml");

        System.out.println("The count "+ds.count());


        Properties dbProperties = new Properties();
        dbProperties.setProperty("user", MYSQL_USERNAME);
        dbProperties.setProperty("password", MYSQL_PWD);

        String where = "dwdr01s.TRIBAL_XFDF_DOC";

        ds.select("*")
                .withColumn("section", ds.col("_name"))
                .write()
                .mode(SaveMode.Overwrite)
                .jdbc(MYSQL_CONNECTION_URL, where, dbProperties);

    }


    private void testMySql(JavaSparkContext sc){

        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);

        StructType schema = getSchema();

        //reads XML file into a dataset and writes contents to MySql table
        Dataset dataset_mysql = sqlContext.read()
                .format("com.databricks.spark.xml")
                .schema(schema)
                .option("rootTag", "catalog")
                .option("rowTag", "book")
                .option("mode", "DROPMALFORMED")
                .load("books.xml");

        System.out.println("The count "+dataset_mysql.count());

        //Dataset ds2 = dataset_mysql.withColumn("my_title_extended", dataset_mysql.col("title"));

        //ds2.show();

        //dataset_mysql.select("_id", "title", "author").show();
        //dataset_mysql.count();
        //dataset_mysql.printSchema();
        //dataset_mysql.show();

       /*dataset_mysql.write()
               .format("jdbc")
               .mode(SaveMode.Append)
               .options(getOptions())
               .save();*/

        Properties dbProperties = new Properties();
        dbProperties.setProperty("user", MYSQL_USERNAME);
        dbProperties.setProperty("password", MYSQL_PWD);

        String where = "dwdr01s.BOOK";

        dataset_mysql.select("*")
                .write()
                .mode(SaveMode.Overwrite)
                .jdbc(MYSQL_CONNECTION_URL, where, dbProperties);

        /*dataset_mysql
                .write()
                .format("jdbc")
                .mode(SaveMode.Append)
                .options(getOptions())
                .save();*/

    }

    private void testDeal(JavaSparkContext sc){

        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);

        StructType schema = getSchema();

        //reads XML file into a dataset and writes contents to MySql table
        Dataset dataset_mysql = sqlContext.read()
                .format("com.databricks.spark.xml")
                //.schema(schema)
                .option("rowTag", "ADDRESS")
                .option("inferSchema", "true")
                .load("deal_sets.xml");

        System.out.println("The count "+dataset_mysql.count());


        //ds2.show();

        //dataset_mysql.select("_id", "title", "author").show();
        //dataset_mysql.count();
        dataset_mysql.printSchema();
        //dataset_mysql.show();

       /*dataset_mysql.write()
               .format("jdbc")
               .mode(SaveMode.Append)
               .options(getOptions())
               .save();*/

        Properties dbProperties = new Properties();
        dbProperties.setProperty("user", MYSQL_USERNAME);
        dbProperties.setProperty("password", MYSQL_PWD);

        String where = "dwdr01s.PROPERTY";

        dataset_mysql.select("*")
                .write()
                .mode(SaveMode.Overwrite)
                .jdbc(MYSQL_CONNECTION_URL, where, dbProperties);

        /*dataset_mysql
                .write()
                .format("jdbc")
                .mode(SaveMode.Append)
                .options(getOptions())
                .save();*/

    }

    public static void main(String[] args) {

        SparkConf conf = new SparkConf();

        conf.setAppName("Spark MySQL Connector");
        conf.setMaster("local[*]");

        SparkXmlDemo app = new SparkXmlDemo(conf);
        app.run();
    }

    private static Map<String, String> getOptions(){

        //Datasource options
        Map<String, String> options = new HashMap<String, String>();
        options.put("driver", MYSQL_DRIVER);
        options.put("url", MYSQL_CONNECTION_URL);
        options.put("dbtable", "BOOK");
        options.put("user", MYSQL_USERNAME);
        options.put("password", MYSQL_PWD);

        return options;

    }

    /*private static StructType getSchema(){

        StructType schema = DataTypes
                .createStructType(new StructField[] {
                        DataTypes.createStructField("_id", DataTypes.StringType, true),
                        DataTypes.createStructField("author", DataTypes.StringType, true),
                        DataTypes.createStructField("description", DataTypes.StringType, true),
                        DataTypes.createStructField("genre", DataTypes.StringType, true),
                        DataTypes.createStructField("price", DataTypes.DoubleType, true),
                        DataTypes.createStructField("publish_date", DataTypes.DateType, true),
                        DataTypes.createStructField("title", DataTypes.StringType, true)
                        DataTypes.createStructField("my_title_extended", DataTypes.StringType, true)
                });
        return schema;

    }*/

    private static StructType getSchema(){

        StructType schema = DataTypes
                .createStructType(new StructField[] {
                        DataTypes.createStructField("_name", DataTypes.StringType, true),
                        DataTypes.createStructField("value", DataTypes.StringType, true),
                        DataTypes.createStructField("value-richtext", DataTypes.StringType, true)
                });
        return schema;

    }

}
