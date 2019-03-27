package io.dja.smough.post

import io.dja.smough.Logger
import io.dja.smough.domain._

import scala.collection.mutable

// TODO: refactor this into a generic trait and make this an implementation of said trait
class PostCache(val postStore: PostStore) extends Logger {
  // TODO: deflate post objects into a hashmap or maybe flatbuffer?
  // TODO: this should be an injectable cache, even if the default is just
  // TODO: no matter what, this should be synchronized
  // a simple class that keeps things in an in memory HashMap
  private val postCache = mutable.HashMap[String, Post]()

  // TODO there's a solid chance I'd rather just load pageSize*2 on demand here
  def loadPosts(): Unit = {
    log.info("Determining whether or not to load posts into memory")
    if (cacheIsEmpty) {
      log.info("Post cache is empty, loading posts")
      // TODO: add pagination
      for (post <- postStore.retrieveAll()) {
        add(post)
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
      author = post.author,
      category = post.category
    )
    log.info(s"Checking to see if Post(${postWithSlug.slug}) exists in cache")
    // TODO: find a better way to deal with Optionals
    if (!cacheContains(postWithSlug.slug.get)) {
      log.info(s"Inserting Post(${postWithSlug.slug}) into database")
      postStore.insert(postWithSlug)

      log.info(s"Updating cache with Post(${postWithSlug.slug}")
      add(postWithSlug)
    }
    Result(s"Created `${postWithSlug.title}`")
  }

  // TODO: is IllegalArgumentException the best exception here?
  def delete(id: Int): Result = {
    val post = postStore.findById(id)
    post match {
      case Some(p) =>
        if (p.id.isDefined) {
          val id = p.id.get
          log.info(s"Deleting Post(${id})")
          remove(p)
          postStore.delete(id)
        }
      case None => throw new IllegalArgumentException(s"No such post ${id}")
    }
    Result(s"Deleted `${post.get.title}`")
  }

  def update(post: Post): Result = {
    log.info(s"Updating Post(${post.slug}) in database")
    postStore.update(post)

    log.info(s"Updating Post(${post.slug}) in cache")
    add(post)
    Result(s"Updated `${post.title}`")
  }

  // TODO: add pagination
  def retrieveAll(): List[Post] = {
    log.info("Retrieving posts from cache")
    postCache.synchronized(postCache.values.toList)
  }

  // TODO: calling synchronized everywhere is probably not good
  def findBySlug(slug: String): Option[Post] = {
    log.info(s"Attempting to find Post(${slug}) in cache")
    Option(
      postCache.synchronized(
        postCache.getOrElseUpdate(slug, postStore.findBySlug(slug).get)))
  }

  // TODO: Pagination
  def findByYear(year: Int): List[Post] = postStore.findByYear(year)
  def findByMonth(year: Int, month: Int): List[Post] = postStore.findByMonth(year, month)
  def findByDay(year: Int, month: Int, day: Int): List[Post] = postStore.findByDay(year, month, day)

  private def cacheIsEmpty(): Boolean =
    postCache.synchronized(postCache.isEmpty)

  private def cacheContains(slug: String): Boolean =
    postCache.synchronized(
      postCache.contains(slug))

  private def add(post: Post) =
    postCache.synchronized(
      postCache += (post.slug.get -> post))

  private def remove(post: Post) =
    postCache.synchronized(
      postCache -= post.slug.get)
}