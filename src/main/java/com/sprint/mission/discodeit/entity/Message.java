package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  @Column(name = "content", nullable = false)
  private String content;

  @Setter(AccessLevel.NONE)
  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Setter(AccessLevel.NONE)
  @ManyToOne
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Setter(AccessLevel.NONE)
  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MessageAttachment> attachments = new ArrayList<>();

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public void addAttachment(MessageAttachment attachment) {
    this.attachments.add(attachment);
  }

  public void removeAttachment(MessageAttachment attachment) {
    this.attachments.remove(attachment);
  }

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    this.content = content;
    this.channel = channel;
    this.author = author;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }
}