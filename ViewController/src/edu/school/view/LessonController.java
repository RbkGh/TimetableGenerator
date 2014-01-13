package edu.school.view;

import edu.school.jpa.Lesson;
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

public class LessonController extends CRUDBean implements Serializable {

    public LessonController() {
      super(Lesson.class);
    }

    public static LessonController getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (LessonController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "lessonController");
    }
    
    public Lesson getSelected() {
        return (Lesson)super.getSelectedRow();
    }

    public Lesson getLesson(java.lang.Integer id) {
        return (Lesson)getFacade().find(id, Lesson.class);
    }


//    @FacesConverter(forClass = Lesson.class)
    public static class LessonControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LessonController controller = (LessonController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "lessonController");
            return controller.getLesson(getKey(value));
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
            if (object instanceof Lesson) {
                Lesson o = (Lesson) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Lesson.class.getName());
            }
        }
    }
}
