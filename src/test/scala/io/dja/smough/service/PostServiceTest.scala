package io.dja.smough.service

import java.time.Instant

import io.dja.smough.database.PostStore
import io.dja.smough.domain.Post
import org.mockito.ArgumentMatchersSugar
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.{verify, when}

class PostServiceTest extends FunSuite
    with MockitoSugar
    with ArgumentMatchersSugar
    with BeforeAndAfter {

  val postStore = mock[PostStore]
  val expectedPost = Post(
    1, None, "test post", "test-post", "this is a test", 1, Instant.now, Instant.now)
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
    verify(postStore).retrieveAllFromDb()
    assert(
      Map(
        expectedPost.slug -> expectedPost) == postService.retrieveAllFromCache())
  }

}
