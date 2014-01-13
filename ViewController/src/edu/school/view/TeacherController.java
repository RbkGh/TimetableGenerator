package edu.school.view;

import edu.school.jpa.Subject;
import edu.school.jpa.Teacher;
import edu.school.view.util.JsfUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.rich.event.PopupFetchEvent;

import org.apache.myfaces.trinidad.event.LaunchEvent;


public class TeacherController extends CRUDBean implements Serializable {

  private Subject subject;

  public TeacherController() {
    super(Teacher.class);
  }
    
    public static TeacherController getInstance() {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            return (TeacherController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "teacherController");
    }
    
    public Teacher getSelected() {
      return (Teacher)super.getSelectedRow();
    }

/*
 * Add teacher logic
 */
  @Override
  public void addLL(LaunchEvent launchEvent) {
    Teacher teacher = getCurrent();
    AvailabilityController avcontroller = AvailabilityController.getInstance();
    readOnly = false;
    recreateModel();
    avcontroller.setOwner(teacher);
    avcontroller.setReadOnly(readOnly);
    teacher.setAvailabilityId(avcontroller.getMaxOwner()+1);
  }

  /*
   * View teacher logic
   */
  @Override
  public void selectAL(ActionEvent actionEvent) {
    Teacher teacher = getSelected();
    AvailabilityController avcontroller = AvailabilityController.getInstance();
    avcontroller.setOwner(teacher);
    avcontroller.setReadOnly(readOnly);
    if (teacher.getAvailabilityId() == null)
      teacher.setAvailabilityId(avcontroller.getMaxOwner()+1);
  }

  /*
   * Edit teacher
   */
  @Override
    public void editLL(LaunchEvent launchEvent) {
      super.editLL(launchEvent);
      Teacher teacher = getCurrent();
      AvailabilityController avcontroller = AvailabilityController.getInstance();
      avcontroller.setOwner(teacher);
      avcontroller.setReadOnly(readOnly);
      if (teacher.getAvailabilityId() == null) {
        teacher.setAvailabilityId(avcontroller.getMaxOwner()+1);
      }
    }

    public String addSubject() {
        if (subject != null && current != null) {
            Collection<Subject> subjects = ((Teacher)current).getSubjectCollection();
            if (subjects == null) {
                subjects = new ArrayList<Subject>();
                ((Teacher)current).setSubjectCollection(subjects);
            }
            subjects.add(subject);
        }
        return null;
    }
    
    public String removeSubject() {
        
        return null;
    }
    
    public SelectItem[] getAvailableSubjects() {
        SelectItem[] list = null;
        if (getCurrent() != null) {
            ArrayList<SelectItem> avail = new ArrayList<SelectItem>();
            FacesContext facesContext = FacesContext.getCurrentInstance();
            SubjectController sc = (SubjectController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "subjectController");
            SelectItem[] all = sc.getItemsAvailableSelectOne();
            for (SelectItem s : all) {
                boolean hasSubject = false;
                if (((Teacher)current).getSubjectCollection() != null) {
                    for (Subject subj : ((Teacher)current).getSubjectCollection()) {
                        if (subj.equals(s.getValue())) {
                            hasSubject = true;
                            break;
                        }
                    }
                }
                if (!hasSubject)
                    avail.add(s);
            }
            list = avail.toArray(new SelectItem[0]);
        }
        return list;
    }
    
    public Teacher getTeacher(java.lang.Integer id) {
        return (Teacher)getFacade().find(id, Teacher.class);
    }

    /**
     * @return the subject
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

  public Teacher getCurrent() {
    return (Teacher)super.getCurrent();
  }

  public void setCurrent(Teacher current) {
    this.current = current;
  }

  @Override
  public void selectPFL(PopupFetchEvent popupFetchEvent) {
    super.selectPFL(popupFetchEvent);
    Teacher teacher = getSelected();
    AvailabilityController avcontroller = AvailabilityController.getInstance();
    avcontroller.setOwner(teacher);
    avcontroller.recreateModel();
    String source = popupFetchEvent.getLaunchSourceClientId();
    avcontroller.setReadOnly(source.contains("View"));
    if (teacher.getAvailabilityId() == null) {
      teacher.setAvailabilityId(avcontroller.getMaxOwner()+1);
    }
  }

  public void saveAL(ActionEvent event) {
    // Save changes to Teacher
    super.saveAL(event);
    // Save changes to Availability
    AvailabilityController.getInstance().persistItems();
  }
  
  public void deletePFL(PopupFetchEvent popupFetchEvent) {
    // Add event code here...
  }

  //@FacesConverter(forClass = Teacher.class)
    public static class TeacherControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TeacherController controller = (TeacherController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "teacherController");
            return controller.getTeacher(getKey(value));
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
            if (object instanceof Teacher) {
                Teacher o = (Teacher) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Teacher.class.getName());
            }
        }
    }


  public void test() {
      Teacher t = new Teacher();
      t.setFirstName("Benny");
      t.setLastName("Hill");
      t.setAddress("Bingley lane, Sropshire");
      t.setCanSubstitute(Byte.valueOf("1"));
      t.setInitials("BH");
      t.setPhone("+353535");
      Subject s1 = new Subject();
      s1.setName("Engineering");
      s1.setShortName("Eng");
      FacesContext facesContext = FacesContext.getCurrentInstance();
      SubjectController sc = (SubjectController) facesContext.getApplication().getELResolver().
              getValue(facesContext.getELContext(), null, "subjectController");
      getFacade().persistSubject(s1);
      ArrayList<Subject> tsa = new ArrayList<Subject>();
      tsa.add(s1);
      t.setSubjectCollection(tsa);
      getFacade().persistTeacher(t);
  }
}
