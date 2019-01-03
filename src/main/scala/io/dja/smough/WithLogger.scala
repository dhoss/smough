package io.dja.smough

import org.slf4j.{LoggerFactory, Logger => SLogger}

trait WithLogger {
  lazy val log: SLogger = LoggerFactory.getLogger(getClass)
}
