package io.dja.smough.service

import io.dja.smough.{WithLogger, domain}
import io.dja.smough.database.PostStore
import io.dja.smough.domain.Post

import scala.collection.mutable

class PostService(val postStore: PostStore) extends WithLogger {
  // TODO: deflate post objects into a hashmap or maybe flatbuffer?
  private val postCache = mutable.HashMap[String, Post]()

  def loadPosts(): Unit = {
    log.info("Determining whether or not to load posts into memory")
    if (postCache.isEmpty) {
      log.info("Post cache is empty, loading posts")
      for (post <- postStore.retrieveAllFromDb()) {
        postCache += (post.slug -> post)
      }
      log.info("Loading posts complete.")
    }
  }

  def retrieveAllFromCache(): mutable.HashMap[String, Post] = {
    log.info("Retrieving posts from cache")
    postCache
  }

  def findBySlug(slug: String): Option[Post] = {
    log.info(s"Attempting to find ${slug} in cache")
    Option(postCache.getOrElseUpdate(slug, postStore.findBySlugFromDb(slug).get))
  }
}
