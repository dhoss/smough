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

  val postStore = mock[PostStore]
  val expectedPost = Post(
    None,
    "test post",
    "test-post",
    "this is a test",
    1,
    Some(OffsetDateTime.now),
    Some(OffsetDateTime.now),
    Some(1))
  var postService: PostService = _
  before {
    postService = new PostService(postStore)
    when(postStore.findBySlugFromDb(any[String])).thenReturn(Option(expectedPost))
    when(postStore.retrieveAllFromDb()).thenReturn(List(expectedPost))
  }

  test("Find by slug") {
    assert(Option(expectedPost) == postService.findBySlug("test-post"))
    verify(postStore).findBySlugFromDb(any[String])
  }

  test("Retrieve all from cache") {
    // should start out empty
    assert(
      Map() == postService.retrieveAllFromCache())
    postService.loadPosts()
    verify(postStore, times(1)).retrieveAllFromDb()
    verifyNoMoreInteractions(postStore)
    assert(
      Map(
        expectedPost.slug -> expectedPost) == postService.retrieveAllFromCache())
    // make sure to breakout if the cache isn't empty
    postService.loadPosts()
  }
}
