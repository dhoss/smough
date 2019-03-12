package io.dja.smough.app

import com.typesafe.config._

object Configuration {
  val conf = ConfigFactory.load()

  val jdbcUrl = conf.getString("database.jdbcUrl")
  val dbUser = conf.getString("database.user")
  // TODO: provide a mechanism to generate this and hash it
  val dbPassword = conf.getString("database.password")
  val dbThreadPoolSize = conf.getInt("database.threadPoolSize")

  val apiServerPort = conf.getInt("api-server.port")
  val apiServerBindAddress = conf.getString("api-server.bindAddress")
}