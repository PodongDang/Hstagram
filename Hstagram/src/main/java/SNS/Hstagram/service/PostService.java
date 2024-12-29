package SNS.Hstagram.service;

import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.repository.PostRepository;
import SNS.Hstagram.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 작성
    public void createPost(Long userId, String content, String imageUrl) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setImageUrl(imageUrl);
        postRepository.save(post);
    }

    // 특정 사용자 게시글 조회
    public List<Post> getPostsByUser(Long userId) {
        return postRepository.findByUserId(userId);
    }

    // 모든 게시글 조회 (최신순)
    public List<Post> getAllPosts() {
        return postRepository.findAllOrderedByCreatedAt();
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId);
        if (post != null) {
            postRepository.delete(post);
        } else {
            throw new EntityNotFoundException("Post not found");
        }
    }
}
