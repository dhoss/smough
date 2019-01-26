package io.dja.smough.service

import java.time.Instant

import io.dja.smough.database.PostStore
import io.dja.smough.domain.Post
import org.mockito.ArgumentMatchersSugar
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.when

class PostServiceTest extends FunSuite with MockitoSugar with ArgumentMatchersSugar {
  val postStore = mock[PostStore]
  val expectedPost = Post(1, None, "test post", "test-post", "this is a test", 1, Instant.now, Instant.now)
  val postService = new PostService(postStore)
  when(postStore.findBySlugFromDb(any[String])).thenReturn(Option(expectedPost))

  test("Find by slug from db") {
    assert(Option(expectedPost) == postService.findBySlug("test-post"))
  }

}
