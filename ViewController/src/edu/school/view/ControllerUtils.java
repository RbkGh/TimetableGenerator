package edu.school.view;

import edu.school.jpa.User;

import java.util.Map;

import javax.faces.context.FacesContext;

public class ControllerUtils {
  public ControllerUtils() {
    super();
  }
  
  public static User getCurrentUser() {
    User currentUser = null;
    Map<String,Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    currentUser = (User)session.get("currentUser");
    if (currentUser == null) {
      currentUser = new User("guest",1,"guest", "guest", "guest", "guest");
    }
    return currentUser;
  }
}
