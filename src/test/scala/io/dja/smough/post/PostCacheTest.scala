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

  var postService: PostCache = _
  val postStore = mock[PostStore]
  before {
    postService = new PostCache(postStore)
    when(postStore.findBySlug(any[String])).thenReturn(Option(expectedPost))
    when(postStore.findById(any[Int])).thenReturn(Option(expectedPost))
    when(postStore.retrieveAll()).thenReturn(List(expectedPost))
    when(postStore.insert(any[Post])).thenReturn(1)
    when(postStore.update(any[Post])).thenReturn(1)
    when(postStore.delete(any[Int])).thenReturn(1)
  }

  test("Retrieve all from cache") {
    // should start out empty
    Map.empty[String, Post] must equal(postService.retrieveAllFromCache())
    postService.loadPosts()
    verify(postStore, times(1)).retrieveAll()
    verifyNoMoreInteractions(postStore)
    Map(
      expectedPost.slug.get -> expectedPost) must equal(
      postService.retrieveAllFromCache())
    // make sure to breakout if the cache isn't empty
    postService.loadPosts()
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
    postService.insert(postWithoutSlug)
    verify(postStore, times(1)).insert(any[Post])
    verifyNoMoreInteractions(postStore)
    1 must equal(postService.retrieveAllFromCache().size)
    postService.insert(expectedPost)
  }

  test("Update a post") {
    postService.update(expectedPost)
    verify(postStore, times(1)).update(any[Post])
    verifyNoMoreInteractions(postStore)
    1 must equal(postService.retrieveAllFromCache().size)
    postService.insert(expectedPost)
  }

  test("Find by slug") {
    Option(expectedPost) must equal(
      postService.findBySlug("test-post"))
    verify(postStore).findBySlug(any[String])
  }

  test("Delete a post") {
    postService.delete(expectedPost.id.get)
    verify(postStore, times(1)).delete(any[Int])
    0 must equal(postService.retrieveAllFromCache().size)
  }
}