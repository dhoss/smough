package io.dja.smough

import org.slf4j.{LoggerFactory, Logger => SLogger}

// TODO: rename
trait WithLogger {
  lazy val log: SLogger = LoggerFactory.getLogger(getClass)
}
