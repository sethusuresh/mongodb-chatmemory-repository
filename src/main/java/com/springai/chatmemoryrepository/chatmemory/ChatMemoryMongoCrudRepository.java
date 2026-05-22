package com.springai.chatmemoryrepository.chatmemory;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMemoryMongoCrudRepository extends MongoRepository<ChatMemoryEntity, String> {

    public List<ChatMemoryEntity> findByConversationIdOrderByTimestampAsc(String conversationId);

    public Long deleteByConversationId(String conversationId);

    public List<String> findDistinctConversationIdBy();
}
