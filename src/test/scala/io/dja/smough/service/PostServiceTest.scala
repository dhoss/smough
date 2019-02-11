package io.dja.smough.service

import java.time.OffsetDateTime

import io.dja.smough.database.PostStore
import io.dja.smough.domain.Post
import org.mockito.ArgumentMatchersSugar
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.{times, verify, verifyNoMoreInteractions, when}

class PostServiceTest extends FunSuite
    with MockitoSugar
    with ArgumentMatchersSugar
    with BeforeAndAfter {

  val expectedPost = Post(
    None,
    "test post",
    Some("test-post"),
    "this is a test",
    1,
    Some(OffsetDateTime.now),
    Some(OffsetDateTime.now),
    Some(1))

  var postService: PostService = _
  val postStore = mock[PostStore]
  before {
    postService = new PostService(postStore)
    when(postStore.findBySlug(any[String])).thenReturn(Option(expectedPost))
    when(postStore.retrieveAll()).thenReturn(List(expectedPost))
    when(postStore.insert(any[Post])).thenReturn(1)
    when(postStore.update(any[Post])).thenReturn(1)
    when(postStore.delete(any[Int])).thenReturn(1)
  }

  // TODO: do we even need to check to make sure the cache is empty or anything besides the
  // verification to make sure the Store method is called and that there are no more interactions?
  test("Insert new post") {
    val postWithoutSlug = Post(
      title = expectedPost.title,
      body = expectedPost.body,
      parent = expectedPost.parent,
      author = expectedPost.author,
      createdOn = expectedPost.createdOn,
      updatedOn =  expectedPost.updatedOn
    )
    postService.insert(postWithoutSlug)
    verify(postStore, times(1)).insert(any[Post])
    verifyNoMoreInteractions(postStore)
    assert(1 == postService.retrieveAllFromCache().size)
    postService.insert(expectedPost)
  }

  test("Update a post") {
    postService.update(expectedPost)
    verify(postStore, times(1)).update(any[Post])
    verifyNoMoreInteractions(postStore)
    assert(1 == postService.retrieveAllFromCache().size)
    postService.insert(expectedPost)
  }

  test("Find by slug") {
    assert(Option(expectedPost) == postService.findBySlug("test-post"))
    verify(postStore).findBySlug(any[String])
  }

  test("Retrieve all from cache") {
    // should start out empty
    assert(
      Map() == postService.retrieveAllFromCache())
    postService.loadPosts()
    verify(postStore, times(1)).retrieveAll()
    verifyNoMoreInteractions(postStore)
    assert(
      Map(
        // TODO DEAL WITH THIS BETTER
        expectedPost.slug.get -> expectedPost) == postService.retrieveAllFromCache())
    // make sure to breakout if the cache isn't empty
    postService.loadPosts()
  }

  test("Delete a post") {
    postService.delete(expectedPost)
    verify(postStore, times(1)).delete(any[Int])
    verifyNoMoreInteractions(postStore)
    assert(0 == postService.retrieveAllFromCache().size)
    postService.delete(expectedPost)
  }
}
