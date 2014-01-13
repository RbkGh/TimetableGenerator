package edu.school.view;

import edu.school.jpa.User;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;

public class UserController extends CRUDBean implements Serializable {

  public UserController() {
    super(User.class);
  }

  public String logout() {
    ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession(false).invalidate();
    return null;
  }
}
