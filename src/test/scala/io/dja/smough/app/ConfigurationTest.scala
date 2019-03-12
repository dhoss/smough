package io.dja.smough.app

import org.mockito.ArgumentMatchersSugar
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSuite, MustMatchers}

class ConfigurationTest extends FunSuite
    with MockitoSugar
    with ArgumentMatchersSugar
    with MustMatchers {

  val expectedDbConfig = Map(
    "jdbcUrl" -> "jdbc:postgresql://localhost:5432/smough_test",
    "user" -> "smough_test",
    "password" -> "smough_test",
    "threadPoolSize" -> 10)
  val expectedServerConfig = Map("port" -> 8080, "bindAddress" -> "0.0.0.0")

  test("Config") {
    Configuration.jdbcUrl must equal(expectedDbConfig("jdbcUrl"))
    Configuration.dbUser must equal(expectedDbConfig("user"))
    Configuration.dbPassword must equal(expectedDbConfig("password"))
    Configuration.dbThreadPoolSize must equal(expectedDbConfig("threadPoolSize"))

    Configuration.apiServerPort must equal(expectedServerConfig("port"))
    Configuration.apiServerBindAddress must equal(expectedServerConfig("bindAddress"))
  }
}
