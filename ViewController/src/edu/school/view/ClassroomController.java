package edu.school.view;

import edu.school.jpa.Classroom;
import edu.school.jpa.Subject;
import edu.school.view.util.JsfUtil;
import edu.school.view.util.PaginationHelper;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
//import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

public class ClassroomController extends CRUDBean implements Serializable {

    public ClassroomController() {
      super(Classroom.class);
    }

    public static ClassroomController getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (ClassroomController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "classroomController");
    }
    
    public Classroom getSelected() {
        return (Classroom)super.getSelectedRow();
    }

    public Classroom getCurrent() {
      return (Classroom)super.getCurrent();
    }
    
    private SelectItem[] classroomItems = null;
    @Override
    public SelectItem[] getItemsAvailableSelectOne() {
        if (classroomItems != null)
            return classroomItems;
        classroomItems = buildSelectItemList(false);
        return classroomItems;
    }
    
    @Override
  public SelectItem[] getItemsAvailableSelectOneObject() {
      return buildSelectItemList(true);
  }
  
    
    private SelectItem[] buildSelectItemList(boolean asObject) {
      List<Classroom> entities = getFacade().getClassroomFindAll();
      int size = entities.size() + 1;
      SelectItem[]  _classroomItems = new SelectItem[size];
      int i = 0;
      _classroomItems[i++] = new SelectItem("", "---");
      for (Classroom x : entities) {
        _classroomItems[i++] = new SelectItem(asObject ? x : x.getId(), x.getName());
      }
      return _classroomItems;
    }

    public Classroom getClassroom(java.lang.Integer id) {
        return (Classroom)getFacade().find(id,Classroom.class);
    }

  @Override
  public DataModel getItems() {
      DataModel items = super.getItems();
      List<Classroom> rooms = (List<Classroom>)items.getWrappedData();
      List<Subject> subjs = (List<Subject>)SubjectController.getInstance().getItems().getWrappedData();
      if (subjs != null && rooms != null) {
        for (Classroom room : rooms) {
          for (Subject subj : subjs) {
            if (subj.getId().equals(room.getPrefSubject())) {
              room.setPrefSubjectName(subj.getName());
              break;
            }
          }
        }
      }
      return items;
  }

  //@FacesConverter(forClass = Classroom.class)
    public static class ClassroomControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ClassroomController controller = (ClassroomController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "classroomController");
            Object x = controller.getClassroom(getKey(value));
            return x;
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Classroom) {
                Classroom o = (Classroom) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Classroom.class.getName());
            }
        }
    }
}
