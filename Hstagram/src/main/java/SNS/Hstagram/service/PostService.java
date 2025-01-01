package SNS.Hstagram.service;

import SNS.Hstagram.domain.Post;
import SNS.Hstagram.domain.User;
import SNS.Hstagram.dto.PostDTO;
import SNS.Hstagram.repository.PostRepository;
import SNS.Hstagram.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 작성
    public void addPost(Long userId, String content, String imageUrl) {
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

    // 게시글 업데이트 (내용 및 이미지 수정)
    public void modifyPost(Long postId, String content, String imageUrl) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new EntityNotFoundException("게시글을 찾을 수 없습니다.");
        }

        post.setContent(content);
        post.setImageUrl(imageUrl);
        post.setUpdatedAt(java.time.LocalDateTime.now());
        postRepository.save(post);
    }

    // 특정 사용자 게시글 조회
    public List<Post> findUserPostsList(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    // 모든 게시글 조회 (최신순)
    public List<Post> findAllPostsList() {
        return postRepository.findAllOrderedByCreatedAt();
    }

    // 피드 조회
    public List<PostDTO> findUserFeedList(Long userId) {
        List<Post> posts = postRepository.findFeedPostsByUserId(userId);
        return posts.stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    // 게시글 삭제
    public void removePost(Long postId) {
        Post post = postRepository.findById(postId);
        if (post != null) {
            postRepository.delete(post);
        } else {
            throw new EntityNotFoundException("Post not found");
        }
    }

}
