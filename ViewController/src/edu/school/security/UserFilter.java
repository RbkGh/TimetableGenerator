package edu.school.security;

import edu.school.jpa.User;

import java.io.IOException;

import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class UserFilter implements Filter {
  private FilterConfig _filterConfig = null;

  public void init(FilterConfig filterConfig) throws ServletException {
    _filterConfig = filterConfig;
  }

  public void destroy() {
    _filterConfig = null;
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                                   ServletException {
    HttpSession session = ((HttpServletRequest)request).getSession(false);
    User currentUser = (User)session.getAttribute("currentUser");
    if (currentUser == null) {
      Principal p = ((HttpServletRequest)request).getUserPrincipal();
      if (p != null) {
        session.setAttribute("currentPrincipal",p);
      } else {
        if ("true".equals(System.getProperty("DEVELOPMENT"))) {
          p = new Principal() {
            public String getName() { return "standa"; }
          };
          session.setAttribute("currentPrincipal",p);
        }
      }
    }
    chain.doFilter(request, response);
  }
}
