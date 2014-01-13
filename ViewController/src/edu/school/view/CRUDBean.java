package edu.school.view;

import edu.school.jpa.AbstractJpaEntity;
import edu.school.jpa.UserIdJpaEntity;
import edu.school.view.util.JsfUtil;
import edu.school.view.util.PaginationHelper;

import java.lang.reflect.Method;

import java.security.Principal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.LaunchEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;


public class CRUDBean {

  private Class jpaClass;
  private String jpaClassName;
  private JavaServiceFacade ejbFacade;
  protected UserIdJpaEntity current;
  protected PaginationHelper pagination;
  protected boolean readOnly;

  private DataModel items = null;
  private RichPopup tableEditPopup;
  private RichTable table;
  private RichPopup confirmDeletePopup;
  private RichPopup confirmRemovePopup;

  public CRUDBean() {
    super();
    ejbFacade = new JavaServiceFacade();
  }

  public CRUDBean(Class jpaClass) {
    this();
    this.jpaClass = jpaClass;
    jpaClassName = jpaClass.getName().substring(jpaClass.getName().lastIndexOf('.')+1);
  }

  public JavaServiceFacade getEjbFacade() {
    return getFacade();
  }

  public JavaServiceFacade getFacade() {
    return ejbFacade;
  }

  public AbstractJpaEntity getCurrent() {
    if (current == null) {
      try {
        current = (UserIdJpaEntity)jpaClass.newInstance();
      } catch (Exception e) {
        current = null;
      }
    }
    return current;
  }

  public void setCurrent(UserIdJpaEntity current) {
    this.current = current;
  }
  
  public PaginationHelper getPagination() {
      if (pagination == null) {
          pagination = new PaginationHelper(10) {
              @Override
              public int getItemsCount() {
                  return getFacade().count(jpaClass);
              }

              @Override
              public DataModel createPageDataModel() {
                return new ListDataModel(findAll());
//                  return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()},Teacher.class/*jpaClass*/));
              }
          };
      }
      return pagination;
  }

  private Integer getUserId() {
    FacesContext adfCtx = FacesContext.getCurrentInstance();
    Map<String,Object> session = adfCtx.getExternalContext().getSessionMap();
    Principal currentUser = (Principal)session.get("currentPrincipal");
    Object currentUserEntity = session.get("currentUser");
    if (currentUserEntity == null && currentUser != null) {
      currentUserEntity = ejbFacade.getEntityManager().createNamedQuery("User.findLoginName").
                setParameter("loginName", currentUser.getName()).getSingleResult();
    }
    Integer userId = null; 
    if (currentUserEntity != null) {
      try {
        Method getId = currentUserEntity.getClass().getDeclaredMethod("getId", new Class[0]);
        userId = (Integer)getId.invoke(currentUserEntity, new Object[0]);
      } catch (Exception e) {
        e.printStackTrace();
        userId = null;
      }
    }
    return userId;
  }
  
  /**
   * Inject userId into each query
   * @return
   */
  protected List findAll() {
    List list = null;
    Integer userId = getUserId(); 
    if (userId != null) {
      list = ejbFacade.getEntityManager().createNamedQuery(jpaClassName+".findAll").setParameter("userId", userId).getResultList();
    } else {
      list = new ArrayList<Object>(0);
    }
    return list;
  }

  public RichPopup getTableEditPopup() {
    return tableEditPopup;
  }

  public void setTableEditPopup(RichPopup tableEditPopup) {
    this.tableEditPopup = tableEditPopup;
  }

  public RichTable getTable() {
    return table;
  }

  public void setTable(RichTable table) {
    this.table = table;
  }

  public void saveAL(ActionEvent event) {
    AdfFacesContext adfCtx = AdfFacesContext.getCurrentInstance(); 
    String which = (String)adfCtx.getPageFlowScope().get("which");
    if ("add".equalsIgnoreCase(which)) {
      try {
        current.setUserId(getUserId()); // ensure current user is indicated as owner
        getFacade()._persistEntity(current);
        items = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,jpaClassName+" Added","Data for "+jpaClassName+" ID ["+current.getEID()+"] has been saved to database."));
      } catch (Exception e) {
        e.printStackTrace();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,jpaClassName+" Create Failed",jpaClassName+" has not been created due to: "+e.getMessage()));
      }
    } else if ("edit".equalsIgnoreCase(which)) {
      try {
        getFacade()._mergeEntity(current);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,jpaClassName+" Updated","Data for "+jpaClassName+" ID ["+current.getEID()+"] has been updated in database."));
      } catch (Exception e) {
        e.printStackTrace();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,jpaClassName+" Update Failed",jpaClassName+" ID ["+current.getEID()+"] has not been updated due to: "+e.getMessage()));
      }
    }
    if (table != null)
      RequestContext.getCurrentInstance().addPartialTarget(table);
    if (tableEditPopup != null)
      tableEditPopup.hide();
  }

  public void cancelAL(ActionEvent event) {
    if (tableEditPopup != null)
      tableEditPopup.hide();
  }
  
  
  public void addAL(ActionEvent actionEvent) {
    getCurrent();
    readOnly = false;
  }

  public void addLL(LaunchEvent launchEvent) {
    getCurrent();
    readOnly = false;
  }
  
  public UserIdJpaEntity getSelectedRow() {
    UserIdJpaEntity selected = null;
    if (table != null) {
      RowKeySet rowKeySet = table.getSelectedRowKeys();
      ListDataModel model = (ListDataModel)table.getValue();
      List aList = (List)(model).getWrappedData();
      if (rowKeySet != null) {
        Iterator it = rowKeySet.iterator();
        if (it.hasNext()) {
          Object firstKey = it.next();
          if (Integer.class.isAssignableFrom(firstKey.getClass())) {
            selected = (UserIdJpaEntity)aList.get((Integer)firstKey);
          }
        }
      }
      if (selected == null && aList != null && aList.size() > 0)
        selected = (UserIdJpaEntity)aList.get(0);
    }
    return selected;
  }

  public void editLL(LaunchEvent launchEvent) {
    current = getSelectedRow();
    readOnly = false;
  }

  public void editAL(ActionEvent actionEvent) {
    current = getSelectedRow();
    readOnly = false;
  }

  public void onConfirmDeleteClose(DialogEvent dialogEvent) {
      if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {
        current = getSelectedRow();
        if (current != null) {
          try {
            getFacade().remove(current,jpaClass);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,jpaClassName+" Deleted","Data for "+jpaClassName+" ID ["+current.getEID()+"] has been deleted from database."));
          } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,jpaClassName+" Delete Error","Data for "+jpaClassName+" ID ["+current.getEID()+"] has NOT been deleted from database due to: "+e.getMessage()));
            e.printStackTrace();
          }
        }
      } 
  }

  /**
   * Dialog to confirm removal from list w/o deleting the entity from database
   * @param dialogEvent
   */
  public void onConfirmRemoveClose(DialogEvent dialogEvent) {
      if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {
        current = getSelectedRow();
        if (current != null) {
          try {
            getFacade().remove(current,jpaClass);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,jpaClassName+" Removed",jpaClassName+" ID ["+current.getEID()+"] has been removed from list."));
          } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,jpaClassName+" Delete Error",jpaClassName+" ID ["+current.getEID()+"] has NOT been removed from the list due to: "+e.getMessage()));
            e.printStackTrace();
          }
        }
      } 
  }

  public DataModel getItems() {
      if (items == null) {
          items = getPagination().createPageDataModel();
      }
      return items;
  }

  public SelectItem[] getItemsAvailableSelectMany() {
    return JsfUtil.getSelectItems(findAll(), false);
  }

  public SelectItem[] getItemsAvailableSelectOne() {
    return JsfUtil.getSelectItems(findAll(), true);
  }

  public SelectItem[] getItemsAvailableSelectOneObject() {
    return JsfUtil.getSelectItems(findAll(), true);
  }


  protected void recreateModel() {
      items = null;
  }

  protected void recreatePagination() {
      pagination = null;
  }

  /**
   * @return the readOnly
   */
  public boolean isReadOnly() {
      return readOnly;
  }

  public Boolean getReadOnly() {
      return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }
  
  public void selectAL(ActionEvent actionEvent) {
    current = getSelectedRow();
    readOnly = true;
  }

  public void deleteAL(ActionEvent actionEvent) {
    current = getSelectedRow();
    if (confirmDeletePopup != null) {
      confirmDeletePopup.show(new RichPopup.PopupHints());
    }
  }

  public RichPopup getConfirmDeletePopup() {
    return confirmDeletePopup;
  }

  public void setConfirmDeletePopup(RichPopup confirmDeletePopup) {
    this.confirmDeletePopup = confirmDeletePopup;
  }

  public RichPopup getConfirmRemovePopup() {
    return confirmRemovePopup;
  }

  public void setConfirmRemovePopup(RichPopup confirmRemovePopup) {
    this.confirmRemovePopup = confirmRemovePopup;
  }

  public void selectPFL(PopupFetchEvent popupFetchEvent) {
    String source = popupFetchEvent.getLaunchSourceClientId();
    setReadOnly(source.contains("View"));
    if (!source.endsWith("Add")) {
      current = getSelectedRow();
    } else {
      current = null;
      getCurrent();
    }
  }

  public void onEditClose(DialogEvent dialogEvent) {
    // Add event code here...
    RequestContext.getCurrentInstance().addPartialTarget(table);
  }

}
