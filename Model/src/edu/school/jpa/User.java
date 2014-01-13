package edu.school.jpa;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "User.findAll", query = "select o from User o"),
  @NamedQuery(name = "User.findLoginName", query = "select o from User o where o.loginName = :loginName")
})
@Table(name = "\"user\"")
public class User extends AbstractJpaEntity implements Serializable {
  @Column(name="FIRST_NAME")
  private String firstName;
  @Id
  @Column(nullable = false)
  private Integer id;
  @Column(name="LOGIN_NAME", nullable = false)
  private String loginName;
  @Column(name="PASSWORD_HASH", nullable = false)
  private String passwordHash;
  private String role;
  private String surname;

  public User() {
  }

  public User(String firstName, Integer id, String loginName, String passwordHash, String role, String surname) {
    this.firstName = firstName;
    this.id = id;
    this.loginName = loginName;
    this.passwordHash = passwordHash;
    this.role = role;
    this.surname = surname;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public Object getEID() {
    return id;
  }

  @Override
  public String toString() {
    return firstName+" "+surname+" ("+loginName+")";
  }
}
