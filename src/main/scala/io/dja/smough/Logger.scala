package io.dja.smough

import org.slf4j.{LoggerFactory, Logger => SLogger}

// TODO: rename
trait Logger {
  lazy val log: SLogger = LoggerFactory.getLogger(getClass)
}
