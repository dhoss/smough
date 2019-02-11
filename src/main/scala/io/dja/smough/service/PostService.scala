package io.dja.smough.service

import io.dja.smough.WithLogger
import io.dja.smough.database.PostStore
import io.dja.smough.domain.Post

import scala.collection.mutable

class PostService(val postStore: PostStore) extends WithLogger {
  // TODO: deflate post objects into a hashmap or maybe flatbuffer?
  // TODO: this should be an injectable cache, even if the default is just
  // a simple class that keeps things in an in memory HashMap
  private val postCache = mutable.HashMap[String, Post]()

  def loadPosts(): Unit = {
    log.info("Determining whether or not to load posts into memory")
    if (postCache.isEmpty) {
      log.info("Post cache is empty, loading posts")
      // TODO: add pagination
      for (post <- postStore.retrieveAll()) {
        addToCache(post)
      }
      log.info("Loading posts complete.")
    }
    log.info("Cache is not empty")
  }

  def insert(post: Post): Unit = {
    log.info(s"Checking to see if Post(${post.slug}) exists in cache")
    if (!postCache.contains(post.slug)) {
      log.info(s"Inserting Post(${post.slug}) into database")
      postStore.insert(post)

      log.info(s"Updating cache with Post(${post.slug}")
      addToCache(post)
    }
  }

  def delete(post: Post): Unit = {
    if (post.id.isDefined) {
      val id = post.id.get
      log.info(s"Deleting Post(${id})")
      removeFromCache(post)
      postStore.delete(id)
    }
  }

  def update(post: Post): Unit = {
    log.info(s"Updating Post(${post.slug}) in database")
    postStore.update(post)

    log.info(s"Updating Post(${post.slug}) in cache")
    addToCache(post)
  }

  // TODO: add pagination
  def retrieveAllFromCache(): mutable.HashMap[String, Post] = {
    log.info("Retrieving posts from cache")
    postCache
  }

  def findBySlug(slug: String): Option[Post] = {
    log.info(s"Attempting to find Post(${slug}) in cache")
    Option(postCache.getOrElseUpdate(slug, postStore.findBySlug(slug).get))
  }

  private def addToCache(post: Post) = {
    postCache += (post.slug -> post)
  }

  private def removeFromCache(post: Post) = {
    postCache -= post.slug
  }
}
