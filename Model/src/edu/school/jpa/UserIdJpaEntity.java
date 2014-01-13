package edu.school.jpa;

import javax.persistence.Column;

public abstract class UserIdJpaEntity extends AbstractJpaEntity {
  public UserIdJpaEntity() {
    super();
  }
  public abstract Integer getUserId();
  public abstract void setUserId(Integer userId);
}
