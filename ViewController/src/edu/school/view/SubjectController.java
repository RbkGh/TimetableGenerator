package edu.school.view;

import edu.school.jpa.Subject;
import edu.school.view.util.JsfUtil;
import edu.school.view.util.PaginationHelper;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
//import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import oracle.adf.view.rich.event.DialogEvent;

public class SubjectController extends CRUDBean implements Serializable {

    public SubjectController() {
      super(Subject.class);
    }

    public static SubjectController getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (SubjectController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "subjectController");

    }
    
    public Subject getSelected() {
        return (Subject)super.getSelectedRow();
    }

    public Subject getSubject(java.lang.Integer id) {
        return (Subject)getFacade().find(id, Subject.class);
    }

  //    @FacesConverter(forClass = Subject.class)
    public static class SubjectControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SubjectController controller = (SubjectController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "subjectController");
            return controller.getSubject(getKey(value));
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
            if (object instanceof Subject) {
                Subject o = (Subject) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Subject.class.getName());
            }
        }
    }
}
