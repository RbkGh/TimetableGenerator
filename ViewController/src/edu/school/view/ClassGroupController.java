package edu.school.view;

import edu.school.jpa.ClassGroup;
import edu.school.jpa.ClassSubjectCount;
import edu.school.jpa.ClassSubjectCountPK;
import edu.school.jpa.Classroom;
import edu.school.jpa.Subject;

import edu.school.jpa.Teacher;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.event.PopupFetchEvent;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.LaunchEvent;

//import javax.faces.convert.FacesConverter;


public class ClassGroupController extends CRUDBean implements Serializable {

    private Subject subject;
    private String  subjectCount;
    private Integer classroomId;
  private RichPopup editSubjectPopup;
  private RichTable subjectPopupTable;

  public ClassGroupController() {
      super(ClassGroup.class);
    }

    public static ClassGroupController getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (ClassGroupController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "classGroupController");

    }
    
    public ClassGroup getSelected() {
      return (ClassGroup)super.getSelectedRow();
    }

  /*
   * Add teacher logic
   */
    @Override
    public void addLL(LaunchEvent launchEvent) {
      ClassGroup cg = (ClassGroup)super.getCurrent();
      AvailabilityController avcontroller = AvailabilityController.getInstance();
      readOnly = false;
      recreateModel();
      avcontroller.setOwner(cg);
      avcontroller.setReadOnly(readOnly);
      cg.setAvailabilityId(avcontroller.getMaxOwner()+1);
    }

    /*
     * View teacher logic
     */
    @Override
    public void selectAL(ActionEvent actionEvent) {
      ClassGroup cg = getSelected();
      AvailabilityController avcontroller = AvailabilityController.getInstance();
      avcontroller.setOwner(cg);
      avcontroller.setReadOnly(readOnly);
      if (cg.getAvailabilityId() == null)
        cg.setAvailabilityId(avcontroller.getMaxOwner()+1);
    }

    /*
     * Edit teacher
     */
    @Override
      public void editLL(LaunchEvent launchEvent) {
        super.editLL(launchEvent);
        ClassGroup cg = (ClassGroup)getCurrent();
        AvailabilityController avcontroller = AvailabilityController.getInstance();
        avcontroller.setOwner(cg);
        avcontroller.setReadOnly(readOnly);
        if (cg.getAvailabilityId() == null) {
          cg.setAvailabilityId(avcontroller.getMaxOwner()+1);
        }
      }

    public ClassGroup getClassGroup(java.lang.Integer id) {
        return (ClassGroup)getFacade().find(id,ClassGroup.class);
    }

    /**
     * @return the subjectCount
     */
    public String getSubjectCount() {
        return subjectCount;
    }

    /**
     * @param subjectCount the subjectCount to set
     */
    public void setSubjectCount(String subjectCount) {
        this.subjectCount = subjectCount;
    }

  public Integer getClassroomId() {
    if (current != null && ((ClassGroup)current).getClassroom() != null)
      classroomId = ((ClassGroup)current).getClassroom().getId();
    return classroomId;
  }

  public void setClassroomId(Integer classroomId) {
    this.classroomId = classroomId;
    if (current != null) {
      ClassGroup group = (ClassGroup)current;
      if (group.getClassroom() == null || 
          (group.getClassroom() != null && !classroomId.equals(group.getClassroom().getId()))) {
        Classroom room = (Classroom)getFacade().find(classroomId, Classroom.class);       
        group.setClassroom(room);
      }
    }
  }

  public void setEditSubjectPopup(RichPopup editSubjectPopup) {
    this.editSubjectPopup = editSubjectPopup;
  }

  public RichPopup getEditSubjectPopup() {
    return editSubjectPopup;
  }

  @Override
  public void saveAL(ActionEvent actionEvent) {
    super.saveAL(actionEvent);
  }

  public void saveSubjAL(ActionEvent actionEvent) {
    if (actionEvent.getComponent().getId().contains("Save")) {
      Map<String, Object> flowScope = ADFContext.getCurrent().getPageFlowScope();
      Subject selectedSubj = (Subject)subjectPopupTable.getSelectedRowData();
      String count = (String)flowScope.get("subjectCount");
      if (count != null) {
        try {
          selectedSubj.setCount(Integer.parseInt(count));
          getEjbFacade().setSubjectCount(getSelected(), selectedSubj, Integer.parseInt(count));
        } catch (Exception ignore) {}
      }
      RequestContext.getCurrentInstance().addPartialTarget(subjectPopupTable);
    }
    editSubjectPopup.hide();
  }

  public void setSubjectPopupTable(RichTable subjectPopupTable) {
    this.subjectPopupTable = subjectPopupTable;
  }

  public RichTable getSubjectPopupTable() {
    return subjectPopupTable;
  }

  //    @FacesConverter(forClass = ClassGroup.class)
    public static class ClassGroupControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ClassGroupController controller = (ClassGroupController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "classGroupController");
            return controller.getClassGroup(getKey(value));
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
            if (object instanceof ClassGroup) {
                ClassGroup o = (ClassGroup) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ClassGroup.class.getName());
            }
        }
    }

    public String addSubject() {
        int sbjCount = 1;
        ClassGroup cg = (ClassGroup)current;
        if (subjectCount != null) {
            try {
                sbjCount = Integer.parseInt(subjectCount);
            } catch (Exception e) {
                sbjCount = 1;
            }
        }
        if (subject != null && cg != null) {
            Collection<Subject> subjects = cg.getSubjectCollection();
            if (subjects == null) {
                subjects = new ArrayList<Subject>();
                cg.setSubjectCollection(subjects);
            }
            subject.setCount(sbjCount);
            subjects.add(subject);
            getFacade().setSubjectCount(cg, subject, sbjCount);
        }
        return null;
    }
    
    public String removeSubject() {
        String subjName = (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("subjectName");
        Subject toRemove = null;
      ClassGroup cg = (ClassGroup)current;
        for (Subject s : cg.getSubjectCollection()) {
            if (s.getName().equals(subjName)) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) {
            getFacade().removeSubjectCount(cg, toRemove);
            cg.getSubjectCollection().remove(toRemove);
        }
        return null;
    }
    
    public SelectItem[] getAvailableSubjects() {
        SelectItem[] list = null;
      ClassGroup cg = (ClassGroup)current;
        if (cg != null) {
            ArrayList<SelectItem> avail = new ArrayList<SelectItem>();
            SubjectController sc = SubjectController.getInstance();
            SelectItem[] all = sc.getItemsAvailableSelectOne();
            for (SelectItem s : all) {
                boolean hasSubject = false;
                if (cg.getSubjectCollection() != null) {
                    for (Subject subj : cg.getSubjectCollection()) {
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

  public void selectPFL(PopupFetchEvent popupFetchEvent) {
    super.selectPFL(popupFetchEvent);
    ClassGroup cg = getSelected();
    AvailabilityController avcontroller = AvailabilityController.getInstance();
    avcontroller.setOwner(cg);
    avcontroller.recreateModel();
    String source = popupFetchEvent.getLaunchSourceClientId();
    avcontroller.setReadOnly(source.contains("View"));
    if (cg.getAvailabilityId() == null) {
      cg.setAvailabilityId(avcontroller.getMaxOwner()+1);
    }
    Collection<Subject> cgSubjects = cg.getSubjectCollection();
    if (cgSubjects != null) {
      for (Subject s : cgSubjects) {
        ClassSubjectCount classSubjectCount = getEjbFacade().getSubjectCount(cg, s);
        if (classSubjectCount != null) {
          s.setCount(classSubjectCount.getSubject_count());
        }
      }
    }
  }

    /*
    public String getPreferredClassroomName() {
        ClassGroup cg = getSelected();
        if (cg.getPreferredClassroom() == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ClassroomController crcontroller = (ClassroomController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "classroomController");
            cg.setPreferredClassroom(crcontroller.getClassroom(cg.getPrefClassroom()));
        }
        return cg.getPreferredClassroom() != null ? cg.getPreferredClassroom().getName() : "n/a";
    }
    */
}
