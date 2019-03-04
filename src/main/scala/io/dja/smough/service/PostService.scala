package io.dja.smough.service

import io.dja.smough.WithLogger
import io.dja.smough.database.PostStore
import io.dja.smough.domain._

import scala.collection.mutable

class PostService(val postStore: PostStore) extends WithLogger {
  // TODO: deflate post objects into a hashmap or maybe flatbuffer?
  // TODO: this should be an injectable cache, even if the default is just
  // TODO: no matter what, this should be synchronized
  // a simple class that keeps things in an in memory HashMap
  private val postCache = mutable.HashMap[String, Post]()

  def loadPosts(): Unit = {
    log.info("Determining whether or not to load posts into memory")
    if (cacheIsEmpty) {
      log.info("Post cache is empty, loading posts")
      // TODO: add pagination
      for (post <- postStore.retrieveAll()) {
        addToCache(post)
      }
      log.info("Loading posts complete.")
    }
    log.info("Cache is not empty")
  }

  // TODO: deal with exceptions
  def insert(post: Post): Result = {
    // TODO: There has to be a better way to do this
    val postWithSlug = Post(
      parent = post.parent,
      title = post.title,
      slug = Some(post.title.toSlug),
      body = post.body,
      author = post.author
    )
    log.info(s"Checking to see if Post(${postWithSlug.slug}) exists in cache")
    // TODO: find a better way to deal with Optionals
    if (!cacheContains(postWithSlug.slug.get)) {
      log.info(s"Inserting Post(${postWithSlug.slug}) into database")
      postStore.insert(postWithSlug)

      log.info(s"Updating cache with Post(${postWithSlug.slug}")
      addToCache(postWithSlug)
    }
    Result(s"Created \"${postWithSlug.title}\"")
  }

  // TODO: is IllegalArgumentException the best exception here?
  def delete(id: Int): Result = {
    val post = postStore.findById(id)
    post match {
      case Some(p) =>
        if (p.id.isDefined) {
          val id = p.id.get
          log.info(s"Deleting Post(${id})")
          removeFromCache(p)
          postStore.delete(id)
        }
      case None => throw new IllegalArgumentException(s"No such post ${id}")
    }
    Result(s"Deleted ${post.get.title}")
  }

  // TODO: do we want to pass in the slug and a map of fields to be changed?
  // or an UpdatePost object?
  def update(post: Post): Result = {
    log.info(s"Updating Post(${post.slug}) in database")
    postStore.update(post)

    log.info(s"Updating Post(${post.slug}) in cache")
    addToCache(post)
    Result(s"Updated ${post.title}")
  }

  // TODO: add pagination
  def retrieveAllFromCache(): mutable.HashMap[String, Post] = {
    log.info("Retrieving posts from cache")
    postCache.synchronized(postCache)
  }

  def findBySlug(slug: String): Option[Post] = {
    log.info(s"Attempting to find Post(${slug}) in cache")
    Option(
      postCache.synchronized(
        postCache.getOrElseUpdate(slug, postStore.findBySlug(slug).get)))
  }

  private def cacheIsEmpty(): Boolean = postCache.synchronized(postCache.isEmpty)

  private def cacheContains(slug: String): Boolean =
    postCache.synchronized(
      postCache.contains(slug))

  private def addToCache(post: Post) =
    postCache.synchronized(
      postCache += (post.slug.get -> post))

  private def removeFromCache(post: Post) =
    postCache.synchronized(
      postCache -= post.slug.get)
}
