package SNS.Hstagram.repository;

import SNS.Hstagram.domain.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElasticRepository extends ElasticsearchRepository<PostDocument, Long> {
    // 커스텀 검색 메서드 작성 가능
    List<PostDocument> findByContentContaining(String keyword);
}

