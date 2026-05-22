package com.springai.chatmemoryrepository.chatmemory;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "spring_ai_chat_memory")
public record ChatMemoryEntity(
                @Id String id,
                @Indexed String conversationId,
                String content,
                MessageType type,
                List<ToolResponse> responses,
                Instant timestamp) {

        public ChatMemoryEntity(String conversationId, String content, MessageType type, List<ToolResponse> responses,
                        Instant timestamp) {
                this(null, conversationId, content, type, responses, timestamp);
        }

        public Message mapToMessage() {
                Message chatMemoryMessage = null;
                switch (this.type()) {
                        case USER:
                                chatMemoryMessage = new UserMessage(this.content());
                                break;
                        case SYSTEM:
                                chatMemoryMessage = new SystemMessage(this.content());
                                break;
                        case ASSISTANT:
                                chatMemoryMessage = new AssistantMessage(this.content());
                                break;
                        case TOOL:
                                chatMemoryMessage = ToolResponseMessage.builder().responses(this.responses()).build();
                                break;
                        default:
                                chatMemoryMessage = new UserMessage(this.content());
                                break;
                }
                return chatMemoryMessage;
        }

        public static ChatMemoryEntity mapToChatMemoryEntity(String conversationId, Message message, Instant timestamp) {
                ChatMemoryEntity chatMemoryEntity = null;
                switch (message.getMessageType()) {
                        case TOOL:
                                ToolResponseMessage toolResponseMessage = (ToolResponseMessage) message;
                                List<ToolResponse> responses = toolResponseMessage.getResponses();
                                chatMemoryEntity = new ChatMemoryEntity(conversationId, null, message.getMessageType(),
                                                responses, timestamp);
                                break;
                        default:
                                chatMemoryEntity = new ChatMemoryEntity(conversationId, message.getText(),
                                                message.getMessageType(), null, timestamp);
                                break;
                }
                return chatMemoryEntity;
        }
}
