package com.springai.chatmemoryrepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;

import com.springai.chatmemoryrepository.chatmemory.ChatMemoryEntity;
import com.springai.chatmemoryrepository.chatmemory.ChatMemoryMongoCrudRepository;

public class MongodbChatMemoryRepository implements ChatMemoryRepository {

    private final ChatMemoryMongoCrudRepository chatMemoryMongoCrudRepository;

    private final Logger logger = LoggerFactory.getLogger(MongodbChatMemoryRepository.class);

    public MongodbChatMemoryRepository(ChatMemoryMongoCrudRepository chatMemoryMongoCrudRepository) {
        this.chatMemoryMongoCrudRepository = chatMemoryMongoCrudRepository;
    }

    @Override
    public List<String> findConversationIds() {
        List<String> conversationIds = new ArrayList<>();
        try {
            // return this.chatMemoryMongoCrudRepository.findAll().stream()
            // .map(chatMemory -> chatMemory.conversationId())
            // .distinct()
            // .collect(Collectors.toList());
            conversationIds = this.chatMemoryMongoCrudRepository.findDistinctConversationIdBy();
        } catch (Exception e) {
            logger.error("Error in getting Conversation IDs", e);
        }
        return conversationIds;
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        List<Message> chatMemoryMessageList = new ArrayList<>();
        try {
            List<ChatMemoryEntity> chatMemoryList = this.chatMemoryMongoCrudRepository
                    .findByConversationIdOrderByTimestampAsc(conversationId);
            chatMemoryMessageList = chatMemoryList.stream().map(ChatMemoryEntity::mapToMessage)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error in finding Message by conversation_id", e);
        }
        return chatMemoryMessageList;
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        List<ChatMemoryEntity> deletedChatMemoryEntityList = null;
        Instant now = Instant.now();
        AtomicInteger offset = new AtomicInteger(0);
        try {
            deletedChatMemoryEntityList = this.chatMemoryMongoCrudRepository
                    .findByConversationIdOrderByTimestampAsc(conversationId);
            List<ChatMemoryEntity> chatMemoryEntityList = messages.stream()
                    .map(message -> ChatMemoryEntity.mapToChatMemoryEntity(conversationId, message,
                            now.plusMillis(offset.getAndIncrement())))
                    .collect(Collectors.toList());
            this.deleteByConversationId(conversationId);
            this.chatMemoryMongoCrudRepository.saveAll(chatMemoryEntityList);
            logger.debug("Saved Message list to DB for conversation Id: {}", conversationId);
        } catch (Exception e) {
            logger.error("Error in saving message list to DB for conversation Id: {}", conversationId, e);
            if (deletedChatMemoryEntityList != null && !deletedChatMemoryEntityList.isEmpty()) {
                this.chatMemoryMongoCrudRepository.saveAll(deletedChatMemoryEntityList);
            }
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        try {
            Long deletedMessageCount = this.chatMemoryMongoCrudRepository.deleteByConversationId(conversationId);
            logger.debug("{} message deleted for conversation Id: {}", deletedMessageCount, conversationId);
        } catch (Exception e) {
            logger.error("Error in deleting conversation by conversationId: {}", conversationId, e);
        }
    }

}
