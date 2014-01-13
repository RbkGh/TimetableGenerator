package edu.school.view;

import edu.school.jpa.Availability;
import edu.school.jpa.ClassGroup;
import edu.school.jpa.Lesson;
import edu.school.jpa.Teacher;
import edu.school.view.util.JsfUtil;
import edu.school.view.util.PaginationHelper;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
//import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.context.AdfFacesContext;

public class AvailabilityController extends CRUDBean implements Serializable {

    private Object owner;

    public AvailabilityController() {
      super(Availability.class);
    }

    public static AvailabilityController getInstance() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (AvailabilityController)facesContext.getApplication().getELResolver().
                                        getValue(facesContext.getELContext(), null, "availabilityController");
    }
    
    public Availability getSelected() {
      return (Availability)super.getSelectedRow();
    }

    public int getMaxOwner() {
        String ownerType = null;
        if (ClassGroup.class.isInstance(owner)) {
            ownerType = "CG";
        }
        if (Teacher.class.isInstance(owner)) {
            ownerType = "T";
        }
        return getFacade().getMaxOwner(ownerType, ControllerUtils.getCurrentUser());
    }
    
    public List<Availability> getAvailabilityByLesson(Object who) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String ownerId = null;
        String ownerType = null;
        if (who == null) {
            return null;
        }
        if (ClassGroup.class.isInstance(who)) {
            ownerId = ((ClassGroup)who).getAvailabilityId().toString();
            ownerType = "CG";
        }
        if (Teacher.class.isInstance(who)) {
            ownerId = ((Teacher)who).getAvailabilityId().toString();
            ownerType = "T";
        }
        List<Availability> availabilityList = getFacade().findByOwner(ownerId, ownerType, ControllerUtils.getCurrentUser());
        List<Lesson> lessons = LessonController.getInstance().findAll();
        for (Lesson l : lessons) {
            boolean hasLesson = false;
            for (Availability a : availabilityList) {
                hasLesson = a.getLesson().getId().equals(l.getId());
                if (hasLesson)
                    break;
            }
            if (!hasLesson) {
                Availability av = new Availability(ownerId);
                av.setLesson(l);
                av.setOwnerType(ownerType);
                getFacade().persistAvailability(av);
                availabilityList.add(av);
            }
        }
        Collections.sort(availabilityList,new Comparator() {@Override public int compare(Object a, Object b) {
            Integer ai = ((Availability)a).getLesson().getId();
            Integer bi = ((Availability)b).getLesson().getId();
            return ai.compareTo(bi);
        }});
        return availabilityList;
    }
    
    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count(Availability.class);
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getAvailabilityByLesson(owner));
//                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }


    public Availability getAvailability(java.lang.Integer id) {
        return (Availability)getFacade().find(id,Availability.class);
    }

    /**
     * @return the owner
     */
    public Object getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public void persistItems() {
      List<Availability> list = (List<Availability>)getItems().getWrappedData();
      if (list != null) {
        for (Availability a : list) {
          getFacade().mergeAvailability(a);
        }
      }
    }
    
  public void flipAL(ActionEvent actionEvent) {
    Map<String,Object> requestMap = ADFContext.getCurrent().getRequestScope();
    Object lessonIdx = requestMap.get("lessonIdx");
    String day = (String)requestMap.get("day");
    List<Availability> list = (List<Availability>)getItems().getWrappedData();
    if (list != null) {
      int idx = Integer.parseInt(lessonIdx.toString());
      if (idx >= 0 && idx < list.size()) {
        Availability a = list.get(idx);
        if ("Monday".equals(day))
          a.setMondayBool(!a.getMondayBool());
        else if ("Tuesday".equals(day))
          a.setTuesdayBool(!a.getTuesdayBool());
        else if ("Wednesday".equals(day))
          a.setWednesdayBool(!a.getWednesdayBool());
        else if ("Thursday".equals(day))
          a.setThursdayBool(!a.getThursdayBool());
        else if ("Friday".equals(day))
          a.setFridayBool(!a.getFridayBool());
        else if ("Saturday".equals(day))
          a.setSaturdayBool(!a.getSaturdayBool());
        else if ("Sunday".equals(day))
          a.setSundayBool(!a.getSundayBool());
      }
    }
  }


  //    @FacesConverter(forClass = Availability.class)
    public static class AvailabilityControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AvailabilityController controller = (AvailabilityController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "availabilityController");
            return controller.getAvailability(getKey(value));
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
            if (object instanceof Availability) {
                Availability o = (Availability) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Availability.class.getName());
            }
        }
    }
}
