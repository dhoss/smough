package io.dja.smough.post

import io.dja.smough.domain.Post
import io.dja.smough.test.Fixtures._
import org.mockito.ArgumentMatchersSugar
import org.mockito.Mockito.{times, verify, verifyNoMoreInteractions, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSuite, MustMatchers}

class PostCacheTest extends FunSuite
    with MockitoSugar
    with ArgumentMatchersSugar
    with BeforeAndAfter
    with MustMatchers {

  var postCache: PostCache = _
  val postStore = mock[PostStore]
  before {
    postCache = new PostCache(postStore)
    when(postStore.findBySlug(any[String])).thenReturn(Option(expectedPost))
    when(postStore.findById(any[Int])).thenReturn(Option(expectedPost))
    when(postStore.retrieveAll()).thenReturn(List(expectedPost))
    when(postStore.insert(any[Post])).thenReturn(1)
    when(postStore.update(any[Post])).thenReturn(1)
    when(postStore.delete(any[Int])).thenReturn(1)
  }

  test("Retrieve all from cache") {
    // should start out empty
    List.empty[Post] must equal(postCache.retrieveAll())
    postCache.loadPosts()
    verify(postStore, times(1)).retrieveAll()
    verifyNoMoreInteractions(postStore)
    List(expectedPost) must equal(postCache.retrieveAll())
    // make sure to breakout if the cache isn't empty
    postCache.loadPosts()
  }

  // TODO: do we even need to check to make sure the cache is empty or anything besides the
  // verification to make sure the Store method is called and that there are no more interactions?
  test("Insert new post") {
    val postWithoutSlug = Post(
      title = expectedPost.title,
      body = expectedPost.body,
      parent = expectedPost.parent,
      author = expectedPost.author,
      category = expectedPost.category,
      createdOn = expectedPost.createdOn,
      updatedOn =  expectedPost.updatedOn
    )
    postCache.insert(postWithoutSlug)
    verify(postStore, times(1)).insert(any[Post])
    verifyNoMoreInteractions(postStore)
    1 must equal(postCache.retrieveAll().size)
    postCache.insert(expectedPost)
  }

  test("Update a post") {
    postCache.update(expectedPost)
    verify(postStore, times(1)).update(any[Post])
    verifyNoMoreInteractions(postStore)
    1 must equal(postCache.retrieveAll().size)
    postCache.insert(expectedPost)
  }

  test("Find by slug") {
    Option(expectedPost) must equal(
      postCache.findBySlug("test-post"))
    verify(postStore).findBySlug(any[String])
  }

  test("Delete a post") {
    postCache.delete(expectedPost.id.get)
    verify(postStore, times(1)).delete(any[Int])
    0 must equal(postCache.retrieveAll().size)
  }
}